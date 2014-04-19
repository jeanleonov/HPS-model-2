package point_initialization.inputs_areas;

import java.util.LinkedHashMap;
import java.util.Map;

import point.components.GenotypeHelper;
import point.components.IndividualsGroup;
import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotGenotype;
import exceptions.Exceptions.NotInteger;
import exceptions.Exceptions.WrongFileStructure;

public class InitialComposition {

	private Map<IndividualsGroup, Integer> composition;
	private String[] inputs;
	
	public InitialComposition(String[] lines) throws InvalidInput {
		composition = new LinkedHashMap<>();
		inputs = lines;
		if (isInputsEmpty())
			return;
		if (lines.length < 2)
			throw new WrongFileStructure("Initial composition", "File should containe 2 rows");
		String[] header = lines[0].split(";");
		String[] values = lines[1].split(";");
		if (header.length != values.length)
			throw new WrongFileStructure("Initial composition", "Mapping between individuals "+
										 "groups and its strengthes should be is a mutual");
		processRows(header, values);
	}
	
	private void processRows(String[] header, String[] values) throws InvalidInput {
		for(int i=0; i<header.length; i++) {
			IndividualsGroup group = parseIndividualsGroup(header[i]);
			int strength;
			try {
				strength = Integer.parseInt(values[i].trim());
			} catch(NumberFormatException e) {
				throw new NotInteger(values[i].trim());
			}
			composition.put(group, strength);
		}
	}
	
	private IndividualsGroup parseIndividualsGroup(String source) throws InvalidInput {
		String[] valuePair = source.split("-");
		if (valuePair.length != 2)
			throw new WrongFileStructure("Initial composition", "Header cell should be like \"xRxR-3\"");
		String genotype = valuePair[0].trim();
		if (!GenotypeHelper.isGenotype(genotype))
			throw new NotGenotype(genotype);
		int age;
		try {
			age = Integer.parseInt(valuePair[1].trim());
		} catch(NumberFormatException e) {
			throw new NotInteger(valuePair[1].trim());
		}
		return new IndividualsGroup(genotype, age);
	}
	
	private boolean isInputsEmpty() {
		if (inputs.length == 0)
			return true;
		for(String line : inputs)
			if (!line.trim().isEmpty())
				return false;
		return true;
	}
	
	public Map<IndividualsGroup, Integer> getInitialComposition() {
		return composition;
	}
}
