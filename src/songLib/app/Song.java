package songLib.app;

public class Song {
	
	public String name, artist, album;
	public int year;
	
	public Song(String name, String artist, String album, String year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = Integer.parseInt(year);
	}
	
	public String toString() {
		return name + ", " + artist;
	}
}
