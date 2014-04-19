package point_initialization.inputs_areas;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotFloat;
import exceptions.Exceptions.UnknownHabitat;
import exceptions.Exceptions.WrongFileStructure;

public class Neighbors {
	
	private Map<String, Float> migrationProbabilities;
	private final static String INPUT_AREA = "Neighbors";

	public Neighbors(String input, Set<String> existingHabitats) throws InvalidInput {
		migrationProbabilities = new LinkedHashMap<>();
		String[][] rows = CSVHelper.getTrimmedTable(input);
		if (CSVHelper.isInputsEmpty(rows))
			return;
		if (rows.length < 2)
			throw new WrongFileStructure("File should containe 2 rows", INPUT_AREA);
		if (!CSVHelper.isTableConsistent(rows))
			throw new WrongFileStructure("Rows are not consistent", INPUT_AREA);
		processRows(rows[0], rows[1], existingHabitats);
	}
	
	private void processRows(String[] header, String[] values, Set<String> existingHabitats) throws InvalidInput {
		for(int i=0; i<header.length; i++) {
			String habitat = header[i];
			if (!existingHabitats.contains(habitat))
				throw new UnknownHabitat(habitat, INPUT_AREA, 1, i+1);
			float accessibility;
			try {
				accessibility = Float.parseFloat(values[i]);
			} catch(NumberFormatException e) {
				throw new NotFloat(values[i], INPUT_AREA, 2, i+1);
			}
			migrationProbabilities.put(habitat, accessibility);
		}
	}
	
	public Map<String, Float> getMigrationProbabilities() {
		return migrationProbabilities;
	}
	
}
