package controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.Album;
import model.Photo;
import model.User;

/**
 * UserController is the class controlling the user main page Allows the user
 * access to their albums
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class UserController {

	@FXML
	private ListView<String> albumListView;

	@FXML
	private TextField newName;

	@FXML
	private Button addUser;

	@FXML
	private Button viewalbum;

	@FXML
	private Button rename;

	@FXML
	private Label selectedAlbum;

	@FXML
	private Label selectedOldest;

	@FXML
	private Label oldest;

	@FXML
	private Label albumName;

	@FXML
	private Label listSize;

	@FXML
	private Button delete;

	@FXML
	private Button tagSearch;

	@FXML
	private Button dateSearch;
	@FXML
	private DatePicker dateFrom;
	@FXML
	private DatePicker dateTo;

	@FXML
	private Button logout;

	@FXML
	private Button clearNew;

	@FXML
	private Label newest;

	@FXML
	private Label totalPhotos;

	@FXML
	private Label selectedYoungest;

	@FXML
	private Label selectedTotal;
	@FXML
	private Label welcomeName;

	private List<String> listOfAlbums;
	private List<Album> albumList;

	private User user;

	public ObservableList<String> obsList;

	public List<String> tags;

	public static final String workingDirectory = System.getProperty("user.dir");
	public String absoluteFilePath = workingDirectory + File.separator + "images" + File.separator;

	/**
	 * Attemps to load all albums into a listview
	 * 
	 * @param mainStage
	 *            The main stage * @throws IOException Exception for loading in
	 *            albums
	 */
	public void initialize(User u) {

		try {
			
			
			this.user = u;
	for(Album a: user.getAlbum()){
				
				a.setRange();
			}

			absoluteFilePath += user.getUserName() + File.separator;

			tags = new ArrayList<String>();

			this.albumList = u.getAlbum();

			welcomeName.setText("Welcome, " + u.getUserName());

			listOfAlbums = new ArrayList<String>();

			for (int i = 0; i < albumList.size(); i++) {

				String name = albumList.get(i).getName();

				int size = albumList.get(i).getSize();

				if (albumList.get(i).getEarliestPhoto() != null && albumList.get(i).getLatestPhoto() != null) {
					String range = albumList.get(i).getEarliestPhoto() + "-" + albumList.get(i).getLatestPhoto();

					listOfAlbums.add(name + "   -   " + size + "   -   " + range);

				} else {

					listOfAlbums.add(name + "   -   " + size);
				}
			}
			obsList = FXCollections.observableArrayList(listOfAlbums);

			albumListView.setItems(obsList);

			albumListView.getSelectionModel().select(0);

			listSize.setText(albumList.size() + "");

			String item = albumListView.getSelectionModel().getSelectedItem();

			String[] parse = item.split("-");

			selectedAlbum.setText(parse[0].trim());
			selectedTotal.setText(parse[1].trim());

			if (albumList.get(0).getEarliestPhoto() != null && albumList.get(0).getLatestPhoto() != null) {
				selectedOldest.setText(parse[2].trim());
				selectedYoungest.setText(parse[3].trim());
			}

			albumListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					String item = albumListView.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					selectedAlbum.setText(parse[0].trim());
					selectedTotal.setText(parse[1].trim());

					if (parse.length == 4) {
						selectedOldest.setText(parse[2].trim());
						selectedYoungest.setText(parse[3].trim());

					} else {
						selectedYoungest.setText("");
						selectedOldest.setText("");

					}

				}
			});

		} catch (Exception e) {

			return;
		}

	}

	/**
	 * Attemps to create create a new album, and creates a directory for that
	 * album
	 * 
	 * @param event
	 *            Create Album Button
	 * @throws IOException
	 *             Exception for serializing user
	 */
	public void createAlbum() throws IOException {

		String name = newName.getText().trim();

		if (name.equals("") || name.contains("-")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("Names can not be blank or contain a \"-\"");
			alert.show();

			return;

		}

		if (!checkDupe(name)) {

			Album newAlbum = new Album(name);

			// user.addAlbum(newAlbum);

			albumList.add(newAlbum);

			listOfAlbums.add(newAlbum.getName() + "   -   " + "0");

			obsList = FXCollections.observableArrayList(listOfAlbums);

			// obsList.sort((s1, s2) ->
			// s1.toLowerCase().compareTo(s2.toLowerCase()));

			albumListView.setItems(obsList);

			albumListView.getSelectionModel().select(0);

			listSize.setText(listOfAlbums.size() + "");

			String item = albumListView.getSelectionModel().getSelectedItem();

			String[] parse = item.split("-");

			selectedAlbum.setText(parse[0].trim());
			selectedTotal.setText(parse[1].trim());

			if (listSize.getText().equals("1")) {

				albumListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						String item = albumListView.getSelectionModel().getSelectedItem();

						String[] parse = item.split("-");

						selectedAlbum.setText(parse[0].trim());
						selectedTotal.setText(parse[1].trim());

					}
				});
			}

			File file = new File(absoluteFilePath + newAlbum.getName() + File.separator);

			file.mkdirs();

			user.write(user);

			albumListView.getSelectionModel().select(Integer.parseInt(listSize.getText()) - 1);

			selectedAlbum.setText(albumList.get(Integer.parseInt(listSize.getText()) - 1).getName());

			selectedYoungest.setText("");
			selectedOldest.setText("");
			selectedTotal.setText("0");

			clearNew();

		} else {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("Album name aready exists");
			alert.setContentText("Please use a different album name.");
			alert.show();

		}
	}

	/**
	 * deletes an album and its directory recursivly A user can view a photo,
	 * copy a photo, move a photo, and delete a photo through the buttons
	 * displayed in each cell factoy
	 * 
	 * @param event
	 *            Delete album button pressed * @throws IOException Exception
	 *            for loading in photos
	 */
	public void deleteAlbum() throws IOException {

		if (albumListView.getSelectionModel().getSelectedItem()
				.substring(0, (albumListView.getSelectionModel().getSelectedItem().indexOf(" "))).trim().equals("stock")
				&& user.getUserName().equals("stock")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Delete Error");
			alert.setHeaderText("The Stock user's stock album can not be deleted.");
			alert.setContentText("You can still edit the contents of the album.");
			alert.show();

			return;

		}

		if (listSize.getText().equals("0")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Delete Error");
			alert.setHeaderText("The list is empty.");
			alert.setContentText("There are no albums to delete.");
			alert.show();

			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are You Sure?");
		alert.setHeaderText("Do you really want to delete this album?");
		alert.setContentText("Press Ok to delete album, or Cancel to go back.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			String item = albumListView.getSelectionModel().getSelectedItem();

			for (Album s : albumList) {

				if (s.getName().equals(item.substring(0, item.indexOf("-")).trim())) {

					listOfAlbums.remove(item);
					int index = albumList.indexOf(s);
					albumList.remove(index);

					// obsList.remove(index);
					obsList = FXCollections.observableArrayList(listOfAlbums);

					// obsList.sort((s1, s2) ->
					// s1.toLowerCase().compareTo(s2.toLowerCase()));

					albumListView.setItems(obsList);

					listSize.setText(listOfAlbums.size() + "");

					if (Integer.parseInt(listSize.getText()) == 0) {

						albumListView.setOnMouseClicked(null);
						selectedAlbum.setText("");
						selectedTotal.setText("");
						selectedOldest.setText("");
						selectedYoungest.setText("");

						File imageDir = new File(absoluteFilePath + File.separator + s.getName());

						recursiveDelete(imageDir);

						user.write(user);

						return;

					}

					albumListView.getSelectionModel().select(0);

					item = albumListView.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					selectedAlbum.setText(parse[0].trim());
					selectedTotal.setText(parse[1].trim());
					if (parse.length == 4) {
						selectedOldest.setText(parse[2].trim());
						selectedYoungest.setText(parse[3].trim());
					}

					File imageDir = new File(absoluteFilePath + File.separator + s.getName());

					recursiveDelete(imageDir);
					user.write(user);

					break;
				}

			}

		} else {

			return;
		}

	}

	/**
	 * deletes a directory recursively for that album
	 */
	public static void recursiveDelete(File file) {

		if (!file.exists()) {

			return;
		}

		if (file.isDirectory()) {

			for (File f : file.listFiles()) {

				recursiveDelete(f);
			}
		}

		file.delete();

	}

	/**
	 * Search for albums by tags
	 * 
	 * @param event
	 *            Search By Tags button pressed * @throws IOException Exception
	 *            for loading in photos
	 */
	public void searchTags(ActionEvent event) throws IOException {

		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Search a Tag");

		ButtonType loginButtonType = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(20, 100, 10, 10));

		TextField from = new TextField();
		from.setPromptText("Name");
		TextField to = new TextField();
		to.setPromptText("Value");

		gridPane.add(from, 2, 0);
		gridPane.add(new Label("Name"), 0, 0);
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

			try {
				findTags(prefix, value, event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

	}

	/**
	 * Finds the tags when you search by a tag
	 * 
	 * @param event
	 *            search by date button pressed
	 * @throws IOException
	 *             Exception for loading in photos
	 */
	public void findTags(String prefix, String value, ActionEvent event) throws IOException {

		Album temp = new Album("Search Results");

		List<Album> tempAlbumList = user.getAlbum();

		for (Album a : tempAlbumList) {

			List<Photo> tempPhotoList = a.getPhotolist();

			for (Photo p : tempPhotoList) {

				if (temp.contains(p)) {
					continue;
				}

				List<String> tempTagList = p.getTags();

				for (String s : tempTagList) {

					if ((prefix + ":" + value).equals(s) || prefix.equals(s.substring(0, s.indexOf(":")))
							|| value.equals(s.substring(s.indexOf(":") + 1))) {

						if (temp.contains(p)) {
							continue;
						}
						temp.addPhoto(p);

					}

				}

			}

		}

		if (temp.getSize() == 0) {

			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Search Error");
			error.setHeaderText("No Results");
			error.setContentText("Please try other tags.");
			error.showAndWait();
			tags = new ArrayList<String>();
			searchTags(event);
			return;
		}

		Parent parent;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResult.fxml"));
		parent = (Parent) loader.load();

		SearchResultController src = loader.getController();
		Scene scene = new Scene(parent);
		src.initialize(temp, user);
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		stage.setScene(scene);
		stage.show();

		tags = new ArrayList<String>();

	}

	/**
	 * search for photos by date pulls in date from the date pickers
	 * 
	 * @param event
	 *            search by date button pressed * @throws IOException Exception
	 *            for loading in photos
	 */
	public void searchDate(ActionEvent event) throws IOException {

		try {
			LocalDate localDateFrom = dateFrom.getValue();
			LocalDate localDateTo = dateTo.getValue();

			if (localDateFrom == null || localDateTo == null) {

				Alert error = new Alert(AlertType.INFORMATION);
				error.setTitle("Search Error");
				error.setHeaderText("You left a date picker empty");
				error.setContentText("Please enter both dates (from and to).");
				error.showAndWait();

				return;

			}

			String date = localDateFrom.toString();
			String[] parse = date.split("-");
			String from = parse[1] + "/" + parse[2] + "/" + parse[0];

			date = localDateTo.toString();
			parse = date.split("-");
			String to = parse[1] + "/" + parse[2] + "/" + parse[0];
	

			Album temp = new Album("Search Results");

			List<Album> tempAlbumList = user.getAlbum();

			for (Album a : tempAlbumList) {
	
				List<Photo> tempPhotoList = a.getPhotolist();

				for (Photo p : tempPhotoList) {

					if (temp.contains(p)) {
						continue;
					}

					String photoDate = p.getDate().substring(0,p.getDate().indexOf(" "));

					if (photoDate.compareTo(from) >= 0 && photoDate.compareTo(to) <= 0) {

						temp.addPhoto(p);

					}

				}
			}
				if (temp.getSize() == 0) {

					Alert error = new Alert(AlertType.INFORMATION);
					error.setTitle("Search Error");
					error.setHeaderText("No Results");
					error.setContentText("Please try other dates.");
					error.showAndWait();

					return;
				}

				Parent parent;

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SearchResult.fxml"));
				parent = (Parent) loader.load();

				SearchResultController src = loader.getController();
				Scene scene = new Scene(parent);
				src.initialize(temp, this.user);
				Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

				stage.setScene(scene);
				stage.show();

			
		} catch (Exception e) {
		
			return;
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
	 * This method clears the new name textfield
	 * 
	 * @param event
	 *            The Clear button is pressed
	 * 
	 */
	public void clearNew() {
		/**
		 * This method checks to make sure you are not creating a duplicate
		 * album An album is considered a duplicate if it's name already exists
		 * for an existing album
		 */

		newName.setText("");

	}

	public boolean checkDupe(String n) {

		for (Album s : getList()) {

			if (s.getName().equals(n)) {

				return true;
			}

		}
		return false;
	}

	/**
	 * changes the name of an album and renames its directory in the images
	 * directory
	 * 
	 * @param event
	 *            Change Namebutton pressed * @throws IOException Exception for
	 *            serializing user
	 */
	public void renameAlbum(ActionEvent event) throws IOException {

		String name = newName.getText().trim();

		if (name.equals("") || checkDupe(name)) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Rename Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("That name is invalid or already exists.");
			alert.show();

			return;
		}

		if (!checkDupe(name)) {

			for (Album a : albumList) {

				if (a.getName().equals(selectedAlbum.getText())) {

					a.setName(name, user);

					user.write(user);

					clearNew();

					initialize(user);

				}

			}

		}

	}

	/**
	 * Views the album selected from the listview
	 * 
	 * @param event
	 *            View Album button pressed * @throws IOException Exception for
	 *            loading in photos
	 */
	public void viewAlbum(ActionEvent event) throws IOException {

		if (albumList.size() == 0) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("View Error");
			alert.setHeaderText("There are no albums to view");
			alert.setContentText("First try creating an album");
			alert.show();

			return;

		}

		Album a = null;

		for (Album album : albumList) {

			if (album.getName().equals(selectedAlbum.getText())) {

				a = album;

			}

		}

		Parent parent;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album.fxml"));
		parent = (Parent) loader.load();

		AlbumViewController albumViewController = loader.getController();
		Scene scene = new Scene(parent);
		
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		albumViewController.initialize(a, user,scene,stage);
		stage.setScene(scene);
		stage.show();

	}

	/**
	 * returns the album list of the user
	 */
	public List<Album> getList() {

		return albumList;

	}

}
