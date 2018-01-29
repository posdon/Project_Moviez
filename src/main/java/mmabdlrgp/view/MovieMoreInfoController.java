package mmabdlrgp.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import mmabdlrgp.Projet_Moviez.model.Movie;

public class MovieMoreInfoController {
	@FXML
	private Label titleLabel;
	@FXML
	private Label genresLabel;
	@FXML
	private Label ratingLabel;

    private Stage dialogStage;
    private Movie movie;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        titleLabel.setText(movie.getTitle().getValue().toString());
        genresLabel.setText(movie.getGenres().getValue().toString());
        ratingLabel.setText(movie.getMovieId().getValue().toString());
    }

    @FXML
    private void handleClose() {
        dialogStage.close();
    }
}