package mmabdlrgp.Projet_Moviez.controller;

import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.Main;
import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class MovieRateController {

	@FXML
	private TextField ratingField;

	private Stage dialogStage;
	private Movie movie;
	private boolean okClicked = false;
	private RecommandationModel model;
	private Main mainApp;
	
	@FXML
	private void initialize() {
	}

	public void setDialogStage(Main mainApp, Stage dialogStage, RecommandationModel model) {
		this.dialogStage = dialogStage;
		this.model = model;
		this.mainApp = mainApp;
		mainApp.setCurrentVector(model.getCurrentUserVector());
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
		if(model.getCurrentUserVector().keySet().contains(movie.getMovieId().intValue())) {
			ratingField.setText(model.getCurrentUserVector().get(movie.getMovieId().intValue()).toString());        	
        }else {
        	ratingField.setText("-Not rated-");
        }
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleOk() {
		if (isInputValid()) {
			double note = Double.parseDouble(ratingField.getText());
		    if(note >= 0.0 || note <= 5.0) {
	    		mainApp.putCurrentVector(movie.getMovieId().intValue(),note);
	    	}else {
	    		System.out.println("Error :: Note isn't in the good format");
	    	}
		    
			okClicked = true;
			dialogStage.close();
		}
	}

	private boolean isInputValid() {
		String errorMsg = "";

		if (ratingField.getText() == null || ratingField.getText().length() == 0) {
			errorMsg += "Rating not valid!\n";
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid Rating");
			alert.setHeaderText("The Rating cannot be empty");
			alert.setContentText(errorMsg);
			alert.showAndWait();

			return false;

		} else {
			try {
				Double.parseDouble(ratingField.getText());
			} catch (NumberFormatException e) {
				errorMsg += "Rating not valid (must be an integer)!\n";
			}
			if( Double.parseDouble(ratingField.getText())<0.0 || Double.parseDouble(ratingField.getText())>5.0 ){
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(dialogStage);
				alert.setTitle("Invalid Rating");
				alert.setHeaderText("The Rating must be between 0 and 5");
				alert.setContentText(errorMsg);
				alert.showAndWait();

				return false;
			}
		}
		return true;
	}
}