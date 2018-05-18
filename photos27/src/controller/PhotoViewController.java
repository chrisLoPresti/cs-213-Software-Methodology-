package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.User;

/**
 * PhotoViewController is the class for controlling how a user displays a photo.
 * This class allows the user to view an individual photo close up
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class PhotoViewController {

	@FXML
	private Button logout;

	@FXML
	private Button addTags;

	@FXML
	private ImageView myImageView;

	@FXML
	private Label tags;

	@FXML
	private Label date;

	@FXML
	private Label albumLabel;

	@FXML
	private Label photoName;

	@FXML
	private Button next;

	@FXML
	private Button previous;

	@FXML
	private Button removeTags;

	@FXML
	private Button editCaption;

	@FXML
	private TextArea caption;

	@FXML
	private Button changeName;
	@FXML
	private Button back;

	private Album album;
	private Photo photo;
	private User user;
	private boolean searching;

	public String workingDirectory = System.getProperty("user.dir");
	public String absoluteFilePath = workingDirectory + File.separator + "images" + File.separator;

	/**
	 * loads in a photo and displays its content
	 * 
	 * @throws IOException
	 *             Exception for files not existing
	 */
	public void initialize(Photo p, User u, Album a, Boolean searching) throws IOException {
		
		this.searching = searching;

		tags.setText("Tags:");
		this.user = u;

		this.photo = p;

		this.album = a;

		try {

			File file = new File(absoluteFilePath + u.getUserName() + File.separator + p.getAlbumName() + File.separator
					+ photo.getName());

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

			myImageView.setImage(image);
			if (p.getTags() != null) {

				for (String t : p.getTags()) {

					tags.setText(tags.getText() + "  " + t + "  ");

				}
			}
			caption.setText(p.getCaption());

			photoName.setText(p.getName());

			albumLabel.setText(a.getName());

			date.setText(p.getDate());

		} catch (Exception e) {

			return;
		}
	}

	/**
	 * loads the next photo in the album or loops to the begging/end
	 * 
	 * @param event
	 *            > button pressed
	 * @throws IOException
	 *             Exception for switching photos
	 */
	public void next(ActionEvent e) throws IOException {

		int index = 0;

		for (Photo p : album.getPhotolist()) {

			if (p.equals(photo)) {

				if (index + 1 >= album.getPhotolist().size()) {

					index = 0;

				} else {
					index++;

				}

				initialize(album.getPhotolist().get(index), user, album, searching);

				return;

			}
			index++;
		}

	}

	/**
	 * loads the previous photo in the album or loops to the begging/end
	 * 
	 * @param event
	 *            < button pressed
	 * @throws IOException
	 *             Exception for switching photos
	 */
	public void previous(ActionEvent e) throws IOException {

		int index = 0;

		for (Photo p : album.getPhotolist()) {

			if (p.equals(photo)) {

				if (index - 1 < 0) {

					index = album.getPhotolist().size() - 1;

				} else {
					index--;

				}

				initialize(album.getPhotolist().get(index), user, album, searching);

				return;

			}
			index++;
		}

	}

	/**
	 * Adds tags to the photo
	 * 
	 * @param event
	 *            Add Tags button pressed
	 * @throws IOException
	 *             Exception for adding tags
	 */
	public void addTags(ActionEvent e) throws IOException {

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Add New Tag");

		ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(20, 80, 10, 10));

		TextField from = new TextField();
		from.setPromptText("Prefix");
		TextField to = new TextField();
		to.setPromptText("value");

		gridPane.add(from, 2, 0);
		gridPane.add(new Label("Prefix"), 0, 0);
		gridPane.add(new Label("Value"), 4, 0);
		gridPane.add(to, 6, 0);

		dialog.getDialogPane().setContent(gridPane);

		Platform.runLater(() -> from.requestFocus());

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(from.getText(), to.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(pair -> {
			String prefix = pair.getKey();
			String value = pair.getValue();

			if (value.equals("") || prefix.equals("")) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Add Error");
				alert.setHeaderText("Tag values can not be blank!");
				alert.setContentText("Please use valid information.");
				alert.showAndWait();
				try {
					addTags(e);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return;

			}

			try {
				checkValues(prefix, value, e);

				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		});
		
	}

	/**
	 * Checks to make sure you are not adding a duplicate tag
	 * 
	 * @throws IOException
	 *             Exception for invalid information
	 */
	public void checkValues(String prefix, String value, ActionEvent e) throws IOException {

		if (this.photo.getTags().size() == 0) {
			photo.addTags(prefix + ":" + value);
			tags.setText(tags.getText() + "  " + prefix + ":" + value + "  ");
			user.write(user);
			return;

		}

		for (int i = 0; i < this.photo.getTags().size(); i++) {

			if (this.photo.getTags().get(i).equals(prefix + ":" + value)) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Remove Error");
				alert.setHeaderText("Invalid entry information");
				alert.setContentText("That tag exists. Please try a unique tag.");
				alert.showAndWait();

				addTags(e);

				return;

			}

		}

		photo.addTags(prefix + ":" + value);

		user.write(user);
		tags.setText(tags.getText() + "  " + prefix + ":" + value + "  ");
		return;

	}

	/**
	 * Removes tags from a photo
	 * 
	 * @param event
	 *            Remove Tags button pressed
	 * @throws IOException
	 *             Exception for serializing the user
	 */
	public void removeTags(ActionEvent e) throws IOException {

		String tag = "";

		TextInputDialog dialog = new TextInputDialog("RemoveTag");
		dialog.setTitle("Remove A Tag");
		dialog.setHeaderText("Remove A Tag in the format \"name:value\"");
		dialog.setContentText("Please enter your tag:");
		final Button cancel = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
		cancel.addEventFilter(ActionEvent.ACTION, event -> {
			return;
		});

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			tag = result.get().trim();

			if (tag.equals("RemoveTag") || tag.equals("")) {

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Remove Error");
				alert.setHeaderText("Invalid entry information");
				alert.setContentText("Please use valid information.");
				alert.showAndWait();

				removeTags(e);

				return;

			}

			List<String> temp = photo.getTags();
			int index = 0;
			for (String s : temp) {
				s = s.trim();
				if (s.equals(tag)) {

					photo.getTags().remove(index);
					tags.setText("Tags:");

					for (String t : photo.getTags()) {

						tags.setText(tags.getText() + "  " + t + "  ");

					}

					user.write(user);

					return;

				}
				index++;
			}

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Remove Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("That Tag Doesn't Exists.");
			alert.showAndWait();

			removeTags(e);

			return;
		}

	}

	/**
	 * Adds/edits a caption belonging to the photo
	 * 
	 * @param event
	 *            Edit Caption button pressed
	 * @throws IOException
	 *             Exception for serializing the user
	 */
	public void editCaption(ActionEvent e) throws IOException {

		caption.setText(caption.getText());

		photo.setCaption(caption.getText());

		user.write(user);

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Success!");
		alert.setHeaderText("Your Caption Was Updated!");
		alert.setContentText("Your Caption Was Updated Succesfully.");
		alert.showAndWait();

		return;

	}

	/**
	 * Changes the name of a photo
	 * 
	 * @param event
	 *            Change Name button pressed
	 * @throws IOException
	 *             Exception for serializing the user
	 */
	public void changeName(ActionEvent e) throws IOException {
		int count = 0;
		int index = 0;
		TextInputDialog dialog = new TextInputDialog(
				photoName.getText().substring(0, photoName.getText().length() - 4));
		dialog.setTitle("Change Photo name");
		dialog.setHeaderText("Change the name of your photo");
		dialog.setContentText("Please enter your new name:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {

			for (Photo p : album.getPhotolist()) {

				if (p.equals(photo)) {

					index = count;
				}

				if (p.getName().substring(0, p.getName().length() - 4).equals(result.get())) {

					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Change Name Error");
					alert.setHeaderText("A photo already exists in this album with that name");
					alert.setContentText("Please try a new name");
					alert.showAndWait();

					changeName(e);

					return;
				}

				count++;
			}

			List<Photo> temp = album.getPhotolist();

			photo.changeName(result.get());

			temp.set(index, photo);

			photoName.setText(photo.getName());

			user.write(user);

			initialize(photo, user, album, searching);

		}

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
	 * This will log the user out and take them to the login page
	 * 
	 * @param event
	 *            back button pressed
	 * @throws IOException
	 *             Exception for loading a new scene and showing a new stage
	 * @throws IOException
	 *             Exception for a controller not being present
	 */
	public void goBack(ActionEvent event) throws IOException, ClassNotFoundException {

		if (!searching) {
			Parent parent;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
			parent = (Parent) loader.load();

			AlbumViewController avc = loader.getController();
			Scene scene = new Scene(parent);
			
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			avc.initialize(album, user,scene,stage);
			stage.setScene(scene);
			stage.show();
		} else {

			Parent parent;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResult.fxml"));
			parent = (Parent) loader.load();

			SearchResultController src = loader.getController();
			Scene scene = new Scene(parent);
			src.initialize(album, user);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(scene);
			stage.show();

		}
	}

}
