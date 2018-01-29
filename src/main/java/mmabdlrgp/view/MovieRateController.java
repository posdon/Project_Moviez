package mmabdlrgp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.model.Movie;

public class MovieRateController {

	@FXML
	private TextField ratingField;

	private Stage dialogStage;
	private Movie movie;
	private boolean okClicked = false;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
		ratingField.setText(movie.getMovieId().getValue().toString());
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	@FXML
	private void handleOk() {
		if (isInputValid()) {
			movie.setMovieId(Integer.parseInt(ratingField.getText()));

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
				Integer.parseInt(ratingField.getText());
			} catch (NumberFormatException e) {
				errorMsg += "Rating not valid (must be an integer)!\n";
			}
			if( Integer.parseInt(ratingField.getText())<0 || Integer.parseInt(ratingField.getText())>5 ){
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