package hps.point_initialization;

import hps.exceptions.Exceptions.FileDoesntExist;
import hps.exceptions.Exceptions.FolderDoesntExist;
import hps.point.Point;
import hps.point.components.Habitat;
import hps.point.components.IndividualsGroup;
import hps.point.components.IndividualsGroupState;
import hps.point.components.Posterity;
import hps.point.components.Scenario;
import hps.point.components.Viability;
import hps.point_initialization.inputs_areas.InitialCompositionReader;
import hps.point_initialization.inputs_areas.NeighborsReader;
import hps.point_initialization.inputs_areas.PosterityReader;
import hps.point_initialization.inputs_areas.ResourcesReader;
import hps.point_initialization.inputs_areas.ScenarioReader;
import hps.point_initialization.inputs_areas.ViabilityReader;
import hps.statistic_saving.InitialPointSaver;
import hps.tools.CMDArgument;
import hps.tools.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class PointInitialiser {
	
	private final static String
		DIMENSIONS = "Dimensions.csv",
		INITIAL_COMPOSITION = "Initial-composition.csv",
		NEIGHBORS = "Neighbors.csv",
		POSTERITY = "Posterity.csv",
		RESOURCES = "Resources.csv",
		SCENARIO = "Scenario.csv",
		VIABILITY = "Viability.csv";
	
	private Set<String> existingHabitats;
	private InputsPreparer preparer;
	private File inputsFolder;
	private InitialPointSaver saver;

	public PointInitialiser(InitialPointSaver saver) throws IOException {
		inputsFolder = new File((String)CMDArgument.INPUTS_FOLDER.getValue());
		if (!inputsFolder.exists() || !inputsFolder.isDirectory())
			throw new FolderDoesntExist(inputsFolder.getPath(), "but folder with inputs is strongly required");
		this.saver = saver;
		initiatePreparer();
		existingHabitats = new HashSet<>();
		for (File file : inputsFolder.listFiles())
			if (file.isDirectory())
				existingHabitats.add(file.getName());
	}
	
	public Point getPoint(int pointNumber) throws IOException {
		preparer.setPoint(pointNumber);
		List<Habitat> habitats = new LinkedList<>();
		for (File file : inputsFolder.listFiles())
			if (file.isDirectory())
				habitats.add(initiateHabitat(file));
		return new Point(habitats);
	}
	
	public int maxPointNumber() {
		return preparer.maxPointNumber();
	}
	
	private void initiatePreparer() throws IOException {
		File dimensionsCSV = new File(inputsFolder.getPath() + File.separatorChar + DIMENSIONS);
		if (dimensionsCSV.exists() && dimensionsCSV.isFile()) {
			String fileContent = getFullFileContent(dimensionsCSV);
			preparer = new InputsPreparer(fileContent);
			saver.save(DIMENSIONS, fileContent);
		}
		else {
			preparer = new InputsPreparer("");
			saver.save(DIMENSIONS, "");
			Logger.info(String.format("\"%s\" file is missed. Single point will be modelled", DIMENSIONS));
		}
	}
	
	private Habitat initiateHabitat(File habitatFolder) throws IOException{
		Viability viability = initiateViability(habitatFolder);
		Posterity posterity = initiatePosterity(habitatFolder);
		Scenario scenario = initiateScenario(habitatFolder);
		Double resources = initiateResources(habitatFolder);
		LinkedHashMap<String, Double> neighbors = initiateNeighbors(habitatFolder);
		LinkedHashMap<IndividualsGroup, Integer> rawInitialComposition = initiateInitialComposition(habitatFolder);
		LinkedHashMap<IndividualsGroup, IndividualsGroupState> initialComposition = new LinkedHashMap<>();
		for (Entry<IndividualsGroup, Integer> entry : rawInitialComposition.entrySet()) {
			IndividualsGroup group = entry.getKey();
			IndividualsGroupState groupState = new IndividualsGroupState(group, entry.getValue(), viability, posterity);
			initialComposition.put(group, groupState);
		}
		return new Habitat(initialComposition, viability, posterity, scenario, neighbors, resources, habitatFolder.getName());
	}
	
	private Viability initiateViability(File habitatFolder) throws IOException {
		File viabilityCSV = new File(habitatFolder.getPath() + File.separatorChar + VIABILITY);
		if (!viabilityCSV.exists() || !viabilityCSV.isFile())
			throw new FileDoesntExist(habitatFolder.getPath() + File.separatorChar + VIABILITY,
									  "but file with parameters of viability is strongly required");
		String fileContent = getFullFileContent(viabilityCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, ViabilityReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), VIABILITY, preparedContent);
		return new ViabilityReader(preparedContent).getViability();
	}
	
	private Posterity initiatePosterity(File habitatFolder) throws IOException {
		File posterityCSV = new File(habitatFolder.getPath() + File.separatorChar + POSTERITY);
		if (!posterityCSV.exists() || !posterityCSV.isFile())
			throw new FileDoesntExist(habitatFolder.getPath() + File.separatorChar + POSTERITY,
									  "but file with parameters of reproduction is strongly required");
		String fileContent = getFullFileContent(posterityCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, PosterityReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), POSTERITY, preparedContent);
		return new PosterityReader(preparedContent).getPosterity();
	}
	
	private Scenario initiateScenario(File habitatFolder) throws IOException {
		File scenarioCSV = new File(habitatFolder.getPath() + File.separatorChar + SCENARIO);
		String fileContent = "";
		if (!scenarioCSV.exists() || !scenarioCSV.isFile())
			Logger.info("File with scenario in \"%s\" is missed. No external events will happen here");
		else
			fileContent = getFullFileContent(scenarioCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, ScenarioReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), SCENARIO, preparedContent);
		return new ScenarioReader(preparedContent).getScenario();
	}

	private Double initiateResources(File habitatFolder) throws IOException {
		File resourcesCSV = new File(habitatFolder.getPath() + File.separatorChar + RESOURCES);
		if (!resourcesCSV.exists() || !resourcesCSV.isFile())
			throw new FileDoesntExist(habitatFolder.getPath() + File.separatorChar + RESOURCES,
									  "but file with initial resources of habitat is strongly required");
		String fileContent = getFullFileContent(resourcesCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, ResourcesReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), RESOURCES, preparedContent);
		return new ResourcesReader(preparedContent).getResources();
	}
	
	private LinkedHashMap<String, Double> initiateNeighbors(File habitatFolder) throws IOException {
		File neighborsCSV = new File(habitatFolder.getPath() + File.separatorChar + NEIGHBORS);
		String fileContent = "";
		if (!neighborsCSV.exists() || !neighborsCSV.isFile())
			Logger.info("File with neighbors of \"%s\" is missed. It will be impossible to leave this habitat");
		else
			fileContent = getFullFileContent(neighborsCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, NeighborsReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), NEIGHBORS, preparedContent);
		return new NeighborsReader(preparedContent, existingHabitats).getMigrationProbabilities();
		
	}
	
	private LinkedHashMap<IndividualsGroup, Integer> 
						initiateInitialComposition(File habitatFolder) throws IOException {
		File initialCompositionCSV = new File(habitatFolder.getPath() + File.separatorChar + INITIAL_COMPOSITION);
		String fileContent = "";
		if (!initialCompositionCSV.exists() || !initialCompositionCSV.isFile())
			Logger.info("File with initial composition of \"%s\" is missed. Habitat will be empty in the first year");
		else
			fileContent = getFullFileContent(initialCompositionCSV);
		String preparedContent = preparer.getPreparedContent(fileContent, InitialCompositionReader.INPUT_AREA);
		saver.saveForCurrentPoint(habitatFolder.getName(), INITIAL_COMPOSITION, preparedContent);
		return new InitialCompositionReader(preparedContent).getInitialComposition();
	}
	
	public LinkedHashMap<String, String> getCurrentPointDynamicValues() {
		return preparer.getCurrentPointValuesMap();
	}
	
	
	private String getFullFileContent(File file) throws IOException {
		BufferedReader inputReader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			inputReader = new BufferedReader(fileReader);
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = inputReader.readLine()) != null)
				builder.append(line).append('\n');
			return builder.toString();
		} finally {
			if (inputReader != null)
				inputReader.close();
			if (fileReader != null)
				fileReader.close();
		}
	}
}
