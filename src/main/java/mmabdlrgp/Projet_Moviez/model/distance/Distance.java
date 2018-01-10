package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.Map;

public interface Distance {

	public Map<Integer, Double> Distance(Map<Integer,Double> currentRating,
			Map<Integer, Map<Integer, Double>> otherRatings, Map<Integer, Double> userWeight);
}
