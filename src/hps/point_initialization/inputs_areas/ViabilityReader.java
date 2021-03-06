package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.Negative;
import hps.exceptions.Exceptions.NotAllGenotypes;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.NotInRange;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.WrongFileStructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hps.point.components.GenotypeHelper;
import hps.point.components.Viability.GenotypeViability;

public class ViabilityReader {

	private hps.point.components.Viability viability;
	public final static String INPUT_AREA = "Viability";
	private static Set<String> knownGenotypes = new HashSet<>();
	
	public ViabilityReader(String input) throws InvalidInput {
		viability = new hps.point.components.Viability();
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
		for (int i=1; i<header.length; i++) {
			GenotypeViability genotypeViability;
			if (GenotypeHelper.isFemale(header[i]))
				genotypeViability = parseFemaleGenotypeViability(rows, i);
			else
				genotypeViability = parseMaleGenotypeViability(rows, i);
			viability.addGenotypeViability(header[i], genotypeViability);
		}
	}
	
	private void checkAndTrimHeader(String[] header) throws InvalidInput {
		if (knownGenotypes.isEmpty())
			for (int i=1; i<header.length; i++) {
				if (!GenotypeHelper.isLookedAsGenotype(header[i]))
					throw new NotGenotype(header[i], INPUT_AREA, 1, i+1);
				knownGenotypes.add(header[i]);
			}
		else {
			Set<String> uniqueGenotypes = new HashSet<>();
			for (int i=1; i<header.length; i++) {
				if (!GenotypeHelper.isGenotype(header[i]))
					throw new NotGenotype(header[i], INPUT_AREA, 1, i+1);
				uniqueGenotypes.add(header[i]);
			}
			if (uniqueGenotypes.size() != knownGenotypes.size())
				throw new NotAllGenotypes();
		}
	}
	
	private GenotypeViability parseMaleGenotypeViability(String[][] rows, int columnNumber) throws InvalidInput {
		GenotypeViability genotypeViability = new GenotypeViability();
		
		genotypeViability.lifetime = parseInt(rows[ 1][columnNumber],  2, columnNumber);
		
		genotypeViability.spawning = parseInt(rows[ 2][columnNumber],  3, columnNumber);
		
		genotypeViability.							survival = parseDouble(rows[ 3][columnNumber],  4, columnNumber, 0.0, 1.0);
		genotypeViability.				  survivalAchieveAge = parseInt   (rows[ 4][columnNumber],  5, columnNumber);
		genotypeViability.				 survivalCoefficient = parseDouble(rows[ 5][columnNumber],  6, columnNumber, 0.0, 1.0);
		genotypeViability.					   survivalFirst = parseDouble(rows[ 6][columnNumber],  7, columnNumber, 0.0, 1.0);
		genotypeViability.survivalCoefficientBeforeAchiveAge = parseDouble(rows[ 7][columnNumber],  8, columnNumber, 0.0, 1.0);
		
		genotypeViability.							competitiveness = parseDouble(rows[ 8][columnNumber],  9, columnNumber, 0.0, 1.0);
		genotypeViability.				  competitivenessAchieveAge =    parseInt(rows[ 9][columnNumber], 10, columnNumber);
		genotypeViability.				 competitivenessCoefficient = parseDouble(rows[10][columnNumber], 11, columnNumber, 0.0, 1.0);
		genotypeViability.					   competitivenessFirst = parseDouble(rows[11][columnNumber], 12, columnNumber, 0.0, 1.0);
		genotypeViability.competitivenessCoefficientBeforeAchiveAge = parseDouble(rows[12][columnNumber], 13, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 reproduction = parseDouble(rows[13][columnNumber], 14, columnNumber, 0.0, 1.0);
		genotypeViability.reproductionCoefficient = parseDouble(rows[14][columnNumber], 15, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 fertility = parseDouble(rows[15][columnNumber], 16, columnNumber, 0.0, 1.0);
		genotypeViability.fertilityCoefficient = parseDouble(rows[16][columnNumber], 17, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 amplexusRepeat = parseDouble(rows[17][columnNumber], 18, columnNumber, 0.0, 1.0);
		genotypeViability.amplexusRepeatCoefficient = parseDouble(rows[18][columnNumber], 19, columnNumber, 0.0, 1.0);
		
		genotypeViability.voracity01 = parseDouble(rows[19][columnNumber], 20, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity02 = parseDouble(rows[20][columnNumber], 21, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity03 = parseDouble(rows[21][columnNumber], 22, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity04 = parseDouble(rows[22][columnNumber], 23, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity05 = parseDouble(rows[23][columnNumber], 24, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity06 = parseDouble(rows[24][columnNumber], 25, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity07 = parseDouble(rows[25][columnNumber], 26, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity08 = parseDouble(rows[26][columnNumber], 27, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity09 = parseDouble(rows[27][columnNumber], 28, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity10 = parseDouble(rows[28][columnNumber], 29, columnNumber, 0.0, Double.MAX_VALUE);
		
		return genotypeViability;
	}
	
	private GenotypeViability parseFemaleGenotypeViability(String[][] rows, int columnNumber) throws InvalidInput {
		GenotypeViability genotypeViability = new GenotypeViability();
		
		genotypeViability.lifetime = parseInt(rows[ 1][columnNumber],  2, columnNumber);
		
		genotypeViability.spawning = parseInt(rows[ 2][columnNumber],  3, columnNumber);
		
		genotypeViability.							survival = parseDouble(rows[ 3][columnNumber],  4, columnNumber, 0.0, 1.0);
		genotypeViability.				  survivalAchieveAge = parseInt   (rows[ 4][columnNumber],  5, columnNumber);
		genotypeViability.				 survivalCoefficient = parseDouble(rows[ 5][columnNumber],  6, columnNumber, 0.0, 1.0);
		genotypeViability.					   survivalFirst = parseDouble(rows[ 6][columnNumber],  7, columnNumber, 0.0, 1.0);
		genotypeViability.survivalCoefficientBeforeAchiveAge = parseDouble(rows[ 7][columnNumber],  8, columnNumber, 0.0, 1.0);
		
		genotypeViability.							competitiveness = parseDouble(rows[ 8][columnNumber],  9, columnNumber, 0.0, 1.0);
		genotypeViability.				  competitivenessAchieveAge =    parseInt(rows[ 9][columnNumber], 10, columnNumber);
		genotypeViability.				 competitivenessCoefficient = parseDouble(rows[10][columnNumber], 11, columnNumber, 0.0, 1.0);
		genotypeViability.					   competitivenessFirst = parseDouble(rows[11][columnNumber], 12, columnNumber, 0.0, 1.0);
		genotypeViability.competitivenessCoefficientBeforeAchiveAge = parseDouble(rows[12][columnNumber], 13, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 reproduction = parseDouble(rows[13][columnNumber], 14, columnNumber, 0.0, 1.0);
		genotypeViability.reproductionCoefficient = parseDouble(rows[14][columnNumber], 15, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 fertility = parseDouble(rows[15][columnNumber], 16, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.fertilityCoefficient = parseDouble(rows[16][columnNumber], 17, columnNumber, 0.0, 1.0);
		
		genotypeViability.			 amplexusRepeat = parseDouble(rows[17][columnNumber], 18, columnNumber, 0.0, 1.0);
		genotypeViability.amplexusRepeatCoefficient = parseDouble(rows[18][columnNumber], 19, columnNumber, 0.0, 1.0);
		
		genotypeViability.voracity01 = parseDouble(rows[19][columnNumber], 20, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity02 = parseDouble(rows[20][columnNumber], 21, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity03 = parseDouble(rows[21][columnNumber], 22, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity04 = parseDouble(rows[22][columnNumber], 23, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity05 = parseDouble(rows[23][columnNumber], 24, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity06 = parseDouble(rows[24][columnNumber], 25, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity07 = parseDouble(rows[25][columnNumber], 26, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity08 = parseDouble(rows[26][columnNumber], 27, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity09 = parseDouble(rows[27][columnNumber], 28, columnNumber, 0.0, Double.MAX_VALUE);
		genotypeViability.voracity10 = parseDouble(rows[28][columnNumber], 29, columnNumber, 0.0, Double.MAX_VALUE);
		
		return genotypeViability;
	}
	
	private int parseInt(String cell, int rowNumber, int columnNumber) throws InvalidInput {
		int value;
		try {
			value = Integer.parseInt(cell);
		} catch (NumberFormatException e) {
			throw new NotInteger(cell, INPUT_AREA, rowNumber, columnNumber);
		}
		if (value < 0)
			throw new Negative(value,INPUT_AREA,rowNumber,columnNumber);
		return value;
	}
	
	private double parseDouble(String cell, int rowNumber, int columnNumber,
			                   double min, double max) throws InvalidInput {
		double value;
		try {
			value = Double.parseDouble(cell);
		} catch (NumberFormatException e) {
			throw new NotDouble(cell, INPUT_AREA, rowNumber, columnNumber);
		}
		if (value < min || value > max)
			throw new NotInRange(min, max, value, INPUT_AREA, rowNumber, columnNumber);
		return value;
	}

	public hps.point.components.Viability getViability() {
		return viability;
	}
	
	
	public static boolean isKnown(String genotype) {
		return knownGenotypes.contains(genotype);
	}
	
	public static List<String> getKnownGenotypes() {
		List<String> knownList = new ArrayList<>(knownGenotypes.size());
		knownGenotypes.forEach(known -> knownList.add(known));
		return knownList;
	}
	
	/**
	 *  JUST FOR UNIT TESTS!! 
	 */
	public static Set<String> getKnownGenotypesSet() {
		return knownGenotypes;
	}
}
