package songLib.app;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import songLib.view.SongLibController;


public class SongLibApp extends Application {
	@Override
	public void start(Stage primaryStage) 
	throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/songLib/view/SongLib.fxml"));
		
		GridPane root = (GridPane)loader.load();
		
		SongLibController songLibController = loader.getController();
		songLibController.start(primaryStage);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Song List App");
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
