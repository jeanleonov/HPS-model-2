package hps.point.components;

import hps.point.components.Viability.IndividualsGroupViability;

import java.util.Map;


public class IndividualsGroupState {
	
	public int strength;
	public int strengthOfYoungers=0;
	
	public int multipliedst;
	
	private IndividualsGroup group;
	
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
		this.group = group;
		if (reproduction > 0) {
			if (GenotypeHelper.isMale(group.getGenotype())) {
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
	
	public IndividualsGroupState(IndividualsGroupState sourceGroupState) {
		strength = sourceGroupState.strength;
		survival = sourceGroupState.survival;
		competitiveness = sourceGroupState.competitiveness;
		reproduction = sourceGroupState.reproduction;
		fertility = sourceGroupState.fertility;
		amplexusRepeat = sourceGroupState.amplexusRepeat;
		voracity = sourceGroupState.voracity;
		group = sourceGroupState.group;
		isMatureFemale = sourceGroupState.isMatureFemale;
		isMatureMale = sourceGroupState.isMatureMale;
		posterityComposition = sourceGroupState.posterityComposition;
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
		return group.getGenotype();
	}

	public int getAge() {
		return group.getAge();
	}

	public IndividualsGroup getGroup() {
		return group;
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
		return group.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		IndividualsGroupState other = (IndividualsGroupState) obj;
		return group.equals(other.group);
	}
	
}
