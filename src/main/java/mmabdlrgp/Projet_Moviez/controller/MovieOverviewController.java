package mmabdlrgp.Projet_Moviez.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import mmabdlrgp.Projet_Moviez.Main;
import mmabdlrgp.Projet_Moviez.model.Movie;

public class MovieOverviewController {
	
	@FXML
	private TableView<Movie> MovieTable;
	@FXML
	private TableColumn<Movie, String> titleColumn;
	@FXML 
	ChoiceBox<String> cb;
	@FXML 
	private ListView<String> listView;
	@FXML
	private TextField nbUser;
	@FXML
	private TextField nbReco;
	private Stage dialogStage;

	final ObservableList<String> listItems = FXCollections.observableArrayList("Add Items here");
	private Main mainApp;

	public MovieOverviewController() {
	}

	@FXML
	private void initialize() {
		titleColumn.setCellValueFactory(
				cellData -> cellData.getValue().getTitle());


		ObservableList<String> availableChoices = FXCollections.observableArrayList("Distance1", "Distance2"); 
		cb.setItems(availableChoices);
		cb.getSelectionModel().select(0);
		listView.setItems(listItems);
		listItems.add(cb.getSelectionModel().getSelectedItem());
		/*cb.getSelectionModel().selectedIndexProperty().addListener(new
				ChangeListener<Number>() {
			public void changed(ObservableValue ov,
					Number value, Number new_value) {
				listItems.add(availableChoices.get(new_value.intValue()));

			}
		});*/
	}

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
		MovieTable.setItems(mainApp.getMovieData());
	}	

	@FXML
	private void handleMoreInfoMovie() {
		Movie selectedMovie = MovieTable.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			mainApp.showMoreInfoMovie(selectedMovie);
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Movie Selected");
			alert.setContentText("Please select a movie in the table.");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleRateMovie() {
		Movie selectedMovie = MovieTable.getSelectionModel().getSelectedItem();
		if (selectedMovie != null) {
			mainApp.showMovieRate(selectedMovie);
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("No Selection");
			alert.setHeaderText("No Movie Selected");
			alert.setContentText("Please select a movie in the table.");
			alert.showAndWait();
		}
	}

	@FXML
	private void handleHelp() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle("Help");
		alert.setHeaderText("To have a recommandation");
		alert.setContentText("Select the distance method\n"
				+ "Select the number of users\n"
				+ "Select the number of recommandations");
		alert.showAndWait();
	}

	private boolean isInputValid() {
		String errorMsg = "";

		if (nbUser.getText() == null || nbUser.getText().length() == 0) {
			errorMsg += "Number User Or Number Recommandations not valid!\n";
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Number User Or Number Recommandations");
			alert.setHeaderText("The Number User Or Number Recommandations cannot be empty");
			alert.setContentText(errorMsg);
			alert.showAndWait();

			return false;

		} else {
			try {
				Integer.parseInt(nbUser.getText());
				Integer.parseInt(nbReco.getText());
			} catch (NumberFormatException e) {
				errorMsg += "Number User Or Number Recommandations Not valid (must be an integer)!\n";
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("Invalid Number User Or Number Recommandations");
				alert.setHeaderText("Number User Or Number Recommandations not valid");
				alert.setContentText(errorMsg);
				alert.showAndWait();
			}
			if( Integer.parseInt(nbReco.getText())<0 || Integer.parseInt(nbUser.getText())<0 ){
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("Invalid Number User Or Number Recommandations");
				alert.setHeaderText("The Number User Or Number Recommandations must be greater than 0");
				alert.setContentText(errorMsg);
				alert.showAndWait();

				return false;
			}
		}
		return true;
	}

	@FXML
	private void handleStart() {
		if(isInputValid()) {
			listItems.add(cb.getSelectionModel().getSelectedItem());
		}
	}
}