package songLib.view;

import java.io.BufferedWriter;
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

	public void convert(ActionEvent e) {
		Button b = (Button)e.getSource();
		if (b == f2c) {
			float fval = Float.valueOf(f.getText());
			float cval = (fval-32)*5/9;
			c.setText(String.format("%5.1f", cval));
		} else {
			float cval = Float.valueOf(c.getText());
			float fval = cval*9/5+32;
			f.setText(String.format("%5.1f", fval));
		}
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
	
	public void listToFile(ObservableList<Song> obsList) throws IOException { 
		BufferedWriter writer = new BufferedWriter(new FileWriter("SongSave.txt", true));
		for(Song song: obsList) {
			writer.append(song.toString());
			writer.append("/n");
		}
		writer.close();
	}
}
