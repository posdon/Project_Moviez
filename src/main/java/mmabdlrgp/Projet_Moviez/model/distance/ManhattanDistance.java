package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.Map;

public class ManhattanDistance implements Distance {

	@Override
	public Double distance(Map<Integer, Double> currentRating, Map<Integer, Double> otherRatings) {
		Double result = 0.0;
		for(Integer movieId : currentRating.keySet()) {
			result += Math.abs(currentRating.get(movieId)-otherRatings.get(movieId));
		}
		return null;
	}

}
