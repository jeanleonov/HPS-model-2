package point.components;

import java.util.Map;


public class IndividualsGroupState {
	
	public int strength;
	
	private int lifetime;
	private float survival;
	private float competitiveness;
	private float reproduction;
	private float fertility;
	private float amplexusRepeat;
	private float voracity;
	
	private Map<String, Map<String, Float>> posterityComposition;
	
	public IndividualsGroupState(IndividualsGroup group, int strength, Viability viability, Posterity posterity) {
		this.strength = strength;
		lifetime = viability.getLifetime(group);
		survival = viability.getSurvival(group);
		competitiveness = viability.getCompetitiveness(group);
		reproduction = viability.getReproduction(group);
		fertility = viability.getFertility(group);
		amplexusRepeat = viability.getAmplexusRepeat(group);
		voracity = viability.getVoracity(group);
		if (GenotypeHelper.isFemale(group.getGenotype()) && reproduction>0)
			posterityComposition = posterity.getCompositionFor(group.getGenotype());
	}

	public int getLifetime() {
		return lifetime;
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
	
}
