package hps.program_starter;

import hps.exceptions.Exceptions.FileDoesntExist;
import hps.exceptions.Exceptions.FolderDoesntExist;
import hps.exceptions.Exceptions.InvalidInput;
import hps.point.Point;
import hps.point_initialization.PointInitialiser;
import hps.point_movement.PointMover;
import hps.statistic_saving.DetailedStatisticSaver;
import hps.statistic_saving.InitialPointSaver;
import hps.statistic_saving.ShortStatisticSaver;
import hps.tools.AsyncOutputStream;
import hps.tools.CMDArgument;
import hps.tools.CMDLineParser;
import hps.tools.Logger;
import hps.tools.Range;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

public class HPS {

	public static void main(String[] args) {
		try {
			parseArgs(args);
			HPS.get().start();
		} catch(InvalidInput | FolderDoesntExist | FileDoesntExist e) {
			e.printStackTrace();
			Logger.warning(e);
		} catch(Throwable e) {
			e.printStackTrace();
			Logger.error(e);
		}
	}
	
	private static HPS instance = null;
	public static HPS get() throws IOException, InterruptedException {
		if (instance == null)
			instance = new HPS();
		return instance;
	}
	

	
	private InitialPointSaver initialPointSaver;
	private PointInitialiser pointInitialiser;
	private int currentPointNumber;
	private String currentPointName;
	private int currentExperimentNumber;
	private String currentExperimentName;
	private File outputsFolder;
	
	private HPS() throws IOException, InterruptedException {
		instance = this;
		String modelingName = (String)CMDArgument.EXPERIMENTS_SERIES_NAME.getValue();
		outputsFolder = new File((String)CMDArgument.OUTPUTS_FOLDER.getValue() + "/" + modelingName);
		int i=1;
		while (outputsFolder.exists())
			outputsFolder = new File((String)CMDArgument.OUTPUTS_FOLDER.getValue() + "/" + modelingName + "-" + i++);
		outputsFolder.mkdirs();
		initialPointSaver = new InitialPointSaver();
		pointInitialiser = new PointInitialiser(initialPointSaver);
	}
	
	private void start() throws Throwable {
		DetailedStatisticSaver detailedStatisticSaver = new DetailedStatisticSaver();
		ShortStatisticSaver shortStatisticSaver = new ShortStatisticSaver();
		for (Integer point : (Range)CMDArgument.POINTS.getValue()) {
			currentPointNumber = point;
			currentPointName = pointNumberToString(point);
			Logger.openPoint();
			Point initialPoint = pointInitialiser.getPoint(point);
			for (Integer experiment : (Range)CMDArgument.EXPERIMENTS.getValue()) {
				currentExperimentNumber = experiment;
				currentExperimentName = experimentNumberToString(experiment);
				Logger.openExperiment();
				PointMover pointMover = new PointMover(initialPoint, experiment);
				pointMover.registerSubscriber(detailedStatisticSaver);
				pointMover.registerSubscriber(shortStatisticSaver);
				pointMover.move();
				Logger.closeExperiment();
			}
			Logger.closePoint();
		}
		detailedStatisticSaver.finish();
		shortStatisticSaver.finish();
		AsyncOutputStream.finish();
        try {
        	String python = (String)CMDArgument.PYTHON_PATH.getValue();
        	String statisticFolder = outputsFolder.getPath() + File.separator + "statistic";
        	String command = python + " agglutinate.py \""+ statisticFolder +"\"";
            Process agglutinatingProcess = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(agglutinatingProcess.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(agglutinatingProcess.getErrorStream()));
            String programOutput;
            while ((programOutput = stdInput.readLine()) != null) 
                Logger.info(programOutput);
            while ((programOutput = stdError.readLine()) != null) 
                Logger.warning(programOutput);
        }
        catch (IOException e) {
        	Logger.error(e);
        }
	}
	
	private Integer signsInPointNumber = null;
	private String pointNumberToString(int pointNumber) {
		if (signsInPointNumber == null)
			signsInPointNumber = calculateSignsInNumber(pointInitialiser.maxPointNumber());
		return String.format("%0"+signsInPointNumber+"d", pointNumber);
	}
	
	private Integer signsInExperimentNumber = null;
	private String experimentNumberToString(int pointNumber) {
		if (signsInExperimentNumber == null)
			signsInExperimentNumber = calculateSignsInNumber((Integer)CMDArgument.MAX_EXPERIMENT_NUMBER.getValue());
		return String.format("%0"+signsInExperimentNumber+"d", pointNumber);
	}

	private int calculateSignsInNumber(int number) {
		int signs;
		for (signs=0; number>0; signs++)
			number /= 10;
		return signs==0? 1 : signs;
	}
	
	public InitialPointSaver getInitialPointSaver() {
		return initialPointSaver;
	}

	public PointInitialiser getPointInitialiser() {
		return pointInitialiser;
	}

	public int getCurrentPointNumber() {
		return currentPointNumber;
	}

	public String getCurrentPointName() {
		return currentPointName;
	}

	public int getCurrentExperimentNumber() {
		return currentExperimentNumber;
	}

	public String getCurrentExperimentName() {
		return currentExperimentName;
	}

	public File getOutputsFolder() {
		return outputsFolder;
	}

	public LinkedHashMap<String, String> getCurrentPointDynamicValues() {
		return pointInitialiser.getCurrentPointDynamicValues();
	}

	private static void parseArgs(String[] args) {
		try {
	        CMDArgument.parse(args);
	    }
	    catch(CMDLineParser.OptionException e) {
	    	Logger.error(e);
	        System.out.println(CMDArgument.HELP_TEXT);
	        System.exit(2);
	    }
		if((Boolean) CMDArgument.HELP.getValue()) {
	        System.out.println(CMDArgument.HELP_TEXT);
	        System.exit(0);
		}
	}

}
