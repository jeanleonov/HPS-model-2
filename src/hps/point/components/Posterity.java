package hps.point.components;

import java.util.LinkedHashMap;
import java.util.Map;

public class Posterity {
	
	private Map<String, Map<String, Map<String, Double>>> motherPosteritiesMap;
	
	public Posterity() {
		motherPosteritiesMap = new LinkedHashMap<>();
	}
	
	public void addCompositionFor(String mother, String father, Map<String, Double> posterityComposition) {
		Map<String, Map<String, Double>> motherPosterities = motherPosteritiesMap.get(mother);
		if (motherPosterities == null) {
			motherPosterities = new LinkedHashMap<>();
			motherPosteritiesMap.put(mother, motherPosterities);
		}
		motherPosterities.put(father, posterityComposition);
	}
	
	public Map<String, Map<String, Double>> getCompositionFor(String genotype) {
		return motherPosteritiesMap.get(genotype);
	}
	
}
