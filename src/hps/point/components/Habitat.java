package hps.point.components;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Habitat {
	
	private String habitatName;
	private LinkedHashMap<IndividualsGroup, IndividualsGroupState> groupsStates;
	private Viability viability;
	private Posterity posterity;
	private Scenario scenario;
	private Map<String, Double> migrationProbabilities;
	private double resources;
	
	public final static String EXTERNAL_WORLD = "-";
	
	public Habitat(LinkedHashMap<IndividualsGroup, IndividualsGroupState> groupsStates,
			       Viability viability, Posterity posterity, Scenario scenario,
			       Map<String, Double> migrationProbabilities, double resources,
			       String habitatName) {
		this.groupsStates = groupsStates;
		this.viability = viability;
		this.posterity = posterity;
		this.scenario = scenario;
		this.migrationProbabilities = migrationProbabilities;
		this.resources = resources;
		this.habitatName = habitatName;
	}
	
	public Habitat(Habitat habitat) {
		groupsStates = new LinkedHashMap<>();
		for (Entry<IndividualsGroup, IndividualsGroupState> entry : habitat.getGroupsStates().entrySet()) {
			IndividualsGroupState groupState = new IndividualsGroupState(entry.getValue());
			groupsStates.put(entry.getKey(), groupState);
		}
		this.viability = habitat.viability;
		this.posterity = habitat.posterity;
		this.scenario = habitat.scenario;
		this.migrationProbabilities = habitat.migrationProbabilities;
		this.resources = habitat.resources;
		this.habitatName = habitat.habitatName;
	}

	public IndividualsGroupState getState(IndividualsGroup group) {
		IndividualsGroupState state = groupsStates.get(group);
		if (state == null) {
			state = new IndividualsGroupState(group, 0, viability, posterity);
			groupsStates.put(group, state);
		}
		return state;
	}
	
	public Scenario getScenario() {
		return scenario;
	}
	
	public Map<String, Double> getMigrationProbabilities() {
		return migrationProbabilities;
	}
	
	public LinkedHashMap<IndividualsGroup, IndividualsGroupState> getGroupsStates() {
		return groupsStates;
	}
	
	public void setGroupsStates(LinkedHashMap<IndividualsGroup, IndividualsGroupState> map) {
		groupsStates = map;
	}

	public double getResources() {
		return resources;
	}

	public void setResources(double resources) {
		this.resources = resources;
	}

	public String getHabitatName() {
		return habitatName;
	}

	public Viability getViability() {
		return viability;
	}

	public Posterity getPosterity() {
		return posterity;
	}
}
