package point.components;

import java.util.Map;

import point.components.Viability.IndividualsGroupViability;


public class IndividualsGroupState {
	
	public int strength;
	
	private String genotype;
	private int age;
	
	private float survival;
	private float competitiveness;
	private float reproduction;
	private float fertility;
	private float amplexusRepeat;
	private float voracity;
	
	private boolean isMatureMale;
	private boolean isMatureFemale;
	
	// additional fields to help PointMover
	public float percentageInHabitat;
	
	private Map<String, Map<String, Float>> posterityComposition;
	
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

	public float getSurvival() {
		return survival;
	}

	public float getCompetitiveness() {
		return competitiveness;
	}

	public float getReproduction() {
		return reproduction;
	}

	public float getFertility() {
		return fertility;
	}

	public float getAmplexusRepeat() {
		return amplexusRepeat;
	}

	public float getVoracity() {
		return voracity;
	}

	public Map<String, Float> getPosterityComposition(String father) {
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
	
}
