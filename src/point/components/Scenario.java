package point.components;

import java.util.HashMap;
import java.util.Map;

public class Scenario {

	private Map<Integer, YearEvents> history;
	
	public Scenario() {
		history = new HashMap<>();
	}
	
	public void addEvent(int year, Map<IndividualsGroup, Integer> immigration,
						 Double addedResources, Double resourcesMultiplication, Double newResources) {
		YearEvents events = new YearEvents();
		events.immigration = immigration;
		events.addedResources = addedResources;
		events.resourcesMultiplication = resourcesMultiplication;
		events.newResources = newResources;
		history.put(year, events);
	}
	
	public Map<IndividualsGroup, Integer> getImmigration(int year) {
		YearEvents events = history.get(year);
		if (events == null)
			return null;
		return events.immigration;
	}
	
	public double getResources(int year, double currentResources) {
		YearEvents events = history.get(year);
		if (events == null)
			return currentResources;
		if (events.addedResources != null)
			return currentResources + events.addedResources;
		if (events.resourcesMultiplication != null)
			return currentResources * events.resourcesMultiplication;
		if (events.newResources != null)
			return events.newResources;
		return currentResources;
	}
	
	
	private static class YearEvents {		
		Map<IndividualsGroup, Integer> immigration;
		Double addedResources;
		Double resourcesMultiplication;
		Double newResources;
	}
	
}
