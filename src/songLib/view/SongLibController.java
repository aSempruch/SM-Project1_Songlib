package songLib.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import songLib.app.Song;

/**
 * @authors
 * Alan Sempruch
 * NetID: as2322
 *
 * Andrew Li
 * NetID: al958
 *
 */
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
		
		File newFile = new File("bin/SongList.txt");
		try {
			newFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
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
		int selector = songlist.getSelectionModel().getSelectedIndex();
		deleteSong(song);
		if(obsList.isEmpty()) {
			// if list is empty, then clear selection and empty fields
			songlist.getSelectionModel().clearSelection();
			details_title.setText("");
			details_artist.setText("");
			details_album.setText("");
			details_year.setText("");
		}
		else if(obsList.size() > selector) {
			// if there is song below the deleted, select that one
			songlist.getSelectionModel().selectNext();
			loadSong();
		}
		// otherwise just select the song previous to the deleted
	}
	
	public void editHandler() {
		
		if(obsList.size() == 0) {
			showError("Error Editing Song", "Cannot edit song if list is empty");
			return;	
		}
		
		
		// Check if title or artist is empty
		if(details_title.getText().length() == 0 || details_artist.getText().length() == 0) {
			showError("Error Editing Song", "Song title and artist are required");
			return;
		}
		
		try {
			if(details_year.getText().length() > 0)
				Integer.parseInt(details_year.getText());
		} catch (NumberFormatException e) {
			showError("Error Editing Song", "The year must be a number");
			return;
		}
		
		Song song = songlist.getSelectionModel().getSelectedItem();
		Song newSong = new Song(details_title.getText(), details_artist.getText(), details_album.getText(), details_year.getText());
		
		if(isDuplicate(newSong) && !(newSong.getName().equals(song.getName()) && newSong.getArtist().equals(song.getArtist()))) {
			showError("Error Editing Song", "A song with this title and artist already exists.");
			return;
		}
		
		obsList.set(songlist.getSelectionModel().getSelectedIndex(),newSong);
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
		if(obsList.size() > 0)
			update_button.setVisible(true);
	}
	
	public void addHandler() {
		String  title = add_title.getText(),
				artist = add_artist.getText(),
				album = add_album.getText(),
				year = add_year.getText();
		
		try {
			if(year.length() > 0)
				Integer.parseInt(year);
		} catch (NumberFormatException e) {
			showError("Error Adding Song", "The year must be a number");
			return;
		}
		
		
		if(title.length() > 0 && artist.length() > 0) {
			Song newSong = new Song(title, artist, album, year);
			addSong(newSong);
		}
		else {
			showError("Error Adding Song", "Song title and artist are required");
		}
	}
	
	private void addSong(Song song) {
		if(isDuplicate(song)) {
			showError("Error Adding Song", "A song with this title and artist already exists.");
			return;
		}
		
		obsList.add(song);
		sortList(obsList);
		try {
			listToFile(obsList);
		}
		catch(IOException e) {
			System.out.println("[IO Error] Unable to update SongList file");
			e.printStackTrace();
		}
		songlist.getSelectionModel().select(song);
		loadSong();
		
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
	
	private void showError(String title, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private boolean isDuplicate(Song newSong) {
		for (Song song : obsList) {
			if(song.getName().equals(newSong.getName()) && song.getArtist().equals(newSong.getArtist()))
				return true;
		}
		
		return false;
	}
}
