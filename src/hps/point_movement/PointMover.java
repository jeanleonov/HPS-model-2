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
		REPRODUCTION("Reproduction"),
		COMPETITION("Competition"),
		DIEING("Dieing"),
		MOVEMENT("Movement"),
		GROWING_UP("Growing up");
		private String name;
		private IterationSubStep(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	private Random rand = new Random();
	
	public PointMover(Point firstPoint) {
		currentPoint = new Point(firstPoint);
		year = 1;
		subscribers = new LinkedList<>();
	}
	
	public Point getCurrentPoint() {
		return currentPoint;
	}
	
	private void notifySubscribers(IterationSubStep justFinishedSubStep) throws Throwable {
		for (StatisticSubcriber subscriber : subscribers)
			subscriber.saveSystemState(currentPoint, year, justFinishedSubStep);
	}
	
	public void registerSubscriber(StatisticSubcriber subscriber) {
		subscribers.add(subscriber);
	}
	
	
	public void move() throws Throwable {
		for (int i=1; i<=(Integer)CMDArgument.YEARS.getValue(); i++) {
			for (Habitat habitat : currentPoint.getHabitats())
				nextYearIn(habitat);
			year++;
		}
	}
	
	private void nextYearIn(Habitat habitat) throws Throwable {
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
	private int numberOfAvailableFemales;
	private double totalAttractivenessOfAvailable;
	
	private static class ApplicationPack {
		List<IndividualsGroupState> malesGroups;
		double totalAttractivenessOfCandidates;
	}
	
	private static class Female {
		IndividualsGroupState femalesGroup;
		int femaleNumber;
		Female(IndividualsGroupState group, int number) {
			femalesGroup = group;
			femaleNumber = number;
		}
		@Override
		public int hashCode() {
			return femalesGroup.hashCode() + femaleNumber*20;
		}
		@Override
		public boolean equals(Object arg0) {
			Female other = (Female)arg0;
			return other.femalesGroup.equals(this.femalesGroup) && femaleNumber==other.femaleNumber;
		}
	}
	
	
	private void reproductionPhaseProcessing(Habitat habitat) throws Throwable {
		initiateMultipliedst(habitat);
		for (int i=0; i<MAX_NUMBER_OF_REPRODUCTION_CIRCLES; i++) {
			initiateMalesAndFemales(habitat);
			if (males.size()==0 || numberOfLonelyFemales==0)
				continue;
			Map<Female, ApplicationPack> applicationsForFemales;
			applicationsForFemales = determineFemalesPopularity();
			for (Entry<Female, ApplicationPack> entry : applicationsForFemales.entrySet()) {
				double point = (double) Math.random() * entry.getValue().totalAttractivenessOfCandidates;
				double currentSum = 0.0;
				int j;
				for (j=0; j<entry.getValue().malesGroups.size(); j++) {
					currentSum += entry.getValue().malesGroups.get(j).getReproduction();
					if (point <= currentSum)
						break;
				}
				createPosterity(entry.getKey().femalesGroup, entry.getValue().malesGroups.get(j), habitat);
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
			if (group.isMatureMale() && group.strength>0)
				males.add(group);
			else if (group.isMatureFemale() && group.getNotMultipliedst()>0) {
				lonelyFemales.add(group);
				numberOfLonelyFemales += group.getNotMultipliedst();
			}
		}
	}
	
	private Map<Female, ApplicationPack> determineFemalesPopularity() {
		Map<Female, ApplicationPack> applicationsForFemales = new LinkedHashMap<>();
		for (IndividualsGroupState maleGroup : males) {
			for (int i=0; i<maleGroup.multipliedst; i++)
				if (Math.random() <= maleGroup.getAmplexusRepeat())
					chooseFemaleFor(maleGroup, applicationsForFemales);
			for (int i=0; i<maleGroup.strength-maleGroup.multipliedst; i++)
				chooseFemaleFor(maleGroup, applicationsForFemales);
		}
		return applicationsForFemales;
	}
	
	private void chooseFemaleFor(IndividualsGroupState maleGroup,
								 Map<Female, ApplicationPack> applicationsForFemales) {
		fillAvailableFemales();
		if (numberOfAvailableFemales == 0)
			return;
		Female chosen = chooseFemaleFromAvailable();
		if (chosen == null)
			return;
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
		numberOfAvailableFemales = Math.abs(rand.nextInt()%(MAX_SIZE_OF_FEMALES_LIST+1));
		for (int i=0, j; i<numberOfAvailableFemales; i++) {
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
		for (int i=numberOfAvailableFemales; i<availableFemales.length; i++)
			availableFemales[i] = null;
	}
	
	private Female chooseFemaleFromAvailable() {
		double point = (double) Math.random() * totalAttractivenessOfAvailable;
		double currentSum = 0.0;
		int i;
		for (i=0; i<numberOfAvailableFemales; i++) {
			currentSum += availableFemales[i].getReproduction();
			if (point <= currentSum)
				break;
		}
		IndividualsGroupState femalesGroup = availableFemales[i]; 
		int possibleFemalesInGroup = femalesGroup.strength - femalesGroup.multipliedst;
		if (possibleFemalesInGroup == 0)
			return null;
		return new Female(femalesGroup, rand.nextInt(possibleFemalesInGroup));
	}
	
	private void createPosterity(IndividualsGroupState mother,
								 IndividualsGroupState father,
								 Habitat habitat) {
		Map<String,Double> posterityComposition = mother.getPosterityComposition(father.getGenotype());
		if (posterityComposition == null)
			return;
		for (Entry<String,Double> entry : posterityComposition.entrySet()) {
			int born = (int) (father.getFertility() * mother.getFertility() * entry.getValue());
			if (born > 0) {
				IndividualsGroup childsGroup = new IndividualsGroup(entry.getKey(), 0);
				habitat.getState(childsGroup).strength += born;
			}
		}
		father.multipliedst += 1;
		mother.multipliedst += 1;
	}

	/*
	=======================================================
	---  COMPETITION:  ------------------------------------
	*/
	private void competitionPhaseProcessing(Habitat habitat) throws Throwable {
		changeHabitatResources(habitat);
		simulateCompetition(habitat);
		notifySubscribers(IterationSubStep.COMPETITION);
	}
	
	private void changeHabitatResources(Habitat habitat) {
		habitat.setResources(habitat.getScenario().getResources(year, habitat.getResources()));
	}
	
	private void simulateCompetition(Habitat habitat) throws Throwable {
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
				if (Math.random() > group.getCompetitiveness()/coeficient)
					dead++;
			group.strength -= dead;
		}
	}
	
	/*
	=======================================================
	---  DIEING:  -----------------------------------------
	*/
	private void diePhaseProcessing(Habitat habitat) throws Throwable {
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int dead = 0;
			for(int i=0; i<group.strength; i++)
				if(Math.random() > group.getSurvival())
					dead++;
			group.strength -= dead;
		}
		notifySubscribers(IterationSubStep.DIEING);
	}

	/*
	=======================================================
	---  MOVEMENT:  ---------------------------------------
	*/
	private void movementPhaseProcessing(Habitat habitat) throws Throwable {
		immigration(habitat);
		innerMigrationAndEmigration(habitat);
	}
	
	private void immigration(Habitat habitat) {
		Map<IndividualsGroup, Integer> immigration = habitat.getScenario().getImmigration(year);
		if (immigration == null)
			return;
		for(Entry<IndividualsGroup, Integer> entry : immigration.entrySet())
			habitat.getState(entry.getKey()).strength += entry.getValue();
	}
	
	private void innerMigrationAndEmigration(Habitat habitat) throws Throwable {
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
	private void growingUpPhaseProcessing(Habitat habitat) throws Throwable {
		LinkedList<IndividualsGroupState> newGroups = new LinkedList<>();
		for(Entry<IndividualsGroup, IndividualsGroupState> entry : habitat.getGroupsStates().entrySet()) {
			String genotype = entry.getKey().getGenotype();
			int newAge = entry.getKey().getAge() + 1;
			IndividualsGroup growedGroup = new IndividualsGroup(genotype, newAge);
			IndividualsGroupState groupState = habitat.getGroupsStates().get(growedGroup);
			if (groupState == null)
				newGroups.add(new IndividualsGroupState(growedGroup, entry.getValue().strength,
														habitat.getViability(), habitat.getPosterity()));
			else
				groupState.strengthOfYoungers = entry.getValue().strength;
		}
		for(Entry<IndividualsGroup, IndividualsGroupState> entry : habitat.getGroupsStates().entrySet()) {
			entry.getValue().strength = entry.getValue().strengthOfYoungers;
			entry.getValue().strengthOfYoungers = 0;
		}
		for(IndividualsGroupState group : newGroups)
			habitat.getGroupsStates().put(group.getGroup(), group);
		notifySubscribers(IterationSubStep.GROWING_UP);
	}
}
