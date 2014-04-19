package point_initialization.inputs_areas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotInteger;
import exceptions.Exceptions.UnknownHabitat;
import exceptions.Exceptions.WrongFileStructure;

public class Neighbors {
	
	private Map<String, Float> migrationProbabilities;

	public Neighbors(String input, Set<String> existingHabitats) throws InvalidInput {
		String[] lines = input.split("\n");
		migrationProbabilities = new LinkedHashMap<>();
		if (isInputsEmpty(lines))
			return;
		if (lines.length < 2)
			throw new WrongFileStructure("Neighbors", "File should containe 2 rows");
		String[] header = lines[0].split(";");
		String[] values = lines[1].split(";");
		if (header.length != values.length)
			throw new WrongFileStructure("Neighbors", "");
		processRows(header, values, existingHabitats);
	}
	
	private void processRows(String[] header, String[] values, Set<String> existingHabitats) throws InvalidInput {
		for(int i=0; i<header.length; i++) {
			String habitat = header[i].trim();
			if (!existingHabitats.contains(habitat))
				throw new UnknownHabitat(habitat);
			float accessibility;
			try {
				accessibility = Float.parseFloat(values[i].trim());
			} catch(NumberFormatException e) {
				throw new NotInteger(values[i].trim());
			}
			migrationProbabilities.put(habitat, accessibility);
		}
	}
	
	private boolean isInputsEmpty(String[] lines) {
		if (lines.length == 0)
			return true;
		for(String line : lines)
			if (!line.trim().isEmpty())
				return false;
		return true;
	}
	
	public Map<String, Float> getMigrationProbabilities() {
		return migrationProbabilities;
	}
	
}
