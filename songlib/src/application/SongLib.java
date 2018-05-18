//Christoopher LoPrsti cpl62  - Kyle Myers kwm38 - Software Methodology 213 SongLib Assignment 
package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SongLib extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("SongLib.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			SongLibController listController = loader.getController();
			listController.initialize(primaryStage);
			Scene scene = new Scene(root, 550, 650);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
