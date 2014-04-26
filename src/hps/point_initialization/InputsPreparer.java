package hps.point_initialization;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.Negative;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.UnknownDimension;
import hps.exceptions.Exceptions.WrongDimensionType;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.exceptions.Exceptions.WrongPointNumber;
import hps.exceptions.Exceptions.WrongUsageOfDimension;
import hps.point_initialization.inputs_areas.CSVHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * Class that prepares inputs for the new point of configuration space 
 * */
public class InputsPreparer {
	
	private List<String> dimensionsIDs;
	private List<DimensionType> dimensionTypes;
	private List<Integer> totalSteps;
	private List<Integer> currentSteps;
	private Map<String,String> onPointValues;
	private int pointNumber;
	private String dimensionsToTestCSV;
	
	private final static String INPUT_AREA = "Dimensions";
	
	private enum DimensionType {
		INTEGER,
		FLOAT,
		ENUMERATION
	}

	private static final String enumeratedValuesRegex = "(:?\\{#(?<value>(?s).*?)#\\})|(:?(?<static>(?s).+?)?(:?#\\{(?<dimension>.*?)\\}))|(?<juststatic>(?s).*)";
	private static final String computedValuesRegex = "(?<static>(?s).+?)?(:?#\\[(?<dinamic>(:?\\w+)(:?\\((:?\\w+)\\))?:(:?[\\d.,]+)-(:?[\\d.,]+))\\])|(?<juststatic>(?s).*)";
	private static final String computedValueTemplateRegex = "(?<dimension>\\w+)(:?\\((?<valueName>\\w+)\\))?:(?<first>[\\d.,]+)-(?<last>[\\d.,]+)";

	public InputsPreparer(String dimensionsToTestCSV) throws InvalidInput {
		this.dimensionsToTestCSV = dimensionsToTestCSV;
		this.dimensionsIDs = new ArrayList<>();
		this.dimensionTypes = new ArrayList<DimensionType>();
		this.totalSteps = new ArrayList<>();
		this.currentSteps = new ArrayList<>();
		this.onPointValues = new HashMap<>();
		initDimensions();
	}
	
	public void setPoint(int point) throws InvalidInput {
		pointNumber = point-1;
		onPointValues.clear();
		currentSteps.clear();
		initCurrentSteps();
	}
	
	private void initDimensions() throws InvalidInput {
		String[][] rows = CSVHelper.getTrimmedTable(dimensionsToTestCSV);
		if (CSVHelper.isInputsEmpty(rows))
			return;
		if (!CSVHelper.isTableConsistent(rows) || rows[0].length != 3)
			throw new WrongFileStructure("Rows are not consistent", INPUT_AREA);
		for (int i=0; i<rows.length; i++) {
			int steps;
			try {
				steps = Integer.parseInt(rows[i][2]);
			} catch(NumberFormatException e) {
				throw new NotInteger(rows[i][2], INPUT_AREA, i+1, 3);
			}
			if (steps < 0)
				throw new Negative(steps, INPUT_AREA, i+1, 3);
			dimensionsIDs.add(rows[i][0]);
			if (rows[i][1].equals("integer"))
				dimensionTypes.add(DimensionType.INTEGER);
			else if (rows[i][1].equals("float"))
				dimensionTypes.add(DimensionType.FLOAT);
			else if (rows[i][1].equals("enumeration"))
				dimensionTypes.add(DimensionType.ENUMERATION);
			else throw new WrongDimensionType(rows[i][1], INPUT_AREA, i+1, 2);
			totalSteps.add(steps);
		}
	}
	
	private void initCurrentSteps() throws InvalidInput {
		if (pointNumber > maxPointNumber() || pointNumber < 0)
			throw new WrongPointNumber(pointNumber);
		int summaryStepsLeft = pointNumber;
		for (int i=0; i<dimensionsIDs.size(); i++) {
			int totalsProduct = 1;
			for (int j=i+1; j<dimensionsIDs.size(); j++)
				totalsProduct *= totalSteps.get(j);
			currentSteps.add(summaryStepsLeft/totalsProduct);
			summaryStepsLeft = summaryStepsLeft%totalsProduct;
		}
	}
	
	public int maxPointNumber() {
		int result = 1;
		for (Integer steps : totalSteps)
			result *= steps;
		return result-1;
	}
	
	private String currentInputArea;
	
	public String getPreparedContent(String content, String inputArea) throws InvalidInput {
		currentInputArea = inputArea;
		String withComputed = replaceComputed(content);
		String withEnumerated = replaceEnumerated(withComputed);
		
		return withEnumerated;
	}
	
	public String replaceComputed(String content) throws InvalidInput {
		Matcher matcher = Pattern.compile(computedValuesRegex).matcher(content);
		StringBuilder prepared = new StringBuilder("");
		while(matcher.find()) {
			String staticPart = matcher.group("juststatic");
			String dinamic = "";
			if (staticPart == null) {
				staticPart = matcher.group("static");
				String dinamicTemplate = matcher.group("dinamic");
				dinamic = compileComputedTemplate(dinamicTemplate);
			}
			prepared.append(staticPart).append(dinamic);
		}
		return prepared.toString();
	}
	
	public String replaceEnumerated(String content) throws InvalidInput {
		Matcher matcher = Pattern.compile(enumeratedValuesRegex).matcher(content);
		StringBuilder prepared = new StringBuilder("");
		while(matcher.find()) {
			String staticPart = matcher.group("juststatic");
			String dinamic = "";
			if (staticPart == null) {
				staticPart = matcher.group("static");
				if (staticPart == null)
					staticPart = "";
				String dimension = matcher.group("dimension");
				if (dimension != null)
					dimension = dimension.replace(" ", "");
				else
					throw new WrongUsageOfDimension("Wrong syntax around dimension value template", currentInputArea);
				int dimensionIndex = dimensionsIDs.indexOf(dimension);
				if (dimensionIndex == -1)
					throw new UnknownDimension(dimension, currentInputArea);
				String value = null;
				for(int i=0; i<totalSteps.get(dimensionIndex); i++) {
					if (!matcher.find() || (value = matcher.group("value")) == null)
						throw new WrongUsageOfDimension("Enumerated values are too low for dimension "+dimension, currentInputArea);
					if (i == currentSteps.get(dimensionIndex))
						dinamic = value;
				}
				onPointValues.put(dimension, dinamic.replace(';', '|').replace("\n", "  //  "));
			}
			prepared.append(staticPart).append(dinamic);
		}
		return prepared.toString();
	}
	
	private String compileComputedTemplate(String template) throws InvalidInput {
		template = template.replace(" ", "");
		Matcher matcher = Pattern.compile(computedValueTemplateRegex).matcher(template);
		String dim=null, valueName=null, first=null, last=null;
		if (!matcher.find())
			throw new WrongUsageOfDimension("Wrong syntax around dimension value template", currentInputArea);
		dim = matcher.group("dimension");
		valueName = matcher.group("valueName");
		first = matcher.group("first");
		last = matcher.group("last");
		String result = translateComputed(dim, first, last);
		if (valueName == null)
			valueName = dim;
		onPointValues.put(valueName, result);
		return result;
	}
	
	private String translateComputed(String dimID, String firstValStr, String lastValStr) throws InvalidInput {
		int dimensionIndex = dimensionsIDs.indexOf(dimID);
		if (dimensionIndex == -1)
			throw new UnknownDimension(dimID, currentInputArea);
		Integer steps = totalSteps.get(dimensionIndex);
		Integer currentStep = currentSteps.get(dimensionIndex);
		if (dimensionTypes.get(dimensionIndex).equals(DimensionType.INTEGER)) {
			Integer first, last;
			try {
				first = Integer.parseInt(firstValStr);
			} catch(NumberFormatException e) {
				throw new NotInteger(firstValStr, currentInputArea);
			}
			try {
				last = Integer.parseInt(lastValStr);
			} catch(NumberFormatException e) {
				throw new NotInteger(firstValStr, currentInputArea);
			}
			return ((Integer)(first + ((last-first)*currentStep)/(steps-1))).toString();
		}
		if (dimensionTypes.get(dimensionIndex).equals(DimensionType.FLOAT)) {
			Double first, last;
			try {
				first = Double.parseDouble(firstValStr);
			} catch(NumberFormatException e) {
				throw new NotDouble(firstValStr, currentInputArea);
			}
			try {
				last = Double.parseDouble(lastValStr);
			} catch(NumberFormatException e) {
				throw new NotDouble(firstValStr, currentInputArea);
			}
			return ((Double)(first + ((last-first)*currentStep)/(steps-1))).toString();
		}
		throw new WrongUsageOfDimension("Enumeration dimension was used as integer of float", currentInputArea);
	}
	
	public Map<String,String> getPrevPointValuesMap() {
		return onPointValues;
	}
}