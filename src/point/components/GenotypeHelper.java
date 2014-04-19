package point.components;

public class GenotypeHelper {
	
	private GenotypeHelper(){}
		
	private static final String pattern = "(([xXyY][a-zA-Z])|(\\([xXyY][a-zA-Z]\\)))*|(([a-zA-Z][xXyY])|(\\([a-zA-Z][xXyY]\\)))*";
	
	public static boolean isGenotype(String genotype) {
		return genotype.matches(pattern);
	}
	
	public static boolean isMale(String genotype) {
		return !genotype.contains("y") && !genotype.contains("Y");
	}
	
	public static boolean isFemale(String genotype) {
		return genotype.contains("y") || genotype.contains("Y");
	}
	
}
