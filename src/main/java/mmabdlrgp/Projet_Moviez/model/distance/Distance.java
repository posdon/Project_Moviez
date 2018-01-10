package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.Map;

public interface Distance {

	public Double distance(Map<Integer, Double> currentRating,
			Map<Integer, Double> otherRatings);
}
