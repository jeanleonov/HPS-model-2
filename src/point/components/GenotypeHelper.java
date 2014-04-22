package point.components;

public class GenotypeHelper {
	
	private GenotypeHelper(){}
		
	private static final String PATTERN = "(([xXyY][a-zA-Z])|(\\([xXyY][a-zA-Z]\\)))*|(([a-zA-Z][xXyY])|(\\([a-zA-Z][xXyY]\\)))*";
	
	public static boolean isGenotype(String genotype) {
		return genotype.matches(PATTERN) && point_initialization.inputs_areas.Viability.isKnown(genotype);
	}
	
	public static boolean isLookedAsGenotype(String genotype) {
		return genotype.matches(PATTERN);
	}
	
	public static boolean isFemale(String genotype) {
		return !genotype.contains("y") && !genotype.contains("Y");
	}
	
	public static boolean isMale(String genotype) {
		return genotype.contains("y") || genotype.contains("Y");
	}
	
}
