//Christoopher LoPrsti cpl62  - Kyle Myers kwm38 - Software Methodology 213 SongLib Assignment 

package song;

public class song {

	public String name;
	public String artist;
	public String album;
	public String year;

	public song next;

	public song(String name, String artist, String album, String year) {

		this.name = name;
		this.artist = artist;
		this.album = album;
		this.year = year;
		this.next = null;

	}

	public String listDisplay() {

		return this.name + "   -   " + this.artist;

	}

}
