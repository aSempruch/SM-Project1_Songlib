package songLib.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import songLib.app.Song;

public class SongLibController {
	
	@FXML Button f2c;
	@FXML Button c2f;
	@FXML TextField f;
	@FXML TextField c;
	@FXML ListView<Song> songlist;
	
	private ObservableList<Song> obsList;
	
	public void start(Stage mainStage) {
		obsList = FXCollections.observableArrayList();
		
		// Add items from file to list
		try {
			fileToList(obsList);
		} catch (IOException e) {
			System.out.println("[IO Error] Unable to Open SongList file for reading");
			e.printStackTrace();
		}
		
		songlist.setItems(obsList);
		
		songlist.getSelectionModel().select(0);

	      // set listener for the items
	      songlist
	        .getSelectionModel()
	        .selectedIndexProperty()
	        .addListener(
	           (obs, oldVal, newVal) -> 
	               showItemInputDialog(mainStage));
	}
	
	private void showItemInputDialog(Stage mainStage) {                
	      Song song = songlist.getSelectionModel().getSelectedItem();
	      int index = songlist.getSelectionModel().getSelectedIndex();

	      TextInputDialog dialog = new TextInputDialog(song.toString());
	      dialog.initOwner(mainStage); dialog.setTitle("List Item");
	      dialog.setHeaderText("Selected Item (Index: " + index + ")");
	      dialog.setContentText("Enter name: ");

	      Optional<String> result = dialog.showAndWait();
	      if (result.isPresent()) { obsList.set(index, song); }
	}
	
	private void fileToList(ObservableList<Song> obsList) throws IOException {
		BufferedReader reader = new BufferedReader(
			new FileReader(
				getClass().getResource("/SongList.txt").getFile()
			)
		);
		
		String line;
		while((line = reader.readLine()) != null) {
			String[] s = line.split(",");
			obsList.add(
				new Song(s[0],s[1],s[2],s[3])
			);
		}
		reader.close();
	}
	
	public void listToFile(ObservableList<Song> obsList) throws IOException { 
		BufferedWriter writer = new BufferedWriter(new FileWriter(
			getClass().getResource("/SongList.txt").getFile(), true
		));
		for(Song song: obsList) {
			writer.append(song.toString());
			writer.append("/n");
		}
		writer.close();
	}
}
