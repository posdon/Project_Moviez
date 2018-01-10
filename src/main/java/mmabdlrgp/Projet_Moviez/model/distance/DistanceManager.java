package mmabdlrgp.Projet_Moviez.model.distance;

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
		return currDistance.Distance(currentRating, otherRatings, userWeight);
	}
}
