package hps.point_movement;

import hps.point.Point;
import hps.point.components.Habitat;
import hps.point.components.IndividualsGroup;
import hps.point.components.IndividualsGroupState;
import hps.statistic_saving.StatisticSubcriber;
import hps.tools.CMDArgument;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class PointMover {

	private Point currentPoint;
	private int year;
	private List<StatisticSubcriber> subscribers;
	
	private final static int
	MAX_SIZE_OF_FEMALES_LIST = 10,
	MAX_NUMBER_OF_REPRODUCTION_CIRCLES = 10;
	
	public enum IterationSubStep {
		REPRODUCTION,
		COMPETITION,
		DIEING,
		MOVEMENT,
		GROWING_UP
	}
	
	private Random rand = new Random();
	
	public PointMover(Point firstPoint) {
		currentPoint = firstPoint;
		year = 0;
	}
	
	public Point getCurrentPoint() {
		return currentPoint;
	}
	
	private void notifySubscribers(IterationSubStep justFinishedSubStep) {
		subscribers.stream().forEach(subscriber -> subscriber.saveSystemState(currentPoint, year, justFinishedSubStep));
	}
	
	
	public void move() {
		for (int i=1; i<=(Integer)CMDArgument.YEARS.getValue(); i++) {
			currentPoint.getHabitats().stream().forEach(habitat -> nextYearIn(habitat));
			year++;
		}
	}
	
	private void nextYearIn(Habitat habitat) {
		reproductionPhaseProcessing(habitat);
		competitionPhaseProcessing(habitat);
		diePhaseProcessing(habitat);
		movementPhaseProcessing(habitat);
		growingUpPhaseProcessing(habitat);
	}
	
	
	/*
	=======================================================
	---  REPRODUCTION:  -----------------------------------
	*/
	private int numberOfLonelyFemales;
	private List<IndividualsGroupState> males = new ArrayList<>();
	private List<IndividualsGroupState> lonelyFemales = new ArrayList<>();
	
	private IndividualsGroupState[] availableFemales = new IndividualsGroupState[MAX_SIZE_OF_FEMALES_LIST];
	private double totalAttractivenessOfAvailable;
	
	private class ApplicationPack {
		List<IndividualsGroupState> malesGroups;
		double totalAttractivenessOfCandidates;
	}
	
	
	private void reproductionPhaseProcessing(Habitat habitat) {
		initiateMultipliedst(habitat);
		for (int i=0; i<MAX_NUMBER_OF_REPRODUCTION_CIRCLES; i++) {
			initiateMalesAndFemales(habitat);
			Map<IndividualsGroupState, ApplicationPack> applicationsForFemales;
			applicationsForFemales = determineFemalesPopularity();
			for (Entry<IndividualsGroupState, ApplicationPack> entry : applicationsForFemales.entrySet()) {
				double point = (double) Math.random() * entry.getValue().totalAttractivenessOfCandidates;
				double currentSum = 0.0;
				int j;
				for (j=0; j<entry.getValue().malesGroups.size(); j++) {
					currentSum += entry.getValue().malesGroups.get(j).getReproduction();
					if (point <= currentSum)
						break;
				}
				createPosterity(entry.getKey(), entry.getValue().malesGroups.get(j), habitat);
			}
		}
		notifySubscribers(IterationSubStep.REPRODUCTION);
	}
	
	private void initiateMultipliedst(Habitat habitat) {
		for(IndividualsGroupState group : habitat.getGroupsStates().values())
			group.multipliedst = 0;
	}
	
	private void initiateMalesAndFemales(Habitat habitat) {
		numberOfLonelyFemales=0;
		males.clear();
		lonelyFemales.clear();
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			if (group.isMatureMale())
				males.add(group);
			else if (group.isMatureFemale()) {
				lonelyFemales.add(group);
				numberOfLonelyFemales += group.getNotMultipliedst();
			}
		}
	}
	
	private Map<IndividualsGroupState, ApplicationPack> determineFemalesPopularity() {
		Map<IndividualsGroupState, ApplicationPack> applicationsForFemales = new LinkedHashMap<>();
		for (IndividualsGroupState maleGroup : males) {
			for (int i=0; i<maleGroup.multipliedst; i++)
				if (Math.random() >= maleGroup.getAmplexusRepeat())
					chooseFemaleFor(maleGroup, applicationsForFemales);
			for (int i=0; i<maleGroup.strength-maleGroup.multipliedst; i++)
				chooseFemaleFor(maleGroup, applicationsForFemales);
		}
		return applicationsForFemales;
	}
	
	private void chooseFemaleFor(IndividualsGroupState maleGroup,
								 Map<IndividualsGroupState, ApplicationPack> applicationsForFemales) {
		fillAvailableFemales();
		IndividualsGroupState chosen = chooseFemaleFromAvailable();
		ApplicationPack applicationsPack = applicationsForFemales.get(chosen);
		if (applicationsPack == null) {
			applicationsPack = new ApplicationPack();
			applicationsPack.malesGroups = new LinkedList<>();
			applicationsPack.totalAttractivenessOfCandidates = 0.0;
			applicationsForFemales.put(chosen, applicationsPack);
		}
		applicationsPack.totalAttractivenessOfCandidates += maleGroup.getReproduction();
		applicationsPack.malesGroups.add(maleGroup);
	}
	
	private void fillAvailableFemales() {
		totalAttractivenessOfAvailable = 0.0;
		int numberOfFemales = Math.abs(rand.nextInt()%(MAX_SIZE_OF_FEMALES_LIST+1));
		for (int i=0, j; i<numberOfFemales; i++) {
			double point = (double) Math.random() * numberOfLonelyFemales;
			double currentSum = 0.0;
			for (j=0; i<lonelyFemales.size(); j++) {
				currentSum += lonelyFemales.get(j).getNotMultipliedst();
				if (point <= currentSum)
					break;
			}
			availableFemales[i] = lonelyFemales.get(j);
			totalAttractivenessOfAvailable += availableFemales[i].getReproduction();
		}
		for (int i=numberOfFemales; i<availableFemales.length; i++)
			availableFemales[i] = null;
	}
	
	private IndividualsGroupState chooseFemaleFromAvailable() {
		double point = (double) Math.random() * totalAttractivenessOfAvailable;
		double currentSum = 0.0;
		int i;
		for (i=0; i<availableFemales.length && availableFemales[i]!=null; i++) {
			currentSum += availableFemales[i].getReproduction();
			if (point <= currentSum)
				break;
		}
		return availableFemales[i];
	}
	
	private void createPosterity(IndividualsGroupState mother,
								 IndividualsGroupState father,
								 Habitat habitat) {
		Map<String,Double> posterityComposition = mother.getPosterityComposition(father.getGenotype());
		if (posterityComposition == null)
			return;
		for (Entry<String,Double> entry : posterityComposition.entrySet()) {
			int born = (int) (father.getFertility() * mother.getFertility() * entry.getValue());
			IndividualsGroup childsGroup = new IndividualsGroup(entry.getKey(), 0);
			habitat.getState(childsGroup).strength += born;
		}
	}

	/*
	=======================================================
	---  COMPETITION:  ------------------------------------
	*/
	private void competitionPhaseProcessing(Habitat habitat) {
		changeHabitatResources(habitat);
		simulateCompetition(habitat);
	}
	
	private void changeHabitatResources(Habitat habitat) {
		habitat.setResources(habitat.getScenario().getResources(year, habitat.getResources()));
	}
	
	private void simulateCompetition(Habitat habitat) {
		double totalSumOfAntiCompetetiveness = 0;
		double totalSumOfVoracity = 0;
		double weightedTotalSumOfVoracity = 0;
		int numberOfIndividuals = 0;
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			totalSumOfAntiCompetetiveness += group.strength * (1.0 - group.getCompetitiveness());
			totalSumOfVoracity += group.strength * group.getVoracity();
			numberOfIndividuals += group.strength;
			weightedTotalSumOfVoracity += group.strength * (group.getVoracity() * group.getCompetitiveness());
		}
		if(totalSumOfVoracity <= habitat.getResources())
			return;
		double coeficient = totalSumOfVoracity-habitat.getResources();
		coeficient /= habitat.getResources();
		coeficient *= numberOfIndividuals;
		coeficient /= totalSumOfAntiCompetetiveness;
		coeficient *= weightedTotalSumOfVoracity;
		coeficient /= totalSumOfVoracity;
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int dead = 0;
			for(int i=0; i<group.strength; i++)
				if (Math.random() <= 1.0 - group.getCompetitiveness()/coeficient)
					dead++;
			group.strength -= dead;
		}
		notifySubscribers(IterationSubStep.COMPETITION);
	}
	
	/*
	=======================================================
	---  DIEING:  -----------------------------------------
	*/
	private void diePhaseProcessing(Habitat habitat) {
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int dead = 0;
			for(int i=0; i<group.strength; i++)
				if(Math.random() <= group.getSurvival())
					dead++;
			group.strength -= dead;
		}
		notifySubscribers(IterationSubStep.DIEING);
	}

	/*
	=======================================================
	---  MOVEMENT:  ---------------------------------------
	*/
	private void movementPhaseProcessing(Habitat habitat) {
		immigration(habitat);
		innerMigrationAndEmigration(habitat);
	}
	
	private void immigration(Habitat habitat) {
		Map<IndividualsGroup, Integer> immigration = habitat.getScenario().getImmigration(year);
		for(Entry<IndividualsGroup, Integer> entry : immigration.entrySet())
			habitat.getState(entry.getKey()).strength += entry.getValue();
	}
	
	private void innerMigrationAndEmigration(Habitat habitat) {
		Map<String, Double> migrationProbabilities = habitat.getMigrationProbabilities();
		double totalVoracity = getTotalVoracity(habitat);
		Set<Entry<String, Double>> entries = migrationProbabilities.entrySet();
		Double totalWeight = (double) entries.stream().mapToDouble(entry -> entry.getValue()).sum();
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int went = 0;
			for(int i=0; i<group.strength; i++) {
				double weightSum = 0;
				double point = Math.random() * totalWeight;
				for (Entry<String, Double> entry : entries) {
					if (Math.random() <= getHabitatAttractiveness(habitat, totalVoracity))
						continue;
					weightSum += entry.getValue();
					if (point <= weightSum) {
						went++;
						if(entry.getKey().equals(Habitat.EXTERNAL_WORLD))
							continue;
						String genotype = group.getGenotype();
						int age = group.getAge();
						Habitat newHabitat = currentPoint.getNamedHabitats().get(entry.getKey());
						IndividualsGroup newGroup = new IndividualsGroup(genotype, age);
						newHabitat.getState(newGroup).strength++;
					}
				}
			}
			group.strength -= went;
		}
		notifySubscribers(IterationSubStep.MOVEMENT);
	}
	
	private double getTotalVoracity(Habitat habitat) {
		double total = 0.0;
		for (IndividualsGroupState group : habitat.getGroupsStates().values())
			total += group.strength * group.getVoracity();
		return total == 0.0? 0.00000000001 : total;
	}
	
	private double getHabitatAttractiveness(Habitat habitat, double totalVoracity) {
		double preResult = habitat.getResources() / totalVoracity;
		return preResult > 1.0 ? 1.0 : preResult;
	}

	/*
	=======================================================
	---  GROWING UP:  -------------------------------------
	*/
	private void growingUpPhaseProcessing(Habitat habitat) {
		LinkedHashMap<IndividualsGroup, IndividualsGroupState> newGroupsStates = new LinkedHashMap<>();
		for(Entry<IndividualsGroup, IndividualsGroupState> entry : habitat.getGroupsStates().entrySet()) {
			String genotype = entry.getKey().getGenotype();
			int newAge = entry.getKey().getAge() + 1;
			IndividualsGroup growedGroup = new IndividualsGroup(genotype, newAge);
			newGroupsStates.put(growedGroup, entry.getValue());
		}
		habitat.setGroupsStates(newGroupsStates);
		notifySubscribers(IterationSubStep.GROWING_UP);
	}
}
