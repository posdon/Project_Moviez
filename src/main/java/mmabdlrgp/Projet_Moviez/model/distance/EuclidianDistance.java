package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;

public class EuclidianDistance implements Distance{

	public Double distance(Map<Integer, Double> currentRating,
			Map<Integer, Double> otherRatings) {
		
		Double distance = 0.0;
		for(Integer movieId : currentRating.keySet()) {
			distance += Math.pow(currentRating.get(movieId)-otherRatings.get(movieId), 2);
		}
		Double maxDistance = 25.0*currentRating.size();
		return (maxDistance-distance)/maxDistance;
	}
}
