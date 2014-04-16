package point.components;

import java.util.HashMap;
import java.util.Map;

public class Posterity {
	
	private Map<String, Map<String, Map<String, Float>>> motherPosteritiesMap;
	
	public Posterity() {
		motherPosteritiesMap = new HashMap<>();
	}
	
	public void addCompositionFor(String mother, String father, Map<String, Float> posterityComposition) {
		Map<String, Map<String, Float>> motherPosterities = motherPosteritiesMap.get(mother);
		if (motherPosterities == null) {
			motherPosterities = new HashMap<>();
			motherPosteritiesMap.put(mother, motherPosterities);
		}
		motherPosterities.put(father, posterityComposition);
	}
	
	public Map<String, Map<String, Float>> getCompositionFor(String genotype) {
		return motherPosteritiesMap.get(genotype);
	}
	
}
