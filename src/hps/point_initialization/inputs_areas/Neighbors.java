package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.UnknownHabitat;
import hps.exceptions.Exceptions.WrongFileStructure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Neighbors {
	
	private Map<String, Double> migrationProbabilities;
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
			double accessibility;
			try {
				accessibility = Double.parseDouble(values[i]);
			} catch(NumberFormatException e) {
				throw new NotDouble(values[i], INPUT_AREA, 2, i+1);
			}
			migrationProbabilities.put(habitat, accessibility);
		}
	}
	
	public Map<String, Double> getMigrationProbabilities() {
		return migrationProbabilities;
	}
	
}
