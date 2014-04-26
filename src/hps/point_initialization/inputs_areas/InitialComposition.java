package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.WrongFileStructure;

import java.util.LinkedHashMap;
import java.util.Map;

import hps.point.components.GenotypeHelper;
import hps.point.components.IndividualsGroup;

public class InitialComposition {

	private Map<IndividualsGroup, Integer> composition;
	private final static String INPUT_AREA = "Initial composition";
	
	public InitialComposition(String input) throws InvalidInput {
		composition = new LinkedHashMap<>();
		String[][] rows = CSVHelper.getTrimmedTable(input);
		if (CSVHelper.isInputsEmpty(rows))
			return;
		if (rows.length < 2)
			throw new WrongFileStructure("File should containe 2 rows", INPUT_AREA);
		if (!CSVHelper.isTableConsistent(rows))
			throw new WrongFileStructure("Rows are not consistent", INPUT_AREA);
		processRows(rows[0], rows[1]);
	}
	
	private void processRows(String[] header, String[] values) throws InvalidInput {
		for(int i=0; i<header.length; i++) {
			IndividualsGroup group = parseIndividualsGroup(header[i], i+1);
			int strength;
			try {
				strength = Integer.parseInt(values[i]);
			} catch(NumberFormatException e) {
				throw new NotInteger(values[i], INPUT_AREA, 2, i+1);
			}
			composition.put(group, strength);
		}
	}
	
	private IndividualsGroup parseIndividualsGroup(String source, int column) throws InvalidInput {
		String[] valuePair = source.split("-");
		if (valuePair.length != 2)
			throw new WrongFileStructure("Header cell should be like \"xRxR-3\"", INPUT_AREA);
		String genotype = valuePair[0];
		if (!GenotypeHelper.isGenotype(genotype))
			throw new NotGenotype(genotype, INPUT_AREA, 1, column);
		int age;
		try {
			age = Integer.parseInt(valuePair[1]);
		} catch(NumberFormatException e) {
			throw new NotInteger(valuePair[1], INPUT_AREA, 1, column);
		}
		return new IndividualsGroup(genotype, age);
	}
	
	public Map<IndividualsGroup, Integer> getInitialComposition() {
		return composition;
	}
}
