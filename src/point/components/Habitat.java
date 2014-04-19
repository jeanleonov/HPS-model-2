package point.components;

import java.util.Map;
import java.util.SortedMap;

public class Habitat {
	
	private String habitatName;
	private SortedMap<IndividualsGroup, IndividualsGroupState> groupsStates;
	private Viability viability;
	private Posterity posterity;
	private Scenario scenario;
	private Map<String, Float> migrationProbabilities;
	private float resources;
	
	public final static String EXTERNAL_WORLD = "-";
	
	public Habitat(SortedMap<IndividualsGroup, IndividualsGroupState> groupsStates,
			       Viability viability, Posterity posterity, Scenario scenario,
			       Map<String, Float> migrationProbabilities, float resources,
			       String habitatName) {
		this.groupsStates = groupsStates;
		this.viability = viability;
		this.posterity = posterity;
		this.scenario = scenario;
		this.migrationProbabilities = migrationProbabilities;
		this.resources = resources;
		this.habitatName = habitatName;
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
	
	public Map<String, Float> getMigrationProbabilities() {
		return migrationProbabilities;
	}
	
	public SortedMap<IndividualsGroup, IndividualsGroupState> getGroupsStates() {
		return groupsStates;
	}
	
	public void setGroupsStates(SortedMap<IndividualsGroup, IndividualsGroupState> map) {
		groupsStates = map;
	}

	public float getResources() {
		return resources;
	}

	public void setResources(float resources) {
		this.resources = resources;
	}

	public String getHabitatName() {
		return habitatName;
	}
}
