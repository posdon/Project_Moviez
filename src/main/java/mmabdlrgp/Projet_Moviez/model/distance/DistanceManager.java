package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.HashMap;
import java.util.Map;

public class DistanceManager {

	public final static DistanceManager INSTANCE = new DistanceManager();
	
	private static Distance currDistance;
	
	private DistanceManager() {
		currDistance = new EuclidianDistance();
	}
	
	public static void setDistance(String distance) {
		switch(distance) {
			case "euclidian":
				currDistance = new EuclidianDistance();
				break;
			default:
				currDistance = new EuclidianDistance();
				break;
		}
	}
	
	public static Map<Integer, Double> distance(Map<Integer,Double> currentRating,
			Map<Integer, Map<Integer, Double>> otherRatings, Map<Integer, Double> userWeight) {
		
		Map<Integer,Double> weightlessBetweeness = new HashMap<Integer,Double>();
		for(Integer userId : otherRatings.keySet()) {
			weightlessBetweeness.put(userId, currDistance.distance(currentRating, otherRatings.get(userId)));
		}
		return applyWeight(weightlessBetweeness, userWeight);
		
		//return currDistance.Distance(currentRating, otherRatings, userWeight);
	}
	
	private static Map<Integer, Double> applyWeight(Map<Integer, Double> weightlessBetweeness,
			Map<Integer, Double> userWeight) {
		Map<Integer, Double> result = new HashMap<Integer,Double>();
		for(Integer userId : weightlessBetweeness.keySet()) {
			result.put(userId, userWeight.get(userId));
		}
		return result;
	}
}
