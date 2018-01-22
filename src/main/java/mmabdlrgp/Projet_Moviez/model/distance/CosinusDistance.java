package mmabdlrgp.Projet_Moviez.model.distance;

import java.util.Map;

public class CosinusDistance implements Distance {

	@Override
	public Double distance(Map<Integer, Double> currentRating,
			Map<Integer, Double> otherRatings) {
		
		Double scalar = 0.0;
		Double sumCurrent = 0.0;
		Double sumOther = 0.0;
		for(Integer movieId : currentRating.keySet()) {
			scalar += currentRating.get(movieId)*otherRatings.get(movieId);
			sumCurrent += Math.pow(currentRating.get(movieId),2);
			sumOther += Math.pow(otherRatings.get(movieId),2);
		}
		Double denominator = Math.sqrt(sumCurrent)*Math.sqrt(sumOther);
		return (denominator == 0.0) ? 0.0 : Math.round(scalar/denominator); 
	}
}
