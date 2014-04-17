package point_movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import point.Point;
import point.components.Habitat;
import point.components.IndividualsGroup;
import point.components.IndividualsGroupState;
import statistic_saving.StatisticSubcriber;

public class PointMover {

	private Point currentPoint;
	private int year;
	private List<StatisticSubcriber> subscribers;
	
	public enum IterationSubStep {
		REPRODUCTION,
		COMPETITION,
		DIEING,
		MOVEMENT,
		GROWING_UP
	}
	
	public PointMover(Point firstPoint) {
		currentPoint = firstPoint;
		year = 0;
	}
	
	public Point getCurrentPoint() {
		return currentPoint;
	}
	
	public int getYear() {
		return year;
	}
	
	private void notifySubscribers(IterationSubStep justFinishedSubStep) {
		for (StatisticSubcriber subscriber : subscribers)
			subscriber.saveSystemState(currentPoint, year, justFinishedSubStep);
	}
	
	
	public void nextYear() {
		for (Habitat habitat : currentPoint.getHabitats())
			nextYearIn(habitat);
		year++;
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
	private int numberOfMales=0, numberOfFemales=0;
	private List<IndividualsGroupState> males = new ArrayList<>();
	private List<IndividualsGroupState> females = new ArrayList<>();
	
	private void reproductionPhaseProcessing(Habitat habitat) {
		initiateMalesAndFemales(habitat);
		
		// TODO
		notifySubscribers(IterationSubStep.REPRODUCTION);
	}
	
	private void initiateMalesAndFemales(Habitat habitat) {
		numberOfMales=0;
		numberOfFemales=0;
		males.clear();
		females.clear();
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			if (group.isMatureMale()) {
				males.add(group);
				numberOfMales += group.strength;
			}
			else if (group.isMatureFemale()) {
				females.add(group);
				numberOfFemales += group.strength;
			}
		}
		for(IndividualsGroupState malesGroup : males)
			malesGroup.percentageInHabitat = (float) malesGroup.strength / numberOfMales;
		for(IndividualsGroupState femalesGroup : females)
			femalesGroup.percentageInHabitat = (float) femalesGroup.strength / numberOfFemales;
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
		float totalSumOfAntiCompetetiveness = 0;
		float totalSumOfVoracity = 0;
		float weightedTotalSumOfVoracity = 0;
		int numberOfIndividuals = 0;
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			totalSumOfAntiCompetetiveness += group.strength * (1f - group.getCompetitiveness());
			totalSumOfVoracity += group.strength * group.getVoracity();
			numberOfIndividuals += group.strength;
			weightedTotalSumOfVoracity += group.strength * (group.getVoracity() * group.getCompetitiveness());
		}
		if(totalSumOfVoracity <= habitat.getResources())
			return;
		float coeficient = totalSumOfVoracity-habitat.getResources();
		coeficient /= habitat.getResources();
		coeficient *= numberOfIndividuals;
		coeficient /= totalSumOfAntiCompetetiveness;
		coeficient *= weightedTotalSumOfVoracity;
		coeficient /= totalSumOfVoracity;
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int dead = 0;
			for(int i=0; i<group.strength; i++)
				if (Math.random() <= 1f - group.getCompetitiveness()/coeficient)
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
		Map<String, Float> migrationProbabilities = habitat.getMigrationProbabilities();
		Set<Entry<String, Float>> entries = migrationProbabilities.entrySet();
		Float totalWeight = 0f;
		for (Entry<String, Float> entry : entries)
			totalWeight += entry.getValue();
		for(IndividualsGroupState group : habitat.getGroupsStates().values()) {
			int went = 0;
			for(int i=0; i<group.strength; i++) {
				double weightSum = 0;
				double point = Math.random() * totalWeight;
				for (Entry<String, Float> entry : entries) {
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

	/*
	=======================================================
	---  GROWING UP:  -------------------------------------
	*/
	private void growingUpPhaseProcessing(Habitat habitat) {
		SortedMap<IndividualsGroup, IndividualsGroupState> newGroupsStates = new TreeMap<>();
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
