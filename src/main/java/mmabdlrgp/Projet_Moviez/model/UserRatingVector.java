package mmabdlrgp.Projet_Moviez.model;

import java.io.Serializable;
import java.util.HashMap;

public class UserRatingVector implements Serializable {

	private Integer userId;
	private HashMap<Integer,Double> ratingsMovie;
	
	public UserRatingVector(Integer userId) {
		super();
		this.userId = userId;
		ratingsMovie = new HashMap<Integer,Double>();
	}
	
	public UserRatingVector addMovieRating(Integer movieId, Double rating) {
		ratingsMovie.put(movieId,rating);
		return this;
	}
	
	public Integer getUserId() {
		return userId;
	}
	
	public Double getRatingFromMovie(Integer movieId) {
		return (ratingsMovie.containsKey(movieId)) ? ratingsMovie.get(movieId) : -1 ;
	}

	@Override
	public String toString() {
		String result = "User [userId=" + userId +"]\n";
		for(Integer movieId : ratingsMovie.keySet()) {
			result += "    [movieId= " + movieId + " ; rating = " + ratingsMovie.get(movieId) + "]\n";
		}
		return result;
	} 
}
