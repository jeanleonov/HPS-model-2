package point.components;

public class GenotypeHelper {
	
	private GenotypeHelper(){}
		
	private static final String pattern = "(([xXyY][a-zA-Z])|(\\([xXyY][a-zA-Z]\\)))*|(([a-zA-Z][xXyY])|(\\([a-zA-Z][xXyY]\\)))*";
	
	static boolean isGenotype(String genotype) {
		return genotype.matches(pattern);
	}
	
	static boolean isMale(String genotype) {
		return !genotype.contains("y") && !genotype.contains("Y");
	}
	
	static boolean isFemale(String genotype) {
		return genotype.contains("y") || genotype.contains("Y");
	}
	
}
