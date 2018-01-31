package mmabdlrgp.Projet_Moviez.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.Main;
import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class MovieOverviewController {
	
	@FXML
	private TableView<Movie> MovieTable;
	@FXML
	private TableColumn<Movie, String> titleColumn;
	@FXML 
	private ListView<String> listView;
	@FXML
	private TextField nbRank;
	@FXML
	private TextField nbIteration;
	
	@FXML
	private TextField nbReco;
	private Stage dialogStage;

	final ObservableList<String> listItems = FXCollections.observableArrayList();
	private Main mainApp;

	private boolean isBlocked = false;
	
	private RecommandationModel model;
	
	public MovieOverviewController() {
	}

	@FXML
	private void initialize() {
		titleColumn.setCellValueFactory(
				cellData -> cellData.getValue().getTitle());


		listView.setItems(listItems);
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
		this.model = mainApp.getModel();
		nbRank.setText(""+model.getRank());
		nbIteration.setText(""+model.getIteration());
		nbReco.setText(""+model.getNbRecommandation());
		MovieTable.setItems(mainApp.getMovieData());
	}	

	@FXML
	private void handleMoreInfoMovie() {
		if(!isBlocked) {
			Movie selectedMovie = MovieTable.getSelectionModel().getSelectedItem();
			if (selectedMovie != null) {
				mainApp.showMoreInfoMovie(selectedMovie);
			} else {
				openAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
			}
		}else {
			openAlert("Wait", "The program is loading", "Please wait a bit to the algorithme to be ready. It can be a bit long (5 min for the first start).");
		}
	}

	@FXML
	private void handleRateMovie() {
		if(!isBlocked) {
			Movie selectedMovie = MovieTable.getSelectionModel().getSelectedItem();
			if (selectedMovie != null) {
				mainApp.showMovieRate(selectedMovie);
			} else {
				openAlert("No Selection", "No Movie Selected", "Please select a movie in the table.");
			}
		}else {
			openAlert("Wait", "The program is loading", "Please wait a bit to the algorithme to be ready. It can be a bit long (5 min for the first start).");
		}
	}
	
	private void openAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
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

		if (nbRank.getText() == null || nbRank.getText().length() == 0 || nbIteration.getText() == null || nbIteration.getText().length() == 0) {
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
				Integer.parseInt(nbRank.getText());
				Integer.parseInt(nbIteration.getText());
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
			if( Integer.parseInt(nbReco.getText())<0 || Integer.parseInt(nbIteration.getText())<0 || Integer.parseInt(nbRank.getText())<0 ){
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
			model.setNbRank(Integer.parseInt(nbRank.getText()));
			model.setNbIteration(Integer.parseInt(nbIteration.getText()));
			model.setNbRecommandation(Integer.parseInt(nbReco.getText()));
			model.setCurrentUserVector(new HashMap<Integer,Double>(mainApp.getCurrentVector()));
			isBlocked = true;
			Map<Integer,Double> results = model.launchRecommandation();
			List<Movie> movieList = mainApp.getMovieData();
			listItems.clear();
			for(Integer key : results.keySet()) {
				for(Movie currMovie : movieList) {
					if(currMovie.getMovieId().intValue() == key) {
						listItems.add(currMovie.getTitle().getValue());
					}
				}
			}
			isBlocked = false;
		}
	}
}