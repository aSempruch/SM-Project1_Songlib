package songLib.app;

public class Song implements Comparable<Song>{
	
	public String name, artist, album;
	public int year;
	
	public Song(String name, String artist, String album, String year) {
		this.name = name;
		this.artist = artist;
		this.album = album;
		if(year.length() == 0)
			this.year = 0;
		else
			this.year = Integer.parseInt(year);
	}
	
	public String toString() {
		return name + ", " + artist;
	}
	
	public String toStringFull() {
		//used for file writing
		return name + "," + artist + "," + album + "," + year;
	}
	
	public String getName() {
		return name;
	}
	
	public String getArtist() {
		return artist;
	}

	@Override
	public int compareTo(Song s2) {
		if(!(s2.getName().equals(this.name)))
			return this.name.compareTo(s2.getName());
		else {
			return this.artist.compareTo(s2.getArtist());
		}
	}
}
