//Christoopher LoPrsti cpl62  - Kyle Myers kwm38 - Software Methodology 213 SongLib Assignment 

package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import song.song;

public class SongLibController {

	@FXML
	public Button add;
	@FXML
	public Button delete;
	@FXML
	public Button save;
	@FXML
	public Button clearNew;
	@FXML
	public Button clearChange;

	@FXML
	public Label selectedSong;
	@FXML
	public Label selectedArtist;
	@FXML
	public Label selectedAlbum;
	@FXML
	public Label selectedYear;
	@FXML
	public Label size;

	@FXML
	public TextField newName;
	@FXML
	public TextField newArtist;
	@FXML
	public TextField newAlbum;
	@FXML
	public TextField newYear;
	@FXML
	public TextField changeSong;
	@FXML
	public TextField changeArtist;
	@FXML
	public TextField changeAlbum;
	@FXML
	public TextField changeYear;

	@FXML
	public ListView<String> songList;
	@FXML
	public ObservableList<String> obsList;

	public song head;

	public int listSize;

	public static final String filename = "songList.txt";
	public static final String workingDirectory = System.getProperty("user.dir");
	public static final String absoluteFilePath = workingDirectory + File.separator + filename;
	public static final File file = new File(absoluteFilePath);

	public void initialize(Stage stage) {

		try {

			Scanner readFile = new Scanner(file);

			String size = readFile.nextLine();

			listSize = Integer.parseInt(size);

			String[] songs = new String[listSize];
			String[] listDisplay = new String[listSize];
			String[] entry = new String[4];

			int i = 0;

			songs[i] = readFile.nextLine();
			entry = songs[i].split("-");

			song temp = new song(entry[0].trim(), entry[1].trim(), entry[2].trim(), entry[3].trim());

			head = temp;

			listDisplay[i] = head.name + "   -   " + head.artist;

			i++;

			while (i < listSize) {

				songs[i] = readFile.nextLine();

				entry = songs[i].split("-");

				temp = new song(entry[0].trim(), entry[1].trim(), entry[2].trim(), entry[3].trim());

				temp.next = head;

				head = temp;

				listDisplay[i] = head.name + "   -   " + head.artist;
				
				i++;
			}

			readFile.close();

			obsList = FXCollections.observableArrayList(listDisplay);

			sortList(head.name, head.artist);

			songList.setItems(obsList);

			songList.getSelectionModel().select(0);

			String item = songList.getSelectionModel().getSelectedItem();

			String[] parse = item.split("-");

			song display = search(parse[0].trim(), parse[1].trim());

			selectedSong.setText(display.name);
			selectedArtist.setText(display.artist);
			selectedAlbum.setText(display.album);
			selectedYear.setText(display.year);
			this.size.setText(Integer.toString(listSize));

			songList.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					String item = songList.getSelectionModel().getSelectedItem();

					String[] parse = item.split("-");

					song display = search(parse[0].trim(), parse[1].trim());

					selectedSong.setText(display.name);
					selectedArtist.setText(display.artist);
					selectedAlbum.setText(display.album);
					selectedYear.setText(display.year);

				}
			});

		} catch (Exception e) {

			return;
		}

	}

	public void sortList(String name, String artist) {

		obsList = obsList.sorted();
		songList.setItems(obsList);

		String[] listData = new String[listSize];
		int index = 0;
		String[] compare = new String[2];

		for (int i = 0; i < listSize; i++) {
			listData[i] = obsList.get(i);
			compare = obsList.get(i).split("-");

			if (name.equals(compare[0].trim()) && artist.equals(compare[1].trim())) {

				index = i;
			}
		}

		obsList = FXCollections.observableArrayList(listData);
		songList.setItems(obsList);
		songList.getSelectionModel().select(index);

		String item = songList.getSelectionModel().getSelectedItem();

		String[] parse = item.split("-");

		song display = search(parse[0].trim(), parse[1].trim());

		selectedSong.setText(display.name);
		selectedArtist.setText(display.artist);
		selectedAlbum.setText(display.album);
		selectedYear.setText(display.year);

	}

	public void editEntry(ActionEvent e) throws IOException {

		int noChange = 0;

		if (head == null) {

			Alert empty = new Alert(AlertType.INFORMATION);
			empty.setTitle("Input Error!");
			empty.setHeaderText("There is nothing to modify!");
			empty.setContentText("The song library is empty!");

			empty.showAndWait();

			changeSong.clear();
			changeArtist.clear();
			changeAlbum.clear();
			changeYear.clear();

			return;

		}

		String name = "";

		if (changeSong.getText().trim().isEmpty() || changeSong.getText().trim().equals("")) {

			name = selectedSong.getText();

			noChange++;

		} else {

			name = changeSong.getText().trim();

		}

		String artist = "";

		if (changeArtist.getText().trim().isEmpty() || changeArtist.getText().trim().equals("")) {

			artist = selectedArtist.getText();

			noChange++;

		} else {

			artist = changeArtist.getText().trim();
		}

		String album = "";

		if (changeAlbum.getText().trim().isEmpty() || changeAlbum.getText().trim().equals("")) {

			album = selectedAlbum.getText();

		} else {

			album = changeAlbum.getText().trim();
		}

		String yearString = "";

		if (changeYear.getText().trim().isEmpty()) {

			yearString = selectedYear.getText();

		} else {
			yearString = changeYear.getText().trim();

			try {
				int year = 0;

				year = (int) Integer.parseInt(yearString);

				if (year > 2017 || year < 1900) {

					Alert empty = new Alert(AlertType.INFORMATION);
					empty.setTitle("Input Error!");
					empty.setHeaderText("We noticed an error with your input!");
					empty.setContentText("That year doesn't seem to make sense.\n Lets try again.");

					empty.showAndWait();

					return;

				}

			} catch (Exception e1) {

				Alert empty = new Alert(AlertType.INFORMATION);
				empty.setTitle("Input Error!");
				empty.setHeaderText("We noticed an error with your input!");
				empty.setContentText("That year doesn't seem to make sense.\n Lets try again.");

				empty.showAndWait();

				return;

			}
		}

		if (name.equals(selectedSong.getText()) && artist.equals(selectedArtist.getText())
				&& album.equals(selectedAlbum.getText()) && yearString.equals(selectedYear.getText())) {

			Alert empty = new Alert(AlertType.INFORMATION);
			empty.setTitle("No Changes Made");
			empty.setHeaderText("We noticed your input is the same as before");
			empty.setContentText("The entered information is the same on file.\n No changes were made.");

			empty.showAndWait();

			changeSong.clear();
			changeArtist.clear();
			changeAlbum.clear();
			changeYear.clear();

			return;

		}

		song newEntry = new song(name, artist, album, yearString);

		song temp = head;

		boolean dupe = dupeCheck(newEntry);

		if (noChange == 2) {

			dupe = false;
		}

		if (dupe == false) {

			while (head != null) {

				if (head.name.equals(selectedSong.getText()) && head.artist.equals(selectedArtist.getText())) {

					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Are You Sure?");
					if (album.equals("") && yearString.equals("")) {

						alert.setHeaderText(name + " - " + artist);

					} else {

						alert.setHeaderText(name + " - " + artist + " - " + album + " - " + yearString);
					}

					alert.setContentText("Press Ok to save changes, or Cancel to go back.");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {

						head.name = name;
						head.artist = artist;
						head.album = album;
						head.year = yearString;

						int index = songList.getSelectionModel().getSelectedIndex();

						obsList.set(index, head.listDisplay());
						songList.setItems(obsList);

						changeSong.clear();
						changeArtist.clear();
						changeAlbum.clear();
						changeYear.clear();
					
						sortList(newEntry.name, newEntry.artist);

						writeFile();

						return;

					} else {

						return;
					}

				}

				head = head.next;

			}

			head = temp;

		} else {

			Alert empty = new Alert(AlertType.INFORMATION);
			empty.setTitle("Input Error!");
			empty.setHeaderText("We noticed an error with your input!");
			empty.setContentText("That song name and artist combination already exists.");

			empty.showAndWait();

			return;

		}

	}// end off add

	public void add(ActionEvent e) throws IOException {

		String name = newName.getText().trim();
		String artist = newArtist.getText().trim();

		if (newName.getText().isEmpty() || newArtist.getText().isEmpty() || name == "" || artist == "") {

			Alert empty = new Alert(AlertType.INFORMATION);
			empty.setTitle("Input Error!");
			empty.setHeaderText("We noticed an error with your input!");
			empty.setContentText("Song names and Artist names can not be empty.");

			empty.showAndWait();

			return;

		}
		String album = "";
		if (newAlbum.getText().isEmpty()) {

			album = "";

		} else {
			album = newAlbum.getText().trim();
		}

		String yearString = "";

		if (newYear.getText().isEmpty()) {

			yearString = "";

		} else {
			yearString = newYear.getText().trim();

			try {
				int year = 0;

				year = (int) Integer.parseInt(yearString);

				if (year > 2017 || year < 1900) {

					Alert empty = new Alert(AlertType.INFORMATION);
					empty.setTitle("Input Error!");
					empty.setHeaderText("We noticed an error with your input!");
					empty.setContentText("That year doesn't seem to make sense.\n Lets try again.");

					empty.showAndWait();

					return;

				}

			} catch (Exception e1) {

				Alert empty = new Alert(AlertType.INFORMATION);
				empty.setTitle("Input Error!");
				empty.setHeaderText("We noticed an error with your input!");
				empty.setContentText("That year doesn't seem to make sense.\n Lets try again.");

				empty.showAndWait();

				return;

			}
		}
		song newEntry = new song(name, artist, album, yearString);

		if (head == null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Are You Sure?");
			alert.setHeaderText("Do you really want to add this entry?");
			alert.setContentText("Press Ok to save changes, or Cancel to go back.");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {

				head = newEntry;
				head.next = null;

				listSize = 1;
				this.size.setText(Integer.toString(listSize));
				obsList = FXCollections.observableArrayList(head.listDisplay());
				songList.setItems(obsList);
				songList.getSelectionModel().select(0);

				songList.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						String item = songList.getSelectionModel().getSelectedItem();

						String[] parse = item.split("-");

						song display = search(parse[0].trim(), parse[1].trim());

						selectedSong.setText(display.name);
						selectedArtist.setText(display.artist);
						selectedAlbum.setText(display.album);
						selectedYear.setText(display.year);

					}
				});

			} else {

				return;
			}

		} else {

			boolean dupe = dupeCheck(newEntry);

			if (dupe == false) {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Are You Sure?");
				alert.setHeaderText("Do you really want to add this entry?");
				alert.setContentText("Press Ok to save changes, or Cancel to go back.");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK) {

					newEntry.next = head;
					head = newEntry;
					listSize++;
					this.size.setText(Integer.toString(listSize));

				} else {

					return;
				}

			} else {

				Alert empty = new Alert(AlertType.INFORMATION);
				empty.setTitle("Input Error!");
				empty.setHeaderText("We noticed an error with your input!");
				empty.setContentText("That song name and artist combination already exists.");

				empty.showAndWait();

				return;

			}

		}

		obsList.add(head.listDisplay());

		newName.clear();
		newArtist.clear();
		newAlbum.clear();
		newYear.clear();
		sortList(head.name, head.artist);
		writeFile();

	}// end off add

	public boolean dupeCheck(song check) {

		String checkName = check.name;
		String checkArtist = check.artist;

		song temp = head;

		while (temp != null) {

			if (temp.name.equals(checkName)) {

				if (temp.artist.equals(checkArtist)) {

					return true;
				}

			}

			temp = temp.next;

		}

		return false;

	}

	public song search(String name, String artist) {

		song temp = head;

		while (temp != null) {

			if (temp.name.equals(name)) {

				if (temp.artist.equals(artist)) {

					return temp;

				}

			}
			temp = temp.next;
		}

		song nullSong = new song("", "", "", "");
		return nullSong;

	}

	public void delete() throws IOException {

		if (head == null) {

			return;

		}

		String item = songList.getSelectionModel().getSelectedItem();

		String[] split = new String[2];

		split = item.split("-");

		song temp = head;
		song prev = null;

		if (head.name.equals(split[0].trim()) && head.artist.equals(split[1].trim())) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Are You Sure?");
			alert.setHeaderText("Deleting this entry will be permanent");
			alert.setContentText("Press Ok to save changes, or Cancel to go back.");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {

				head = head.next;

				if (listSize == 1) {
					listSize--;
					size.setText(Integer.toString(listSize));

					obsList = FXCollections.observableArrayList("");
					songList.setItems(obsList);

					songList.setOnMouseClicked(null);
					songList.getSelectionModel().clearSelection();
					selectedSong.setText("");
					selectedArtist.setText("");
					selectedAlbum.setText("");
					selectedYear.setText("");

					writeFile();

					return;

				} else {

					obsList.remove(item);
				}

				obsList = obsList.sorted();

				songList.setItems(obsList);

				listSize--;

				size.setText(Integer.toString(listSize));

				songList.getSelectionModel().select(0);

				sortList(head.name, head.artist);

				writeFile();
				return;

			} else {

				return;
			}
		}

		while (head.next != null && !(head.name.equals(split[0].trim()) && head.artist.equals(split[1].trim()))) {

			prev = head;
			head = head.next;

		}
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Are You Sure?");
		alert.setHeaderText("Deleting this entry will be permanent");
		alert.setContentText("Press Ok to save changes, or Cancel to go back.");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {

			prev.next = head.next;

			head = temp;

			obsList.remove(item);

			obsList = obsList.sorted();

			songList.setItems(obsList);

			listSize--;
			size.setText(Integer.toString(listSize));

			sortList(head.name, head.artist);

			writeFile();

		} else {

			return;
		}

		return;

	}

	public void writeFile() throws IOException {
		try {

			FileWriter fw = new FileWriter(absoluteFilePath);
			fw.write(listSize + "\n");

			song temp = head;

			while (temp != null) {

				fw.write(temp.name + " - " + temp.artist + " - " + temp.album + " - " + temp.year + "\n");

				temp = temp.next;

			}

			fw.close();

			return;

		} catch (Exception e) {

			return;
		}

	}

	public void clearNew() {

		newName.clear();
		newArtist.clear();
		newAlbum.clear();
		newYear.clear();

		return;
	}

	public void clearChange() {

		changeSong.clear();
		changeArtist.clear();
		changeAlbum.clear();
		changeYear.clear();

		return;

	}
}// end of class
