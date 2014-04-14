package point.components;

import java.util.Map;

public class Habitat {
	
	private Map<IndividualsGroup, IndividualsGroupState> groupStates;
	private Viability viability;
	private Posterity posterity;
	
	public Habitat(Map<IndividualsGroup, IndividualsGroupState> groupStates,
			       Viability viability, Posterity posterity) {
		this.groupStates = groupStates;
		this.viability = viability;
		this.posterity = posterity;
	}

	public IndividualsGroupState getState(IndividualsGroup group) {
		IndividualsGroupState state = groupStates.get(group);
		if (state == null) {
			state = new IndividualsGroupState(group, 0, viability, posterity);
			groupStates.put(group, state);
		}
		return state;
	}
	
	public void setGroupStrength(IndividualsGroup group, int strength) {		
		groupStates.get(group).strength = strength;
	}
	
	public void increaseGroup(IndividualsGroup group, int toAdd) {
		getState(group).strength += toAdd;
	}
	
	public void reduceGroup(IndividualsGroup group, int toSubtract) {
		getState(group).strength -= toSubtract;
	}

}
