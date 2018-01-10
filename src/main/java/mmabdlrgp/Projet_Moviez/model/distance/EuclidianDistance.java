package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.HashMap;
import java.util.Map;

import scala.Tuple2;

public class EuclidianDistance implements Distance{

	public Map<Integer, Double> Distance(Map<Integer,Double> currentRating,
			Map<Integer, Map<Integer, Double>> otherRatings, Map<Integer, Double> userWeight) {
		
		Map<Integer,Double> weightlessBetweeness = new HashMap<Integer,Double>();
		for(Integer userId : otherRatings.keySet()) {
			weightlessBetweeness.put(userId, calculateEuclidienDistance(currentRating, otherRatings.get(userId)));
		}
		return applyWeight(weightlessBetweeness, userWeight);
	}

	private static Map<Integer, Double> applyWeight(Map<Integer, Double> weightlessBetweeness,
			Map<Integer, Double> userWeight) {
		Map<Integer, Double> result = new HashMap<Integer,Double>();
		for(Integer userId : weightlessBetweeness.keySet()) {
			result.put(userId, userWeight.get(userId));
		}
		return result;
	}

	private static Double calculateEuclidienDistance(Map<Integer, Double> currentRating,
			Map<Integer, Double> otherRatings) {
		
		Double distance = 0.0;
		for(Integer movieId : currentRating.keySet()) {
			distance += Math.pow(currentRating.get(movieId)-otherRatings.get(movieId), 2);
		}
		Double maxDistance = 25.0*currentRating.size();
		return (maxDistance-distance)/maxDistance;
	}
}
