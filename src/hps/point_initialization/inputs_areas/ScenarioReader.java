package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.ConflictingData;
import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.WrongFileStructure;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import hps.point.components.GenotypeHelper;
import hps.point.components.IndividualsGroup;

public class ScenarioReader {

	private hps.point.components.Scenario scenario;
	public final static String INPUT_AREA = "Scenario";
	
	private final static String PLUS_COLUMN_NAME_PATTER = "(?i)Resource *\\+";
	private final static String MULTIPLICATE_COLUMN_NAME_PATTER = "(?i)Resource *\\*";
	private final static String NEW_RESOURCE_COLUMN_NAME_PATTER = "(?i)Resource *\\=";
	
	private int plusColumnNumber = -1;
	private int multiplicateColumnNumber = -1;
	private int newResourceColumnNumber = -1;
	
	private List<IndividualsGroup> immigratedGroups;
	
	public ScenarioReader(String input) throws InvalidInput {
		immigratedGroups = new LinkedList<>();
		scenario = new hps.point.components.Scenario();
		String[][] rows = CSVHelper.getTrimmedTable(input);
		if (CSVHelper.isInputsEmpty(rows))
			return;
		if (!CSVHelper.isTableConsistent(rows))
			throw new WrongFileStructure("Rows are not consistent", INPUT_AREA);
		parseRows(rows);
	}
	
	private void parseRows(String[][] rows) throws InvalidInput {
		String[] header = rows[0];
		parseHeader(header);
		for (int i=1; i<rows.length; i++) {
			parseRow(header, rows[i], i+1);
		}
	}
	
	private void parseHeader(String[] header) throws InvalidInput {
		for (int i=1; i<header.length; i++) {
			if (header[i].matches(PLUS_COLUMN_NAME_PATTER))
				plusColumnNumber = i;
			else if (header[i].matches(MULTIPLICATE_COLUMN_NAME_PATTER))
				multiplicateColumnNumber = i;
			else if (header[i].matches(NEW_RESOURCE_COLUMN_NAME_PATTER))
				newResourceColumnNumber = i;
			else
				for (;i<header.length; i++)
					immigratedGroups.add(parseIndividualsGroup(header[i], i+1));
		}
	}
	
	private IndividualsGroup parseIndividualsGroup(String source, int column) throws InvalidInput {
		String[] valuePair = source.split("-");
		if (valuePair.length != 2)
			throw new WrongFileStructure("Unrecognized column name. It should be "
										+"\"Resource+\" or \"Resource*\" or \"Resource=\" "
										+"or like \"xRxR-3\"", INPUT_AREA);
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
	
	private void parseRow(String[] header, String[] row, int rowNumber) throws InvalidInput {
		int year;
		Double  addResource=null, multiplicateResource=null, newResource=null;
		Map<IndividualsGroup,Integer> composition = new LinkedHashMap<>();
		try {
			year = Integer.parseInt(row[0]);
		} catch (NumberFormatException e ) {
			throw new NotInteger(row[0], INPUT_AREA, rowNumber, 1);
		}
		for (int i=1; i<row.length; i++) {
			if (row[i].equals("-"))
				continue;
			if (plusColumnNumber == i)
				try {
					addResource = Double.parseDouble(row[i]);
				} catch(NumberFormatException e) {
					throw new NotDouble(row[i], INPUT_AREA, rowNumber, i+1);
				}
			else if (multiplicateColumnNumber == i)
				try {
					multiplicateResource = Double.parseDouble(row[i]);
				} catch(NumberFormatException e) {
					throw new NotDouble(row[i], INPUT_AREA, rowNumber, i+1);
				}
			else if (newResourceColumnNumber == i)
				try {
					newResource = Double.parseDouble(row[i]);
				} catch(NumberFormatException e) {
					throw new NotDouble(row[i], INPUT_AREA, rowNumber, i+1);
				}
			else {
				for (int j=0, strength; i<header.length; i++, j++) {
					try {
						strength = Integer.parseInt(row[i]);
					} catch (NumberFormatException e) {
						throw new NotInteger(row[i], INPUT_AREA, rowNumber, i+1);
					}
					composition.put(immigratedGroups.get(j), strength);
				}
			}
		}
		if (addResource != null && multiplicateResource != null ||
			addResource != null && newResource != null ||
			multiplicateResource != null && newResource != null)
			throw new ConflictingData("In one year resources could be added OR multiplied OR replaced with new resources (ONE OF)",
									  INPUT_AREA, rowNumber, 3);
		scenario.addEvent(year, composition, addResource, multiplicateResource, newResource);
	}

	public hps.point.components.Scenario getScenario() {
		return scenario;
	}

}
