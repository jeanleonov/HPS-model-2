package point.components;

import java.util.HashMap;
import java.util.Map;

public class Viability {
	
	private Map<String, GenotypeViability> genotypeViabilityMap;
	private Map<IndividualsGroup, IndividualsGroupViability> groupViabilityMap;
	
	public Viability() {
		genotypeViabilityMap = new HashMap<>();
		groupViabilityMap = new HashMap<>();
	}
	
	public void addGenotypeViability(String genotype, GenotypeViability genotypeViability) {
		genotypeViabilityMap.put(genotype, genotypeViability);
	}
	
	public IndividualsGroupViability getGroupViability(IndividualsGroup group) {
		IndividualsGroupViability groupViability = groupViabilityMap.get(group);
		if (groupViability != null)
			return groupViability;
		GenotypeViability genotypeViability = genotypeViabilityMap.get(group.getGenotype());
		groupViability = new IndividualsGroupViability();
		int age = group.getAge();
		groupViability.survival = computeSurvival(age, genotypeViability);
		groupViability.competitiveness = computeCompetitiveness(age, genotypeViability);
		groupViability.reproduction = computeReproduction(age, genotypeViability);
		groupViability.fertility = computeFertility(age, genotypeViability);
		groupViability.amplexusRepeat = computeAmplexusRepeat(age, genotypeViability);
		groupViability.voracity = computeVoracity(age, genotypeViability);
		return groupViability;
	}
	
	private float computeSurvival(int age, GenotypeViability genotypeViability) {
		if (age > genotypeViability.lifetime)
			return 0f;
		int aS = genotypeViability.survivalAchieveAge;
		float S = genotypeViability.survival;
		if (age <= aS) {
			if (age == aS)
				return S;
			float kbS = genotypeViability.survivalCoefficientBeforeAchiveAge;
			float curS = genotypeViability.survivalFirst;
			for (int curAge=0; curAge<age; curAge++)
				curS += (S - curS) * kbS;
		}
		float kS = genotypeViability.survivalCoefficient;
		float curS = S;
		for (int curAge=aS; curAge<age; curAge++)
			curS += (1 - curS) * kS;
		return curS;
	}
	private float computeCompetitiveness(int age, GenotypeViability genotypeViability) {
		if (age > genotypeViability.lifetime)
			return 0f;
		int aC = genotypeViability.competitivenessAchieveAge;
		float C = genotypeViability.competitiveness;
		if (age <= aC) {
			if (age == aC)
				return C;
			float kbC = genotypeViability.competitivenessCoefficientBeforeAchiveAge;
			float curC = genotypeViability.competitivenessFirst;
			for (int curAge=0; curAge<age; curAge++)
				curC += (C - curC) * kbC;
		}
		float kC = genotypeViability.competitivenessCoefficient;
		float curC = C;
		for (int curAge=aC; curAge<age; curAge++)
			curC += (1 - curC) * kC;
		return curC;
	}
	private float computeReproduction(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.reproduction;
			return 0f;
		}
		float curR = genotypeViability.reproduction;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curR += (1 - curR) * genotypeViability.reproductionCoefficient; 
		return curR;
	}
	private float computeFertility(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.fertility;
			return 0f;
		}
		float curF = genotypeViability.fertility;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curF += (1 - curF) * genotypeViability.fertilityCoefficient; 
		return curF;
	}
	private float computeAmplexusRepeat(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.amplexusRepeat;
			return 0f;
		}
		float curAR = genotypeViability.amplexusRepeat;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curAR += (1 - curAR) * genotypeViability.amplexusRepeatCoefficient; 
		return curAR;
	}
	private float computeVoracity(int age, GenotypeViability genotypeViability) {
		switch (age + 1) {
		case 1:
			return genotypeViability.voracity01;
		case 2:
			return genotypeViability.voracity02;
		case 3:
			return genotypeViability.voracity03;
		case 4:
			return genotypeViability.voracity04;
		case 5:
			return genotypeViability.voracity05;
		case 6:
			return genotypeViability.voracity06;
		case 7:
			return genotypeViability.voracity07;
		case 8:
			return genotypeViability.voracity08;
		case 9:
			return genotypeViability.voracity09;
		case 10:
			return genotypeViability.voracity10;
		default:
			return genotypeViability.voracity10;
		}
	}
	
	public static class GenotypeViability {
		int lifetime;
		int spawning;
		float survival;
		int survivalAchieveAge;
		float survivalCoefficient;
		float survivalFirst;
		float survivalCoefficientBeforeAchiveAge;
		float competitiveness;
		int competitivenessAchieveAge;
		float competitivenessCoefficient;
		float competitivenessFirst;
		float competitivenessCoefficientBeforeAchiveAge;
		float reproduction;
		float reproductionCoefficient;
		float fertility;
		float fertilityCoefficient;
		float amplexusRepeat;
		float amplexusRepeatCoefficient;
		float voracity01;
		float voracity02;
		float voracity03;
		float voracity04;
		float voracity05;
		float voracity06;
		float voracity07;
		float voracity08;
		float voracity09;
		float voracity10;
	}
	
	public static class IndividualsGroupViability {
		float survival;
		float competitiveness;
		float reproduction;
		float fertility;
		float amplexusRepeat;
		float voracity;
	}
}
