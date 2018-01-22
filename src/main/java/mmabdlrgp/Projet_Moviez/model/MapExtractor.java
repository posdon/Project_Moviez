package mmabdlrgp.Projet_Moviez.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.Tuple2;

public class MapExtractor {

public final static MapExtractor INSTANCE = new MapExtractor();
	
	private Map<Integer,Double> map;
	private int nbResult;
	private Tuple2<Integer,Double>[] resultTab;
	
	private MapExtractor() {
		map = new HashMap<Integer,Double>();
		nbResult=0;
	}
	
	public void setNbResult(int i) {
		if(i >= 0)
		nbResult = i;
	}
	
	public void setMap(Map<Integer,Double> mapGiven) {
		map = mapGiven;
	}
	
	public List<Tuple2<Integer,Double>> getXFirstResults(){
		resultTab = new Tuple2[nbResult];
		for(int i=0; i<nbResult; i++) {
			resultTab[i] = new Tuple2<Integer,Double>(1,0.0);
		}
		
		for(Integer userId : map.keySet()) {
			for(int i=0; i<nbResult; i++) {
				if(map.get(userId) > resultTab[i]._2()) {
					insertInto(resultTab, i, new Tuple2<Integer,Double>(userId,map.get(userId)));
				}
			}
		}
		
		List<Tuple2<Integer,Double>> result = new ArrayList<Tuple2<Integer,Double>>();
		for(int i=0; i<nbResult; i++) {
			result.add(resultTab[i]);
		}
		return result;
	}
	
	private static void insertInto(Tuple2<Integer,Double>[] tab, int index, Tuple2<Integer,Double> value) {
		for(int i= index; i<tab.length-1; i++) {
			Tuple2<Integer,Double> temp = tab[i];
			tab[i] = value;
			value = temp;
		}
	}
}
