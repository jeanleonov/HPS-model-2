package point_initialization.inputs_areas;

import java.util.LinkedHashMap;
import java.util.Map;

import point.components.GenotypeHelper;
import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotFloat;
import exceptions.Exceptions.NotGenotype;
import exceptions.Exceptions.WrongFileStructure;
import exceptions.Exceptions.WrongParentsPair;

public class Posterity {

	private point.components.Posterity posterity;
	private final static String INPUT_AREA = "Posterity";
	
	public Posterity(String input) throws InvalidInput {
		posterity = new point.components.Posterity();
		String[][] rows = CSVHelper.getTrimmedTable(input);
		if (CSVHelper.isInputsEmpty(rows))
			return;
		if (!CSVHelper.isTableConsistent(rows))
			throw new WrongFileStructure("Rows are not consistent", INPUT_AREA);
		parseRows(rows);
	}
	
	private void parseRows(String[][] rows) throws InvalidInput {
		String[] header = rows[0];
		checkAndTrimHeader(header);
		for (int i=1; i<rows.length; i++)
			parseRow(header, rows[i], i+1);
	}
	
	private void checkAndTrimHeader(String[] header) throws NotGenotype {
		for (int i=2; i<header.length; i++)
			if (!GenotypeHelper.isGenotype(header[i]))
				throw new NotGenotype(header[i], INPUT_AREA, 1, i+1);
	}
	
	private void parseRow(String[] header, String[] row, int rowNumber) throws InvalidInput {
		if (!GenotypeHelper.isGenotype(row[0]))
			throw new NotGenotype(row[0], INPUT_AREA, rowNumber, 1);
		if (!GenotypeHelper.isGenotype(row[1]))
			throw new NotGenotype(row[1], INPUT_AREA, rowNumber, 2);
		String mother, father;
		if (GenotypeHelper.isFemale(row[0])) {
			mother = row[0];
			if (!GenotypeHelper.isMale(row[1]))
				throw new WrongParentsPair(row[0], row[1], INPUT_AREA, rowNumber, 1);
			father = row[1];
		}
		else {
			father = row[0];
			if (!GenotypeHelper.isFemale(row[1]))
				throw new WrongParentsPair(row[0], row[1], INPUT_AREA, rowNumber, 1);
			mother = row[1];
		}
		Map<String,Float> composition = new LinkedHashMap<>();
		for (int i=2; i<row.length; i++) {
			float percentage;
			try {
				percentage = Float.parseFloat(row[i]);
			} catch (NumberFormatException e) {
				throw new NotFloat(row[i], INPUT_AREA, rowNumber, i+1);
			}
			composition.put(header[i], percentage);
		}
		posterity.addCompositionFor(mother, father, composition);
	}
	
	public point.components.Posterity getPosterity() {
		return posterity;
	}
	
}
