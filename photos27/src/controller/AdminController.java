package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Album;
import model.Photo;
import model.User;

/**
 * AdminController is the class for controlling the Admin Page. The respective
 * view is admin.fxml This class is used for creating and deleting users
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */

public class AdminController {

	@FXML
	private ListView<String> userList;

	@FXML
	private TextField newUserName;

	@FXML
	private TextField newName;

	@FXML
	private TextField newPassword;

	@FXML
	private TextField searchUser;

	@FXML
	private Button addUser;

	@FXML
	private Label selectedUsername;

	@FXML
	private Label selectedName;

	@FXML
	private Label name;

	@FXML
	private Label username;

	@FXML
	private Label listSize;

	@FXML
	private Button delete;

	@FXML
	private Button clearNew;

	@FXML
	private Button clearSearch;

	@FXML
	private Button search;

	@FXML
	private Button logout;

	private static List<String> listOfUsers;
	private List<String> userNameList;
	private List<String> passList;
	private List<String> nameList;

	public ObservableList<String> obsList;

	public static final String filename = "userList.txt";
	public static final String workingDirectory = System.getProperty("user.dir");
	public static final String userDir = System.getProperty("user.name");
	public static final String absoluteFilePath = workingDirectory + File.separator + filename;
	public static final String stockPath = workingDirectory;
	public static final File file = new File(absoluteFilePath);
	private boolean clearInfo;

	/**
	 * Attempts to load a previous saved list of users from a text file. If
	 * successful it will populate a ListvIEW with users and attach a listener
	 * to the ListView
	 * 
	 * @param mainStage
	 *            The main stage
	 */
	public void initialize(Boolean clearInfo, List<String> userNameList, List<String> nameList, List<String> passList) {

		try {

			this.userNameList = userNameList;
			this.passList = passList;
			this.nameList = nameList;

			listOfUsers = new ArrayList<String>();

			for (int i = 0; i < userNameList.size(); i++) {

				if (userNameList.get(i).equals("admin")) {

					continue;
				}

				listOfUsers.add(userNameList.get(i) + "   -   " + nameList.get(i));

			}

			obsList = FXCollections.observableArrayList(listOfUsers);

			obsList.sort((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));

			userList.setItems(obsList);

			userList.getSelectionModel().select(0);

			listSize.setText(listOfUsers.size() + "");

			String item = userList.getSelectionModel().getSelectedItem();

			String[] parse = item.split("-");

			selectedUsername.setText(parse[0].trim());
			selectedName.setText(parse[1].trim());

			userList.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					String item = userList.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					selectedUsername.setText(parse[0].trim());
					selectedName.setText(parse[1].trim());

				}
			});

			if (clearInfo) {

				this.clearInfo = clearInfo;

				for (int i = 0; i <= obsList.size(); i++) {

					if (obsList.get(i).substring(0, obsList.get(0).indexOf(" ")).trim().equals("stock")) {

						continue;
					} else {

						userList.getSelectionModel().select(i);

						deleteUser();

					}
				}

				clearInfo = false;
				this.clearInfo = clearInfo;
				writeFile();

				initialize(clearInfo, userNameList, nameList, passList);
				return;
			}

		} catch (Exception e) {

			return;
		}

	}

	/**
	 * Allows the admin to create users, duplicate users are not allowed A user
	 * is concidered to be a duplicate if they share the same username as
	 * another user
	 * 
	 * @param event
	 *            The Create User button is pressed
	 * @throws IOException
	 *             Exception for serializing a new user
	 * @throws ClassNotFoundException
	 */
	public void createUser() throws IOException, ClassNotFoundException {

		String name = newName.getText().trim();
		String userName = newUserName.getText().trim();
		String newPass = newPassword.getText().trim();

		if (name.equals("") || userName.equals("") || newPass.equals("")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("Invalid entry information");
			alert.setContentText("Please use valid information.");
			alert.show();

			return;

		}

		if (!checkDupe(userName)) {

			User u = new User(name, userName, newPass, (long) Integer.parseInt(listSize.getText()));

			userNameList.add(userName);
			passList.add(newPass);
			nameList.add(name);

			listOfUsers.add(userName + "   -   " + name);

			obsList = FXCollections.observableArrayList(listOfUsers);

			obsList.sort((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));

			userList.setItems(obsList);

			userList.getSelectionModel().select(0);

			listSize.setText(listOfUsers.size() + "");

			String item = userList.getSelectionModel().getSelectedItem();

			String[] parse = item.split("-");

			selectedUsername.setText(parse[0].trim());
			selectedName.setText(parse[1].trim());

			if (listSize.getText().equals("1")) {

				userList.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						String item = userList.getSelectionModel().getSelectedItem();

						String[] parse = item.split("-");

						selectedUsername.setText(parse[0].trim());
						selectedName.setText(parse[1].trim());

					}
				});
			}

			try {
				if (!u.getUserName().equals("stock")) {

					File file = new File(stockPath + File.separator + "images" + File.separator + u.getUserName()
							+ File.separator + "stock" + File.separator);

					u.addAlbum(new Album("stock"));

					file.mkdirs();

					User stock = User.read("stock");

					Album stockAlbum = stock.getAlbum().get(0);
					Album userStock = u.getAlbum().get(0);

					for (Photo p : stockAlbum.getPhotolist()) {

						if (p != null) {
							copyPhoto(p, u, userStock);
						}

					}

				}
			} catch (Exception e) {

				return;
			}
			u.write(u);
			clearNew();

			writeFile();

		} else {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Add Error");
			alert.setHeaderText("Username aready exists");
			alert.setContentText("Please use a different username.");
			alert.show();

		}
	}

	public void copyPhoto(Photo photo, User user, Album album) throws IOException {
		

		File from = new File(stockPath + File.separator + "images" + File.separator + "stock" + File.separator + "stock"
				+ File.separator + photo.getName());

		File to = new File(stockPath + File.separator + "images" + File.separator + user.getUserName() + File.separator
				+ album.getName() + File.separator + photo.getName());

		Files.copy(from.toPath(), to.toPath());
		Date date = new Date(from.lastModified());
		Photo newPhoto = new Photo(stockPath + File.separator + "images" + File.separator + user.getUserName()
				+ File.separator + "stock" + File.separator + photo.getName(), user, album, date);
		newPhoto.setDate(photo.getDate());
		newPhoto.setCaption(photo.getCaption());
		newPhoto.setTags(photo.getTags());
		newPhoto.setSource(photo.getSource());

		album.addPhoto(newPhoto);

		if (album.getSize() == 1) {

			album.setEarliestPhoto(newPhoto);
			album.setLatestPhoto(newPhoto);
		} else {

			album.setRange();

		}

		return;

	}

	/**
	 * Allows the admin to delete users
	 * 
	 * @param event
	 *            The Delete User button is pressed
	 * @throws IOException
	 *             Exception for writing the list of users to a text file
	 */
	public void deleteUser() throws IOException {

		if (userList.getSelectionModel().getSelectedItem()
				.substring(0, (userList.getSelectionModel().getSelectedItem().indexOf(" "))).trim().equals("stock")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Delete Error");
			alert.setHeaderText("The Stock user can not be deleted.");
			alert.setContentText("The Stock user can not be deleted, sorry.");
			alert.show();

			return;

		}

		if (clearInfo) {
			String item = userList.getSelectionModel().getSelectedItem();

			for (String s : userNameList) {

				if (s.equals(item.substring(0, item.indexOf(" ")))) {

					listOfUsers.remove(item);
					int index = userNameList.indexOf(s);
					userNameList.remove(index);
					passList.remove(index);
					nameList.remove(index);
					// obsList.remove(index);
					obsList = FXCollections.observableArrayList(listOfUsers);

					obsList.sort((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));

					userList.setItems(obsList);

					String fileString = workingDirectory + File.separator + "dat" + File.separator + s + ".dat";

					File dataFile = new File(fileString);

					dataFile.delete();

					listSize.setText(listOfUsers.size() + "");

					if (Integer.parseInt(listSize.getText()) == 0) {

						userList.setOnMouseClicked(null);
						selectedUsername.setText("");
						selectedName.setText("");

						writeFile();
						File imageDir = new File(workingDirectory + File.separator + "images" + File.separator + s);

						recursiveDelete(imageDir);
						return;

					}

					userList.getSelectionModel().select(0);

					item = userList.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					selectedUsername.setText(parse[0].trim());
					selectedName.setText(parse[1].trim());

					File imageDir = new File(workingDirectory + File.separator + "images" + File.separator + s);

					recursiveDelete(imageDir);

					writeFile();

					break;
				}

			}
			return;
		}
		if (listSize.getText().equals("0")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Delete Error");
			alert.setHeaderText("The list is empty.");
			alert.setContentText("There are no users to delete.");
			alert.show();

			return;
		}

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are You Sure?");
		alert.setHeaderText("Do you really want to delete this user?");
		alert.setContentText("Press Ok to delete user, or Cancel to go back.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			String item = userList.getSelectionModel().getSelectedItem();

			for (String s : userNameList) {

				if (s.equals(item.substring(0, item.indexOf(" ")))) {

					listOfUsers.remove(item);
					int index = userNameList.indexOf(s);
					userNameList.remove(index);
					passList.remove(index);
					nameList.remove(index);
					// obsList.remove(index);
					obsList = FXCollections.observableArrayList(listOfUsers);

					obsList.sort((s1, s2) -> s1.toLowerCase().compareTo(s2.toLowerCase()));

					userList.setItems(obsList);

					String fileString = workingDirectory + File.separator + "dat" + File.separator + s + ".dat";

					File dataFile = new File(fileString);

					dataFile.delete();

					listSize.setText(listOfUsers.size() + "");

					if (Integer.parseInt(listSize.getText()) == 0) {

						userList.setOnMouseClicked(null);
						selectedUsername.setText("");
						selectedName.setText("");

						writeFile();
						File imageDir = new File(workingDirectory + File.separator + "images" + File.separator + s);

						recursiveDelete(imageDir);
						return;

					}

					userList.getSelectionModel().select(0);

					item = userList.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					selectedUsername.setText(parse[0].trim());
					selectedName.setText(parse[1].trim());

					File imageDir = new File(workingDirectory + File.separator + "images" + File.separator + s);

					recursiveDelete(imageDir);

					writeFile();

					break;
				}

			}

		} else {

			return;
		}

	}

	/**
	 * This recursively deletes all directories that are associated with a user
	 * once the user is deleted
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
	 * This method writes the list of users to a text file
	 * 
	 * @throws IOException
	 *             Exception for writing the list of users to a text file
	 */
	public void writeFile() throws IOException {
		try {

			FileWriter fw = new FileWriter(absoluteFilePath);
			fw.write(listSize.getText() + "\n");
			fw.write(userDir + "\n");

			for (String s : userNameList) {

				fw.write(s + " " + passList.get(userNameList.indexOf(s)) + "~" + nameList.get(userNameList.indexOf(s))
						+ "\n");

			}

			fw.close();

			return;

		} catch (Exception e) {

			return;
		}

	}

	/**
	 * Allows the admin to search for a user by their username
	 * 
	 * @param event
	 *            The Search user button is pressed
	 */
	public void search() {

		if (listSize.getText().equals("0")) {

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Search Error");
			alert.setHeaderText("The list is empty.");
			alert.setContentText("There are no users to search for.");
			alert.show();

			return;
		}

		for (String s : userNameList) {

			if (s.equals(searchUser.getText().trim())) {

				userList.getSelectionModel().select(userNameList.indexOf(s));

				String item = userList.getSelectionModel().getSelectedItem();

				String[] parse = item.split("-");

				selectedUsername.setText(parse[0].trim());
				selectedName.setText(parse[1].trim());

				clearSearch();

				return;

			}

		}

		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Search Error");
		alert.setHeaderText("Username doesn't exist");
		alert.setContentText("Please make sure you spelt it correctly");
		alert.show();

	}

	/**
	 * This will log the admin out and take them to the login page
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
	 * This method clears the admins create new user text fields clears username
	 * textfield clears name textfield clears password textfield
	 * 
	 * @param event
	 *            The Clear button is pressed
	 * 
	 */
	public void clearNew() {

		newName.setText("");
		newUserName.setText("");
		newPassword.setText("");

	}

	/**
	 * This method clears the admins search textfield
	 * 
	 * @param event
	 *            The Clear button is pressed
	 * 
	 */
	public void clearSearch() {

		searchUser.setText("");

	}

	/**
	 * This method checks to make sure that when an admin tries to create a new
	 * user, that the new username doesn't already exist
	 */
	public boolean checkDupe(String n) {

		for (String s : userNameList) {

			if (s.equals(n)) {

				return true;
			}

		}
		return false;
	}

}
