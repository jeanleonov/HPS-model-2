package point_initialization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	
	private FileReader fileReader;
	private BufferedReader dimensionsConfigurationsReader;
	private List<String> dimensionsIDs;
	private List<DimensionType> dimensionTypes;
	private List<Integer> totalSteps;
	private List<Integer> currentSteps;
	private Map<String,String> onPointValues;
	private int pointNumber;
	
	private enum DimensionType {
		INTEGER,
		FLOAT,
		ENUMERATION
	}

	private static final String enumeratedValuesRegex = "(:?\\{#(?<value>(?s).*?)#\\})|(:?(?<static>(?s).+?)?(:?#\\{(?<dimension>.*?)\\}))|(?<juststatic>(?s).*)";
	private static final String computedValuesRegex = "(?<static>(?s).+?)?(:?#\\[(?<dinamic>.*?)\\])|(?<juststatic>(?s).*)";
	private static final String computedValueTemplateRegex = "(?<dimension>\\w+)(:?\\((?<valueName>\\w+)\\))?:(?<first>[\\d.,]+)-(?<last>[\\d.,]+)";

	public InputsPreparer(String dimensionsToTestPath) throws IOException {
		this.fileReader = new FileReader(dimensionsToTestPath);
		this.dimensionsConfigurationsReader = new BufferedReader(fileReader);
		this.dimensionsIDs = new ArrayList<>();
		this.dimensionTypes = new ArrayList<DimensionType>();
		this.totalSteps = new ArrayList<>();
		this.currentSteps = new ArrayList<>();
		this.onPointValues = new HashMap<>();
		initDimensions();
	}
	
	public void setPoint(int point) throws IOException {
		pointNumber = point;
		onPointValues.clear();
		currentSteps.clear();
		initCurrentSteps();
	}
	
	private void initDimensions() throws IOException {
		String line;
		while ((line = dimensionsConfigurationsReader.readLine()) != null) {
			if (line.isEmpty())
				continue;
			String[] cells = line.replace(" ", "").split(";");
			String dimensionID = cells[0];
			String type = cells[1];
			Integer steps = Integer.parseInt(cells[2]);
			dimensionsIDs.add(dimensionID);
			dimensionTypes.add(getTypeByString(type));
			totalSteps.add(steps);
		}
	}
	
	private DimensionType getTypeByString(String clazz) throws IOException {
		if (clazz.equals("integer"))
			return DimensionType.INTEGER;
		if (clazz.equals("float"))
			return DimensionType.FLOAT;
		if (clazz.equals("enumeration"))
			return DimensionType.ENUMERATION;
		throw new IOException("Wrong content of file with dimensions configurations. \n");
	}
	
	private void initCurrentSteps() throws IOException {
		if (pointNumber > maxPointNumber())
			throw new IOException("Too big point number.");
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
	
	public String getPreparedContent(String content) throws IOException {
		String withComputed = replaceComputed(content);
		return replaceEnumerated(withComputed);
	}
	
	public String replaceComputed(String content) throws IOException {
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
	
	public String replaceEnumerated(String content) throws IOException {
		Matcher matcher = Pattern.compile(enumeratedValuesRegex).matcher(content);
		StringBuilder prepared = new StringBuilder("");
		while(matcher.find()) {
			String staticPart = matcher.group("juststatic");
			String dinamic = "";
			if (staticPart == null) {
				staticPart = matcher.group("static");
				String dimension = matcher.group("dimension").replace(" ", "");
				int dimensionIndex = dimensionsIDs.indexOf(dimension);
				String value = null;
				for(int i=0; i<totalSteps.get(dimensionIndex); i++) {
					if (!matcher.find() || (value = matcher.group("value")) == null)
						throw new IOException("Enumerated values are too low for dimension "+dimension);
					if (i == currentSteps.get(dimensionIndex))
						dinamic = value;
				}
				onPointValues.put(dimension, dinamic.replace(';', '|').replace("\n", "  //  "));
			}
			prepared.append(staticPart).append(dinamic);
		}
		return prepared.toString();
	}
	
	private String compileComputedTemplate(String template) throws IOException {
		template = template.replace(" ", "");
		Matcher matcher = Pattern.compile(computedValueTemplateRegex).matcher(template);
		String dim=null, valueName=null, first=null, last=null;
		matcher.find();
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
	
	private String translateComputed(String dimID, String firstValStr, String lastValStr) throws IOException {
		int dimensionIndex = dimensionsIDs.indexOf(dimID);
		Integer steps = totalSteps.get(dimensionIndex);
		Integer currentStep = currentSteps.get(dimensionIndex);
		if (dimensionTypes.get(dimensionIndex).equals(DimensionType.INTEGER)) {
			Integer first = Integer.parseInt(firstValStr);
			Integer last = Integer.parseInt(lastValStr);
			return ((Integer)(first + ((last-first)*currentStep)/(steps-1))).toString();
		}
		if (dimensionTypes.get(dimensionIndex).equals(DimensionType.FLOAT)) {
			Double firstInt = Double.parseDouble(firstValStr);
			Double lastInt = Double.parseDouble(lastValStr);
			return ((Double)(firstInt + ((lastInt-firstInt)*currentStep)/(steps-1))).toString();
		}
		throw new IOException("Changeable token translation was failed.");
	}
	
	public Map<String,String> getPrevPointValuesMap() {
		return onPointValues;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (this.dimensionsConfigurationsReader != null)
			this.dimensionsConfigurationsReader.close();
		if (this.fileReader != null)
			this.fileReader.close();
		super.finalize();
	}
}