package model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is the class that represents an album
 * 
 * This model implements the serializable interface.
 * 
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class Album implements Serializable {

	private String name;

	private String earliestPhoto;

	private String latestPhoto;

	private List<Photo> photolist;

	private int size;

	public String workingDirectory = System.getProperty("user.dir");
	public String absoluteFilePath = workingDirectory + File.separator + "images" + File.separator;
	/**
	 * constructor for the album class
	 */
	public Album(String albumName) {
		this.size = 0;
		this.name = albumName;
		this.earliestPhoto = null;
		this.latestPhoto = null;
		this.photolist = new ArrayList<Photo>();
	}
	/**
	 * returns the name of the album
	 */
	public String getName() {
		return name;
	}
	/**
	 * changes the name of the album
	 * this also renames its directory in the image directory
	 */
	public void setName(String name, User user) throws IOException {

		for (Album a : user.getAlbum()) {

			if (a.getName().equals(this.name)) {

				absoluteFilePath += user.getUserName() + File.separator;
				;
				File file1 = new File(absoluteFilePath + this.name);
				this.name = name;
				File file2 = new File(absoluteFilePath);

				file1.renameTo(file2);

				user.write(user);

				return;

			}

		}

	}
	/**
	 * return the list of photos for this album
	 */
	public List<Photo> getPhotolist() {
		return photolist;
	}
	/**
	 * sets the list of photos for this album
	 */
	public void setPhotolist(List<Photo> photolist) {
		this.photolist = photolist;
	}
	/**
	 * returns the date of the latest photo for this album in a string representation
	 */
	public String getLatestPhoto() {
		return latestPhoto;
	}
	/**
	 * sets the date of the latest photo as the string representation of arguement's date
	 */
	public void setLatestPhoto(Photo p) {
		latestPhoto = p.getDate();
	}
	/**
	 * returns the date of the earliest photo in the album in a string representation 
	 */
	public String getEarliestPhoto() {
		return earliestPhoto;
	}
	/**
	 * sets the date of the earliest photo as the string representation of arguement's date
	 */
	public void setEarliestPhoto(Photo p) {
		earliestPhoto = p.getDate();
	}
	/**
	 * gets the size of the album (number of photos)
	 */
	public int getSize() {
		return size;
	}
	/**
	 * sets the size of the album (number of photos)
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * adds a photo to the album , incrementing its size by 1
	 */
	public void addPhoto(Photo p) {
		photolist.add(p);
		size++;
	}
	/**
	 * deletes a photo from the album, also deleting the image from the images directory, decrementing the size by 1
	 */
	public void deletePhoto(Photo p) {
		photolist.remove(p);

		File file = new File(p.getImage());

		if (file.exists()) {

			file.delete();
		}

		size--;

		setRange();

	}
	/**
	 * sets the date range of the album.
	 * sets the earliest photo date 
	 * sets the latest photo date
	 */
	public void setRange() {

		if (size == 0) {

			earliestPhoto = null;
			latestPhoto = null;

			return;
		} else if (size == 1) {

			earliestPhoto = latestPhoto = photolist.get(0).getDate();

			return;
		}

		String early = photolist.get(0).getDate();
		String late = photolist.get(0).getDate();
		earliestPhoto=early;
		latestPhoto = late;
		
		for (int i = 0; i < photolist.size(); i++) {
			

			if (photolist.get(i).getDate().compareTo(latestPhoto) > 0) {
				
				latestPhoto = photolist.get(i).getDate();
				
			}

			if (photolist.get(i).getDate().compareTo(earliestPhoto) < 0) {
		
				earliestPhoto = photolist.get(i).getDate();
				
			}

		}

	}
	/**
	 * checks to see if a given photo is already contained in this album
	 */
	public boolean contains(Photo photo) {

		for (Photo p : photolist) {

			if (p.getSource().equals(photo.getSource())) {

				return true;
			}
		}

		return false;
	}

}
