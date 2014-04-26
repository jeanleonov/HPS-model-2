package hps.point.components;

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
	
	private double computeSurvival(int age, GenotypeViability genotypeViability) {
		if (age > genotypeViability.lifetime)
			return 0.0;
		int aS = genotypeViability.survivalAchieveAge;
		double S = genotypeViability.survival;
		if (age <= aS) {
			if (age == aS)
				return S;
			double kbS = genotypeViability.survivalCoefficientBeforeAchiveAge;
			double curS = genotypeViability.survivalFirst;
			for (int curAge=0; curAge<age; curAge++)
				curS += (S - curS) * kbS;
		}
		double kS = genotypeViability.survivalCoefficient;
		double curS = S;
		for (int curAge=aS; curAge<age; curAge++)
			curS += (1 - curS) * kS;
		return curS;
	}
	private double computeCompetitiveness(int age, GenotypeViability genotypeViability) {
		if (age > genotypeViability.lifetime)
			return 0.0;
		int aC = genotypeViability.competitivenessAchieveAge;
		double C = genotypeViability.competitiveness;
		if (age <= aC) {
			if (age == aC)
				return C;
			double kbC = genotypeViability.competitivenessCoefficientBeforeAchiveAge;
			double curC = genotypeViability.competitivenessFirst;
			for (int curAge=0; curAge<age; curAge++)
				curC += (C - curC) * kbC;
		}
		double kC = genotypeViability.competitivenessCoefficient;
		double curC = C;
		for (int curAge=aC; curAge<age; curAge++)
			curC += (1 - curC) * kC;
		return curC;
	}
	private double computeReproduction(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.reproduction;
			return 0.0;
		}
		double curR = genotypeViability.reproduction;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curR += (1 - curR) * genotypeViability.reproductionCoefficient; 
		return curR;
	}
	private double computeFertility(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.fertility;
			return 0.0;
		}
		double curF = genotypeViability.fertility;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curF += (1 - curF) * genotypeViability.fertilityCoefficient; 
		return curF;
	}
	private double computeAmplexusRepeat(int age, GenotypeViability genotypeViability) {
		if (age <= genotypeViability.spawning) {
			if (age == genotypeViability.spawning)
				return genotypeViability.amplexusRepeat;
			return 0.0;
		}
		double curAR = genotypeViability.amplexusRepeat;
		for (int curAge=genotypeViability.spawning; curAge<age; curAge++)
			curAR += (1 - curAR) * genotypeViability.amplexusRepeatCoefficient; 
		return curAR;
	}
	private double computeVoracity(int age, GenotypeViability genotypeViability) {
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
		public int lifetime;
		public int spawning;
		public double survival;
		public int survivalAchieveAge;
		public double survivalCoefficient;
		public double survivalFirst;
		public double survivalCoefficientBeforeAchiveAge;
		public double competitiveness;
		public int competitivenessAchieveAge;
		public double competitivenessCoefficient;
		public double competitivenessFirst;
		public double competitivenessCoefficientBeforeAchiveAge;
		public double reproduction;
		public double reproductionCoefficient;
		public double fertility;
		public double fertilityCoefficient;
		public double amplexusRepeat;
		public double amplexusRepeatCoefficient;
		public double voracity01;
		public double voracity02;
		public double voracity03;
		public double voracity04;
		public double voracity05;
		public double voracity06;
		public double voracity07;
		public double voracity08;
		public double voracity09;
		public double voracity10;
	}
	
	public static class IndividualsGroupViability {
		double survival;
		double competitiveness;
		double reproduction;
		double fertility;
		double amplexusRepeat;
		double voracity;
	}
}
