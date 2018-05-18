
package photoalbum;

/**
 * This class is the class that represents an album
 * @author Chris LoPresti
 * @author Kyle Myers
 */
import controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import view.*;

public class Photos extends Application {

	/**
	 * This class loads up the login page and starts the program
	 */
	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/login.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			LoginController listController = loader.getController();
			listController.initialize();
			Scene scene = new Scene(root, 400, 300);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This class calls the start method
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
