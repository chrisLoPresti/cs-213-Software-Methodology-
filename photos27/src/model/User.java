package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is the class that represents a user
 * 
 * This model implements the serializable interface.
 * 
 * 
 * @author Chris LoPresti
 * @author Kyle Myers
 */
public class User implements Serializable {

	private String name;

	private String userName;

	private String password;

	private List<Album> album;

	private long serialVersionUID;
	public static final String storeDir = "dat";
	public static final String storeFile = ".dat";
	/**
	 * constructor for the user class
	 */
	public User(String name, String userName, String password, Long id) {

		this.setAlbum(new ArrayList<Album>());

		this.setSerialVersionUID(id);

		this.name = name;

		this.password = password;

		this.userName = userName;

	}
	/**
	 * adds an album to the user
	 */
	public void addAlbum(Album a) {

		album.add(a);
	}
	/**
	 * gets the name of the user
	 */
	public String getUserName() {

		return this.userName;

	}
	/**
	 * serialize the user
	 * @exception IOException     Exception for serializing the user
	 */
	public void write(User u) throws IOException {
	
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(storeDir + File.separator + u.userName + storeFile));
		oos.writeObject(u);
		oos.close();
	}
	/**
	 * de-serialize the user
	 * @exception IOException     Exception for de-serializing the user
	 */
	public static User read(String u) throws IOException, ClassNotFoundException {

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + u + storeFile));

		User user = (User) ois.readObject();

		ois.close();

		return user;
	}
	/**
	 * returns the users serial version id
	 */
	public long getSerialVersionUID() {

		return serialVersionUID;
	}
	/**
	 * sets the users serial version id
	 */
	public void setSerialVersionUID(long serialVersionUID) {

		this.serialVersionUID = serialVersionUID;
	}
	/**
	 * gets the album list for this user
	 */
	public List<Album> getAlbum() {
		return album;
	}
	/**
	 * sets the album list for this user
	 */
	public void setAlbum(List<Album> album) {

		this.album = album;
	}
}
