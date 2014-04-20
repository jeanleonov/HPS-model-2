package point.components;

import java.util.Map;

import point.components.Viability.IndividualsGroupViability;


public class IndividualsGroupState {
	
	public int strength;
	
	public int multipliedst;
	
	private String genotype;
	private int age;
	
	private double survival;
	private double competitiveness;
	private double reproduction;
	private double fertility;
	private double amplexusRepeat;
	private double voracity;
	
	private boolean isMatureMale;
	private boolean isMatureFemale;
	
	private Map<String, Map<String, Double>> posterityComposition;
	
	public IndividualsGroupState(IndividualsGroup group, int strength, Viability viability, Posterity posterity) {
		this.strength = strength;
		IndividualsGroupViability groupViability = viability.getGroupViability(group);
		survival = groupViability.survival;
		competitiveness = groupViability.competitiveness;
		reproduction = groupViability.reproduction;
		fertility = groupViability.fertility;
		amplexusRepeat = groupViability.amplexusRepeat;
		voracity = groupViability.voracity;
		genotype = group.getGenotype();
		age = group.getAge();
		if (reproduction > 0) {
			if (GenotypeHelper.isMale(genotype)) {
				isMatureMale = true;
				isMatureFemale = false;
			}
			else {
				isMatureMale = false;
				isMatureFemale = true;
			}
		}
		else {
			isMatureMale = false;
			isMatureFemale = false;
		}
		if (GenotypeHelper.isFemale(group.getGenotype()) && reproduction>0)
			posterityComposition = posterity.getCompositionFor(group.getGenotype());
	}

	public double getSurvival() {
		return survival;
	}

	public double getCompetitiveness() {
		return competitiveness;
	}

	public double getReproduction() {
		return reproduction;
	}

	public double getFertility() {
		return fertility;
	}

	public double getAmplexusRepeat() {
		return amplexusRepeat;
	}

	public double getVoracity() {
		return voracity;
	}

	public Map<String, Double> getPosterityComposition(String father) {
		return posterityComposition.get(father);
	}

	public String getGenotype() {
		return genotype;
	}

	public int getAge() {
		return age;
	}

	public boolean isMatureMale() {
		return isMatureMale;
	}

	public boolean isMatureFemale() {
		return isMatureFemale;
	}
	
	public int getNotMultipliedst() {
		return strength - multipliedst;
	}
	
	@Override
	public int hashCode() {
		return genotype.hashCode()+age;
	}
	
	@Override
	public boolean equals(Object obj) {
		IndividualsGroupState other = (IndividualsGroupState) obj;
		return genotype.equals(other.genotype) && age==other.age;
	}
	
}
