package hps.program_starter;

import hps.exceptions.Exceptions.InvalidInput;
import hps.point.Point;
import hps.point_initialization.PointInitialiser;
import hps.point_movement.PointMover;
import hps.statistic_saving.InitialPointSaver;
import hps.tools.CMDArgument;
import hps.tools.CMDLineParser;
import hps.tools.Logger;
import hps.tools.Range;

import java.io.IOException;

public class HPS {

	public static void main(String[] args) {
		try {
			parseArgs(args);
			HPS.get().start();
		} catch(InvalidInput e) {
			Logger.warning(e);
		} catch(Throwable e) {
			Logger.error(e);
		}
	}
	
	private static HPS instance = null;
	private static HPS get() throws IOException {
		if (instance == null)
			return instance = new HPS();
		return instance;
	}
	

	
	private InitialPointSaver initialPointSaver;
	private PointInitialiser pointInitialiser;
	
	private HPS() throws IOException {
		initialPointSaver = new InitialPointSaver();
		pointInitialiser = new PointInitialiser(initialPointSaver);
	}
	
	private void start() throws IOException {
		for (Integer point : (Range)CMDArgument.POINTS.getValue()) {
			initialPointSaver.openPoint(pointNumberToString(point));
			Point initialPoint = pointInitialiser.getPoint(point);
			for (Integer experiment : (Range)CMDArgument.EXPERIMENTS.getValue()) {
				PointMover pointMover = new PointMover(initialPoint);
				pointMover.move();
			}
			initialPointSaver.closePoint();
		}
	}
	
	private Integer signsInPointNumber = null;
	private String pointNumberToString(int pointNumber) {
		if (signsInPointNumber == null)
			calculateSignsInPointNumber();
		return String.format("%0"+signsInPointNumber+"d", pointNumber);
	}

	
	private void calculateSignsInPointNumber() {
		int number = pointInitialiser.maxPointNumber();
		int signs;
		for (signs=0; number>0; signs++)
			number /= 10;
		signsInPointNumber = signs==0? 1 : signs;
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
