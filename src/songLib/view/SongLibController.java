package songLib.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import songLib.app.Song;

public class SongLibController {
	
	@FXML ListView<Song> songlist;
	
	@FXML Pane details;
	
	@FXML TextField details_title;
	@FXML TextField details_artist;
	@FXML TextField details_album;
	@FXML TextField details_year;
	
	@FXML TextField add_title;
	@FXML TextField add_artist;
	@FXML TextField add_album;
	@FXML TextField add_year;
	
	@FXML Button update_button;
	
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
		sortList(obsList);
		songlist.setItems(obsList);
		
		songlist.getSelectionModel().select(0);
		loadSong();

	      // set listener for the items
	      songlist
	        .getSelectionModel()
	        .selectedIndexProperty()
	        .addListener(
	           (obs, oldVal, newVal) -> 
	               loadSong());
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
	
	private void loadSong() {
		update_button.setVisible(false);
		try {
			Song song = songlist.getSelectionModel().getSelectedItem();
			details_title.setText(song.name);
			details_artist.setText(song.artist);
			details_album.setText(song.album);
			if(song.year != 0)
				details_year.setText(Integer.toString(song.year));
			else
				details_year.setText("");
		}
		catch (NullPointerException e) {
			// sorting the obsList causes NullPointerException briefly
			// we should probably fix this before submitting
		}
	}
	
	private void fileToList(ObservableList<Song> obsList) throws IOException {
		BufferedReader reader = new BufferedReader(
			new FileReader(
				getClass().getResource("/SongList.txt").getFile()
			)
		);
		
		System.out.println(getClass().getResource("/SongList.txt").getPath());
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
			getClass().getResource("/SongList.txt").getFile(), false
		));
		
		for(Song song: obsList) {
			writer.append(song.toStringFull());
			writer.newLine();
		}
		writer.close();
	}
	
	public void deleteHandler() {
		Song song = songlist.getSelectionModel().getSelectedItem();
		deleteSong(song);
	}
	
	public void editHandler() {
		Song song = songlist.getSelectionModel().getSelectedItem();
		song = new Song(details_title.getText(), details_artist.getText(), details_album.getText(), details_year.getText());
		obsList.set(songlist.getSelectionModel().getSelectedIndex(),song);
		update_button.setVisible(false);
		//ObservableList<Song> dummyList = FXCollections.observableArrayList(obsList);
		sortList(obsList);
		//obsList = FXCollections.observableArrayList(dummyList);
		try {
			listToFile(obsList);
		} catch (IOException e) {
			System.out.println("[IO Error] Unable to Open SongList file for reading");
			e.printStackTrace();
		}
	}
	
	// Make update button visible
	public void updateClickHandler() {
		update_button.setVisible(true);
	}
	
	public void addHandler() {
		String  title = add_title.getText(),
				artist = add_artist.getText(),
				album = add_album.getText(),
				year = add_year.getText();
		
		if(title.length() > 0 && artist.length() > 0) {
			Song newSong = new Song(title, artist, album, year);
			addSong(newSong);
		}
		else {
			// TODO: Show error dialog that title and artist are required
		}
	}
	
	private void addSong(Song song) {
		// TODO: Check if song title & artist conflict
		
		obsList.add(song);
		sortList(obsList);
		try {
			listToFile(obsList);
		}
		catch(IOException e) {
			System.out.println("[IO Error] Unable to update SongList file");
			e.printStackTrace();
		}
		
		// Clear Input Fields
		add_title.clear();
		add_artist.clear();
		add_album.clear();
		add_year.clear();
	}
	
	private void deleteSong(Song song) {
		obsList.remove(song);
		try {
			listToFile(obsList);
		} catch (IOException e) {
			System.out.println("[IO Error] Unable to Open SongList file for reading");
			e.printStackTrace();
		}
	}
	
	private void sortList(ObservableList<Song> obsList) {
		FXCollections.sort(obsList);
	}
}
