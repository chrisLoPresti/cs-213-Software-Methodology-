package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.User;

/**
 * SearchResultController is the class for controlling how search results are
 * displayed This class allows the user to view an individual photo close up
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class SearchResultController {

	@FXML
	private Button logout;

	@FXML
	private Button home;

	@FXML
	private Button create;

	@FXML
	private Button next;

	@FXML
	private Button previous;

	@FXML
	private Label albumName;

	@FXML
	private ListView<Photo> albumDisplay;

	private ObservableList<Photo> obsList;
	private Album album;
	private User user;

	public String workingDirectory = System.getProperty("user.dir");
	public String absoluteFilePath = workingDirectory + File.separator + "images" + File.separator;
	public final String incompleteFilePath = workingDirectory + File.separator + "images" + File.separator;

	/**
	 * Attemps to load all images from the image directory, and populate a
	 * listview with cell factories that hold options pertain to photos Once the
	 * listview is populated a user can interact with each cell factory A user
	 * can view a photo, copy a photo, move a photo, and delete a photo through
	 * the buttons displayed in each cell factoy
	 * 
	 * @param mainStage
	 *            The main stage * @throws IOException Exception for loading in
	 *            photos
	 */
	public void initialize(Album a, User u) throws IOException {

		albumName.setText(a.getName());
		this.user = u;

		this.album = a;

		List<Photo> temp = album.getPhotolist();

		obsList = FXCollections.observableArrayList(temp);

		albumDisplay.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {

			@Override
			public ListCell<Photo> call(ListView<Photo> p) {

				return new Cell();
			}
		});

		albumDisplay.setItems(obsList);

	}

	/**
	 * Loads the PhotoViewController and the photo.fxml gui
	 * 
	 * @param event
	 *            The View button is pressed * @throws IOException Exception for
	 *            in the event a file doesnt exist/errors with files/serializing
	 *            a user
	 */
	public void viewPhoto(ActionEvent event, Photo photo) throws IOException {

		Parent parent;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo.fxml"));
		parent = (Parent) loader.load();

		PhotoViewController pvc = loader.getController();
		Scene scene = new Scene(parent);
		pvc.initialize(photo, user, album, true);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		stage.setScene(scene);
		stage.show();

	}

	/**
	 * This removes a photo from the search results
	 * 
	 * @param event
	 *            The Remove button is pressed * @throws IOException Exception
	 *            for serializing a user
	 */
	public void removePhoto(ActionEvent event, Photo photo) throws IOException {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are You Sure?");
		alert.setHeaderText("Do you really want to remove this photo?");
		alert.setContentText("This will remove the photo from search results.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			album.getPhotolist().remove(photo);
			album.setSize(album.getSize() - 1);

			user.write(user);

			initialize(album, user);

		} else {

			return;
		}

	}

	/**
	 * Creates an album from the search results
	 * 
	 * @param event
	 *            The Create Album From Results button is pressed * @throws
	 *            IOException Exception for serializing a user
	 */
	public void createAlbum(ActionEvent event) throws IOException {

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add A New Album");
		dialog.setHeaderText("Add A New Album!");
		dialog.setContentText("Please enter your album name:");
		final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancel.addEventFilter(ActionEvent.ACTION, e -> {
			return;
		});

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent() && !result.get().trim().equals("")) {

			String name = result.get().trim();

			if (name.equals("") || name.contains("-")) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Add Error");
				alert.setHeaderText("Invalid entry information");
				alert.setContentText("Names can not be blank or contain a \"-\"");
				alert.showAndWait();

				createAlbum(event);
				return;
			}

			for (Album a : user.getAlbum()) {

				if (name.equals(a.getName())) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Add Error");
					alert.setHeaderText("An Album Exists With That Name");
					alert.setContentText("Please enter a new name");
					alert.showAndWait();

					createAlbum(event);
					return;
				}
			}

			absoluteFilePath += user.getUserName() + File.separator + name + File.separator;

			File dir = new File(absoluteFilePath);

			dir.mkdirs();

			int count = 0;
			Album tempAlbum = new Album(name);
			for (Photo p : album.getPhotolist()) {

				File file = new File(p.absoluteFilePath+p.getName());

				tempAlbum.setSize(count);
				addPhoto(file, tempAlbum, p);
				count++;

			}

			System.out.println("nope");
			user.addAlbum(tempAlbum);
			tempAlbum.setName(name, user);

			user.write(user);

			goHome(event);

		} else {

			return;
		}

	}

	/**
	 * This is used to add photos to the newely created album
	 * 
	 * @param event
	 *            The Add Photo button is pressed * @throws IOException
	 *            Exception for serializing a user
	 */
	public void addPhoto(File file, Album album, Photo p) throws IOException {

		File to = new File(absoluteFilePath + p.getName());
		Files.copy(file.toPath(), to.toPath());
		Date date = new Date(file.lastModified());
		Photo newPhoto = new Photo(absoluteFilePath + p.getName(), user, album, date);
		newPhoto.setDate(p.getDate());
		newPhoto.setCaption(p.getCaption());
		newPhoto.setTags(p.getTags());
		newPhoto.setSource(p.getSource());

		album.addPhoto(newPhoto);

		if (album.getSize() == 1) {

			album.setEarliestPhoto(newPhoto);
			album.setLatestPhoto(newPhoto);
		} else {

			album.setRange();

		}

		List<Photo> temp = album.getPhotolist();

		obsList = FXCollections.observableArrayList(temp);

		albumDisplay.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {

			@Override
			public ListCell<Photo> call(ListView<Photo> p) {

				return new Cell();
			}
		});

		albumDisplay.setItems(obsList);

		user.write(this.user);

	}

	/**
	 * This will copy a photo from one album to another
	 * 
	 * @param event
	 *            The Coppy button is pressed
	 * @throws IOException
	 *             Exception for serializing the user
	 */
	public void copyPhoto(ActionEvent event, Photo photo) throws IOException {
		try {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Which Album Is The Target?");
			dialog.setHeaderText("Enter A Target Album");
			dialog.setContentText("Please enter your album name:");
			final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
			cancel.addEventFilter(ActionEvent.ACTION, e -> {
				return;
			});

			Optional<String> result = dialog.showAndWait();

			if (result.isPresent() && !result.get().trim().equals("")) {

				int index = 0;
				for (Album a : user.getAlbum()) {

					if (a.getName().equals(result.get().trim())) {

						File from = new File(absoluteFilePath + user.getUserName() + File.separator
								+ photo.getAlbum().getName() + File.separator + photo.getName());
						File to = new File(incompleteFilePath + user.getUserName() + File.separator + a.getName()
								+ File.separator + photo.getName());

						if (!to.exists()) {
							Files.copy(from.toPath(), to.toPath());
							Date date = new Date(from.lastModified());
							Photo newPhoto = new Photo(incompleteFilePath + user.getUserName() + File.separator
									+ a.getName() + File.separator + photo.getName(), user, user.getAlbum().get(index),
									date);
							newPhoto.setDate(photo.getDate());
							newPhoto.setCaption(photo.getCaption());
							newPhoto.setTags(photo.getTags());
							newPhoto.setSource(photo.getSource());

							a.addPhoto(newPhoto);

							if (a.getSize() == 1) {

								a.setEarliestPhoto(newPhoto);
								a.setLatestPhoto(newPhoto);
							} else {

								a.setRange();

							}

							user.write(this.user);

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Success!");
							alert.setHeaderText("Your Photo Was Coppied!");
							alert.setContentText("Your Photo Was Coppied Succesfully.");
							alert.showAndWait();

							return;
						} else {

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Copy Error");
							alert.setHeaderText("That Photo Exists Already In The Target Album");
							alert.setContentText(result.get() + " Already has that picture");
							alert.showAndWait();

							copyPhoto(event, photo);
							return;
						}

					}
					index++;
				}

			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Copy Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("There are no albums with the name " + result.get());
			alert.showAndWait();

			copyPhoto(event, photo);
			return;
		} catch (Exception e) {

			return;
		}

	}

	/**
	 * This will take the user back to the user home which is controlled by the
	 * UserController and the user.fxml gui
	 * 
	 * @param event
	 *            The Home button is pressed
	 * @throws IOException
	 *             Exception for being unable to load user.fxml
	 */
	public void goHome(ActionEvent e) throws IOException {

		Parent parent;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user.fxml"));
		parent = (Parent) loader.load();

		UserController usercontroller = loader.getController();
		Scene scene = new Scene(parent);
		usercontroller.initialize(user);
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

		stage.setScene(scene);
		stage.show();
	}

	/**
	 * This will log the user out and take them to the login page
	 * 
	 * @param event
	 *            The logout button is pressed
	 * @throws IOException
	 *             Exception for loading a new scene and showing a new stage
	 * @throws IOException
	 *             Exception for a controller not being present
	 */
	public void logout(ActionEvent event) throws IOException, ClassNotFoundException {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are You Sure?");
		alert.setHeaderText("Do you really want to logout?");
		alert.setContentText("Press Ok to logout, or Cancel to stay.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			Parent parent;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
			parent = (Parent) loader.load();

			LoginController logincontroller = loader.getController();
			Scene scene = new Scene(parent);
			logincontroller.initialize();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(scene);
			stage.show();

		} else {

			return;

		}

	}

	/**
	 * This is an inner class that creates the cell factories for the album view
	 * page.
	 * 
	 * @param event
	 *            A photo is added or the initialize method is called
	 * @throws IOException
	 *             Throws multiple exceptions in the event the buttons are not
	 *             able to carry out their actions
	 */
	class Cell extends ListCell<Photo> {

		AnchorPane anchorpane = new AnchorPane();
		StackPane stackpane = new StackPane();

		ImageView viewImage = new ImageView();
		Label caption = new Label();

		Button remove = new Button("Remove");
		Button view = new Button("View");

		Button copy = new Button("Copy");

		public Cell() {
			super();

			viewImage.setFitWidth(45.0);
			viewImage.setFitHeight(45.0);
			viewImage.setPreserveRatio(true);

			StackPane.setAlignment(viewImage, Pos.CENTER);

			stackpane.getChildren().add(viewImage);

			stackpane.setPrefHeight(55.0);
			stackpane.setPrefWidth(45.0);

			AnchorPane.setLeftAnchor(stackpane, 0.0);

			AnchorPane.setLeftAnchor(caption, 55.0);
			AnchorPane.setTopAnchor(caption, 0.0);

			AnchorPane.setRightAnchor(remove, 0.0);
			AnchorPane.setBottomAnchor(remove, 0.0);

			AnchorPane.setRightAnchor(copy, 75.0);
			AnchorPane.setBottomAnchor(copy, 0.0);

			AnchorPane.setRightAnchor(view, 135.0);
			AnchorPane.setBottomAnchor(view, 0.0);

			remove.setVisible(false);
			copy.setVisible(false);

			view.setVisible(false);

			anchorpane.getChildren().addAll(stackpane, caption, remove, copy, view);

			anchorpane.setPrefHeight(55.0);

			caption.setMaxWidth(300.0);

			setGraphic(anchorpane);
		}

		@Override
		public void updateItem(Photo photo, boolean empty) {

			super.updateItem(photo, empty);
			setText(null);
			if (photo == null) {
				viewImage.setImage(null);
				caption.setText("");
				remove.setVisible(false);
				copy.setVisible(false);

				view.setVisible(false);
			}
			if (photo != null) {

				File file = new File(absoluteFilePath + user.getUserName() + File.separator + photo.getAlbum().getName()
						+ File.separator + photo.getName() + File.separator);

				Image image = null;
				try {	
					image = new Image(file.toURI().toURL().toExternalForm());
					if(image.isError()){throw(image.getException());}
				} catch (IOException e1) {
				
					 file = new File(workingDirectory + File.separator + "images" + File.separator+"removed.jpg");
					try {
						image = new Image(file.toURI().toURL().toExternalForm());
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}


				viewImage.setImage(image);
				caption.setText("Caption: " + photo.getCaption());
				remove.setVisible(true);
				copy.setVisible(true);

				view.setVisible(true);

				remove.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							removePhoto(e, photo);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				copy.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							copyPhoto(e, photo);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				view.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							viewPhoto(e, photo);

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
			}

		}
	}
}
