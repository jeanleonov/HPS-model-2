package hps.point;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hps.point.components.Habitat;

public class Point {
	
	private List<Habitat> habitats;
	private Map<String, Habitat> namedHabitats;
	
	public Point(List<Habitat> habitats) {
		this.habitats = habitats;
		namedHabitats = new LinkedHashMap<>();
		for(Habitat habitat : habitats)
			namedHabitats.put(habitat.getHabitatName(), habitat);
	}
	
	public Point(Point sourcePoint) {
		habitats = new LinkedList<>();
		namedHabitats = new LinkedHashMap<>();
		for(Habitat habitat : sourcePoint.habitats) {
			Habitat habitatClone = new Habitat(habitat);
			habitats.add(habitatClone);
			namedHabitats.put(habitat.getHabitatName(), habitatClone);
		}
	}

	public List<Habitat> getHabitats() {
		return habitats;
	}

	public Map<String, Habitat> getNamedHabitats() {
		return namedHabitats;
	}
}
