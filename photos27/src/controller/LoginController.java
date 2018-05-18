package controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;

/**
 * LoginController is the class for controlling the login Page. The respective
 * view is login.fxml This class is used for login in a user This class logs in
 * a user and admin in different ways
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class LoginController {

	@FXML
	private Button login;
	@FXML
	private Button clear;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;
	@FXML
	private Label loginError;
	public static final String userDir = System.getProperty("user.name");
	public static final String filename = "userList.txt";
	public static final String workingDirectory = System.getProperty("user.dir");
	public static final String absoluteFilePath = workingDirectory + File.separator + filename;
	public static final File file = new File(absoluteFilePath);
	private int listSize;
	private List<String> userList;
	private List<String> passList;
	private List<String> nameList;
	private boolean clearInfo;

	/**
	 * loads up a list of users so when someone logs in their credentials can be
	 * verified
	 * 
	 * @throws ClassNotFoundException
	 *             Exception for switching scenes
	 */
	public void initialize() throws ClassNotFoundException {

		try {

			userList = new ArrayList<String>();
			passList = new ArrayList<String>();
			nameList = new ArrayList<String>();

			Scanner readFile = new Scanner(file);

			String size = readFile.nextLine();
			String dir = readFile.nextLine();
		
			 clearInfo = false;
			if (!dir.equals(userDir)) {
				clearInfo = true;

			}

			listSize = Integer.parseInt(size);

			for (int i = 0; i < listSize; i++) {

				String input = readFile.nextLine();

				userList.add(input.substring(0, input.indexOf(" ")));

				passList.add(input.substring(input.indexOf(" ") + 1, input.indexOf("~")));

				nameList.add(input.substring(input.indexOf("~") + 1));

			}

			readFile.close();

		} catch (Exception e) {

			return;
		}

	}

	/**
	 * This will login a user or Admin depending on credentials
	 * 
	 * @param event
	 *            login button pressed
	 * @throws ClassNotFoundException
	 *             Exception for switching scenes
	 * @throws IOException
	 *             Exception for controller not being present
	 */
	@FXML
	protected void login(ActionEvent event) throws ClassNotFoundException, IOException {

		String usernameL = username.getText().trim();
		String passwordL = password.getText().trim();

		Parent parent;

		if (usernameL.equals("admin") && passwordL.equals("admin")) {

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
			parent = (Parent) loader.load();

			AdminController adminController = loader.getController();
			Scene scene = new Scene(parent);
			adminController.initialize(clearInfo, userList, nameList, passList);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(scene);
			stage.show();

		} else if (validLogin(usernameL, passwordL)) {

			User u = User.read(usernameL);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/user.fxml"));
			parent = (Parent) loader.load();

			UserController usercontroller = loader.getController();
			Scene scene = new Scene(parent);
			usercontroller.initialize(u);
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			stage.setScene(scene);
			stage.show();

		} else {

			loginError.setText("Invalid Username and Password Combination");
		}

	}

	/**
	 * This will clear the login credentials
	 * 
	 * @param event
	 *            clear button pressed
	 */
	public void clear() {

		username.setText("");
		password.setText("");
		loginError.setText("");
	}

	/**
	 * Checks to make sure that the username and password exist and that they
	 * correspong to a user
	 * 
	 * @param event
	 *            login button pressed
	 */
	public boolean validLogin(String u, String p) {

		for (String s : userList) {

			if (s.equals(u)) {

				String pass = passList.get(userList.indexOf(u));

				if (p.equals(pass)) {

					return true;
				} else {

					return false;
				}

			}
		}

		return false;

	}

}
