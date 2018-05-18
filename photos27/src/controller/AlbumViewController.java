package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Album;
import model.Photo;
import model.User;

/**
 * AlbumViewController is the class for controlling the Album Page. The
 * respective view is album.fxml This class is used for viewing all of the
 * thumbnails of each photo in the album The user can: View a photo, copy a
 * photo, move a photo, delete a photo
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class AlbumViewController {

	@FXML
	private Button logout;

	@FXML
	private Button home;

	@FXML
	private Button add;

	@FXML
	private Button next;

	@FXML
	private Button previous;

	@FXML
	private ListView<Photo> albumDisplay;

	@FXML
	private Label albumName;

	private ObservableList<Photo> obsList;
	private Album album;
	private User user;

	private int count = 0;
	private boolean moved;
	private int fileChoosers = 0;
	private Scene scene;
	private Stage stage;

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
	public void initialize(Album a, User u, Scene scene, Stage stage) throws IOException {
		this.moved = false;
		albumName.setText(a.getName());
		this.scene = scene;
		this.user = u;
		this.stage = stage;
		this.album = a;

		if (count == 0) {
			absoluteFilePath += user.getUserName() + File.separator + album.getName() + File.separator;
		}
		count++;

		File file = new File(absoluteFilePath);

		file.mkdirs();

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
	 * Takes a photo from a file chooser and attempts to add it to an album If
	 * the photo can be added, it will add the photo to the album and rewrite
	 * the user
	 * 
	 * @param event
	 *            The Add User button is pressed * @throws IOException Exception
	 *            for in the event a file doesn't exist/errors with
	 *            files/serializing a user
	 */
	public void addPhoto(ActionEvent e) throws IOException {

		EventHandler<MouseEvent> clicked = (t -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("Please finish choosing a file first.");
			alert.setContentText("Plese finish choosing a file to continue.");
			alert.showAndWait();
		});

		scene.addEventFilter(MouseEvent.MOUSE_PRESSED, clicked);

		if (fileChoosers > 0) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("You can only open one filechoose at a time.");
			alert.setContentText("Finish your open session to add another photo.");
			alert.showAndWait();
			scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
			return;

		}

		fileChoosers++;

		FileChooser fileChooser = new FileChooser();
		
		stage.setOnCloseRequest(event -> {
			System.exit(0);
		});
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
		fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

		File file = fileChooser.showOpenDialog(null);

		if (file == null) {
			fileChoosers--;
			scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
			return;
		}

		File destFile = new File(absoluteFilePath + file.getName());

		if (!file.exists()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("That photo doesn't exists in the source");
			alert.setContentText("Please select a new photo");
			alert.showAndWait();
			fileChoosers--;
			addPhoto(e);
			scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
			return;
		}

		if (!destFile.exists()) {
			try {
				destFile.createNewFile();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("That photo already exists in this album");
			alert.setContentText("Please select a new photo");
			alert.showAndWait();

			fileChoosers--;
			addPhoto(e);
			scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
			return;

		}

		for (Photo p : album.getPhotolist()) {

			if (p.getSource().equals(file.getPath())) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Add Error");
				alert.setHeaderText("That photo already exists in this album");
				alert.setContentText("Please select a new photo");
				alert.showAndWait();

				fileChoosers--;
				addPhoto(e);
				scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
				return;

			}

		}
		FileChannel source = null;
		FileChannel destination = null;

		try {

			source = new FileInputStream(file).getChannel();

			destination = new FileOutputStream(destFile).getChannel();

			if (destination != null && source != null) {
				destination.transferFrom(source, 0, source.size());
			}

		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		finally {
			if (source != null) {
				try {
					source.close();
				} catch (IOException e4) {
					e4.printStackTrace();
				}
			}
			if (destination != null) {
				try {
					destination.close();
				} catch (IOException e5) {
					e5.printStackTrace();
				}
			}
		}
		fileChoosers--;

		Date date = new Date(file.lastModified());
		Photo newPhoto = new Photo(absoluteFilePath + file.getName(), user, album, date);
		newPhoto.setSource(file.getAbsolutePath());

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
		scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, clicked);
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
		pvc.initialize(photo, user, album, false);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		stage.setScene(scene);
		stage.show();

	}

	/**
	 * Deletes a photo when the delete button is pressed on the cell factory
	 * 
	 * @param event
	 *            The Delete button is pressed * @throws IOException Exception
	 *            for serializing a user
	 */
	public void deletePhoto(ActionEvent event, Photo photo) throws IOException {

		if (!moved) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Are You Sure?");
			alert.setHeaderText("Do you really want to delete this photo?");
			alert.setContentText("Press Ok to delete the photo, or Cancel to keep it.");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {

				album.deletePhoto(photo);

				user.write(user);

				initialize(album, user, scene, stage);

			} else {

				return;
			}
		} else {
			album.deletePhoto(photo);

			user.write(user);

			moved = false;

			initialize(album, user, scene, stage);

		}

	}

	/**
	 * This will log the user out and take them to the login page
	 * 
	 * @param event
	 *            The logout button is pressewd
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

					if (result.get().trim().equals(album.getName())) {

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Copy Error");
						alert.setHeaderText("We can't copy to the same album");
						alert.setContentText(result.get() + " Already has that picture");
						alert.showAndWait();

						copyPhoto(event, photo);
						return;

					}

					if (a.getName().equals(result.get().trim())) {

						File from = new File(absoluteFilePath + photo.getName());

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
	 * This will move a photo from one album to another Moving a photo will
	 * delete it in the directroy that it was moved from
	 * 
	 * @param event
	 *            The Move button is pressed
	 * @throws IOException
	 *             Exception for serializing the user
	 */
	public void movePhoto(ActionEvent event, Photo photo) throws IOException {
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

					if (result.get().trim().equals(album.getName())) {

						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Move Error");
						alert.setHeaderText("We can't move to the same album");
						alert.setContentText(result.get() + " Already has that picture");
						alert.showAndWait();

						movePhoto(event, photo);
						return;

					}

					if (a.getName().equals(result.get().trim())) {

						File from = new File(absoluteFilePath + photo.getName());

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
							moved = true;

							deletePhoto(event, photo);

							user.write(this.user);

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Success!");
							alert.setHeaderText("Your Photo Was Moved!");
							alert.setContentText("Your Photo Was Moved Succesfully.");
							alert.showAndWait();

							return;
						} else {

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Copy Error");
							alert.setHeaderText("That Photo Exists Already In The Target Album");
							alert.setContentText(result.get() + " Already has that picture");
							alert.showAndWait();

							movePhoto(event, photo);
							return;
						}

					}

					index++;

				}

			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Move Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("There are no albums with the name " + result.get());
			alert.showAndWait();

			movePhoto(event, photo);
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

		Button delete = new Button("Delete");
		Button view = new Button("View");
		Button move = new Button("Move");
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

			AnchorPane.setRightAnchor(delete, 0.0);
			AnchorPane.setBottomAnchor(delete, 0.0);

			AnchorPane.setLeftAnchor(copy, 115.0);
			AnchorPane.setBottomAnchor(copy, 0.0);

			AnchorPane.setRightAnchor(move, 70.0);
			AnchorPane.setBottomAnchor(move, 0.0);

			AnchorPane.setLeftAnchor(view, 55.0);
			AnchorPane.setBottomAnchor(view, 0.0);

			delete.setVisible(false);
			copy.setVisible(false);
			move.setVisible(false);
			view.setVisible(false);

			anchorpane.getChildren().addAll(stackpane, caption, delete, copy, move, view);

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
				delete.setVisible(false);
				copy.setVisible(false);
				move.setVisible(false);
				view.setVisible(false);
			}
			if (photo != null) {

				File file = new File(absoluteFilePath + photo.getName() + File.separator);

				Image image = null;
				try {
					image = new Image(file.toURI().toURL().toExternalForm());
					if (image.isError()) {
						throw (image.getException());
					}
				} catch (IOException e1) {

					file = new File(workingDirectory + File.separator + "images" + File.separator + "removed.jpg");
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
				delete.setVisible(true);
				copy.setVisible(true);
				move.setVisible(true);
				view.setVisible(true);

				delete.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							deletePhoto(e, photo);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

				move.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							movePhoto(e, photo);

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
