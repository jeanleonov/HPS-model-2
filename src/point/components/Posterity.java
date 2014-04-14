package point.components;

import java.util.Map;

public class Posterity {
	
	private Map<String, Map<String, Map<String, Float>>> motherPosteritiesMap;
	
	// TODO
	
	public Map<String, Map<String, Float>> getCompositionFor(String genotype) {
		return motherPosteritiesMap.get(genotype);
	}
	
}
