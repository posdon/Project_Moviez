package mmabdlrgp.Projet_Moviez.model;

import java.io.Serializable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Movie implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
 
	private IntegerProperty movieId;
	
	private StringProperty title;
	
	private StringProperty genres;
	
	public Movie(int movieId, String title, String genres) {
		super();
		this.movieId =  new SimpleIntegerProperty(movieId);
		 this.title = new SimpleStringProperty(title);
		this.genres = new SimpleStringProperty(genres);;
	}
 
	public IntegerProperty getMovieId() {
		return movieId;
	}
 
	public void setMovieId(Integer movieId) {
		this.movieId.set(movieId);
	}
 
	public StringProperty getTitle() {
		return title;
	}
 
	public void setTitle(String title) {
		this.title.set(title);
	}
 
	public StringProperty getGenres() {
		return genres;
	}
 
	public void setGenres(String genres) {
		this.genres.set(genres);
	}
 
	@Override
	public String toString() {
		return "Movie [movieId=" + movieId + ", title=" + title + ", genres=" + genres + "]";
	}
}