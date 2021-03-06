package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.Map;

public class EuclidianDistance implements Distance{

	@Override
	public Double distance(Map<Integer, Double> currentRating,
			Map<Integer, Double> otherRatings) {
		
		Double distance = 0.0;
		for(Integer movieId : currentRating.keySet()) {
			distance += Math.pow(currentRating.get(movieId)-otherRatings.get(movieId), 2);
		}
		return distance;
	}
}
