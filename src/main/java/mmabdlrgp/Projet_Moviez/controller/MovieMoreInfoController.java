package mmabdlrgp.Projet_Moviez.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.model.Movie;
import mmabdlrgp.Projet_Moviez.model.RecommandationModel;

public class MovieMoreInfoController {
	@FXML
	private Label titleLabel;
	@FXML
	private Label genresLabel;
	@FXML
	private Label ratingLabel;

    private Stage dialogStage;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMovie(Movie movie) {
        titleLabel.setText(movie.getTitle().getValue().toString());
        genresLabel.setText(movie.getGenres().getValue().toString());
        if(RecommandationModel.getCurrentUserVector().keySet().contains(movie.getMovieId())) {
        	ratingLabel.setText(RecommandationModel.getCurrentUserVector().get(movie.getMovieId()).toString());        	
        }else {
        	ratingLabel.setText("-Not rated-");
        }
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}