package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This class is the class that represents a photo
 * 
 * This model implements the serializable interface.
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class Photo implements Serializable {

	private String caption;
	private String name;
	private List<String> tags;
	private Calendar cal;
	private String date;
	private String imagePath;
	private String source;
	private String albumName;
	private Album album;
	public String workingDirectory = System.getProperty("user.dir");
	public String absoluteFilePath = workingDirectory + File.separator + "images" + File.separator;

	/**
	 * constructor for the photo class
	 */
	public Photo(String image, User u, Album a, Date dateDate) {
		this.setAlbum(a);
		absoluteFilePath += u.getUserName() + File.separator + a.getName() + File.separator;
		this.setAlbumName(a.getName());
		this.tags = new ArrayList<String>();

		this.imagePath = image;

		this.source = "";

		String[] parse = image.split(File.separator);

		this.setName(parse[parse.length - 1]);

		this.setCaption("");

		this.cal = Calendar.getInstance();

		this.cal.setTime(dateDate);

		this.cal.set(Calendar.MILLISECOND, 0);

		if (cal.get(cal.DAY_OF_MONTH) < 10) {

			date = (cal.get(cal.MONTH) + 1) + "/0" + cal.get(cal.DAY_OF_MONTH) + "/" + cal.get(cal.YEAR) + " | "+
			cal.get(cal.HOUR_OF_DAY) + ":" + cal.get(cal.MINUTE) + ":" + cal.get(cal.SECOND) ;
		} else {

			date = (cal.get(cal.MONTH) + 1) + "/" + cal.get(cal.DAY_OF_MONTH) + "/" + cal.get(cal.YEAR) + " | "+
					cal.get(cal.HOUR_OF_DAY) + ":" + cal.get(cal.MINUTE) + ":" + cal.get(cal.SECOND) ;
		}
	}

	/**
	 * returns the date the photo was added
	 */
	public String getDate() {
		return date;
	}

	/**
	 * returns the date the photo was added
	 */
	public void addTags(String tag) {
		tags.add(tag);

	}

	/**
	 * sets the date the photo was added
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * gets the name of the photo
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets the name of the photo
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * returns the file path of the photo
	 */
	public String getImage() {
		return imagePath;
	}

	/**
	 * sets the file path of the photo
	 */
	public void setImage(String image) {
		this.imagePath = image;
	}

	/**
	 * gets the caption of the photo
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * sets the caption of the photo
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}

	/**
	 * gets the photo's tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * sets the photo's tags
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * changes the name of the photo
	 */
	public void changeName(String s) {

		s += name.substring(name.length() - 4);
		File file1 = new File(imagePath);
		File file2 = new File(absoluteFilePath + s);

		file1.renameTo(file2);

		setImage(file2.getAbsolutePath());

		this.name = s;

	}

	/**
	 * gets the source path the file originated from
	 */
	public String getSource() {
		return source;
	}

	/**
	 * sets the source path the file originated from
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * returns the album this photo belongs to
	 */
	public String getAlbumName() {
		return albumName;
	}

	/**
	 * sets the album this photo belongs to
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	/**
	 * gets the album for this photo 
	 */
	public Album getAlbum() {
		return album;
	}
	/**
	 * sets the album for this photo 
	 */
	public void setAlbum(Album album) {
		this.album = album;
	}

}
