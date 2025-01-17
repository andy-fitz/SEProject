package client.java.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/*
	This is where execution begins for the desktop application.
	Main task of this class is to set up the welcome screen and link to it.
 */

public class Client extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/client/resources/view/welcomeScreen.fxml"));
		primaryStage.setTitle("Panopoly");
		Scene scene = new Scene(root, 1200, 800);
		scene.getStylesheets().addAll(this.getClass().getResource("/client/resources/css/welcome.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) throws IOException{
		launch(args);
	}
}