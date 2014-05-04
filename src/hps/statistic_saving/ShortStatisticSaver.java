package hps.statistic_saving;

import hps.point.Point;
import hps.point.components.Habitat;
import hps.point.components.IndividualsGroup;
import hps.point.components.IndividualsGroupState;
import hps.point_movement.PointMover.IterationSubStep;
import hps.program_starter.HPS;
import hps.tools.AsyncOutputStream;
import hps.tools.CMDArgument;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class ShortStatisticSaver implements StatisticSubcriber {
	
	private LinkedHashMap<String, List<Column>> columns=null;
	private AsyncOutputStream currentPointWriter=null;
	private String currentPoint;
	private LinkedHashMap<String, String> pointValues;
	private String[] row;
	private StatisticSettings settings;
	
	public ShortStatisticSaver() throws IOException, InterruptedException {
		settings = StatisticSettings.get();
		pointValues = HPS.get().getCurrentPointDynamicValues();
	}

	@Override
	public void saveSystemState(Point point, int year, IterationSubStep justFinishedSubStep) throws IOException, InterruptedException {
		if (year != (Integer)CMDArgument.YEARS.getValue())
			return;
		if (settings.shortStatisticAfter != justFinishedSubStep)
			return;
		if (columns == null)
			initiateColumns(point);
		if (currentPointWriter == null || 
			!HPS.get().getCurrentPointName().equals(currentPoint))
		{
			currentPoint = HPS.get().getCurrentPointName();
			if (currentPointWriter != null)
				currentPointWriter.close();
			File statisticFolder = new File(settings.statisticFolder.getPath());
			if (!statisticFolder.exists())
				statisticFolder.mkdirs();
			File pointStatisticFile = new File(statisticFolder.getPath() + "/" + HPS.get().getCurrentExperimentName() + ".csv");
			pointStatisticFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(pointStatisticFile);
			currentPointWriter = new AsyncOutputStream(new BufferedOutputStream(fout));
			writeHeader();
		}
		writeRow(point, year, justFinishedSubStep);
	}
	
	private void initiateColumns(Point point) {
		columns = new LinkedHashMap<>();
		LinkedList<Column> pointValuesColumns = new LinkedList<>();
		for (String pointValueName : pointValues.keySet())
			pointValuesColumns.add(new Column(pointValueName, null));
		columns.put("", pointValuesColumns);
		for (Habitat habitat : point.getHabitats()) {
			LinkedList<Column> habitatColumns = new LinkedList<>();
			if (settings.onlyGenotypes)
				for (String genotype : habitat.getViability().getGenotypes())
					habitatColumns.add(new Column(genotype));
			else {
				for (String genotype : habitat.getViability().getGenotypes()) {
					if (settings.onlyMatures)
						for (int age=habitat.getViability().getGenotypeSpawing(genotype); age<=habitat.getViability().getGenotypeLifetime(genotype); age++)
							habitatColumns.add(new Column(new IndividualsGroup(genotype, age)));
					else
						for (int age=0; age<=habitat.getViability().getGenotypeLifetime(genotype); age++)
							habitatColumns.add(new Column(new IndividualsGroup(genotype, age)));
					habitatColumns.add(new Column(genotype));
				}
			}
			columns.put(habitat.getHabitatName(), habitatColumns);
		}
		row = new String[getNumberOfColumns()];
	}
	
	private int getNumberOfColumns() {
		int result = 3;
		for (Entry<String, List<Column>> entry : columns.entrySet())
			result += entry.getValue().size();
		return result;
	}
	
	private void writeHeader() throws IOException {
		row[0] = row[1] = row[2] = "";
		int columnNumber = 3;
		for (int i=0; i<columns.get("").size(); i++)
			row[columnNumber++] = "";
		for (Entry<String, List<Column>> entry : columns.entrySet())
			if (!entry.getKey().equals(""))
				for (int i=0; i<entry.getValue().size(); i++)
					row[columnNumber++] = entry.getKey();
		currentPointWriter.write((String.join(";", row) + "\n").getBytes());
		row[0] = "Point#";
		row[1] = "Year#";
		row[2] = "After subiteration";
		columnNumber = 3;
		for (Column column : columns.get(""))
			row[columnNumber++] = column.dynamicValueName;
		for (Entry<String, List<Column>> entry : columns.entrySet())
			if (!entry.getKey().equals(""))
				for (Column column : entry.getValue())
					row[columnNumber++] = column.toString();
		currentPointWriter.write((String.join(";", row) + "\n").getBytes());
	}
	
	private void writeRow(Point point, Integer year, IterationSubStep justFinishedSubStep) throws IOException, InterruptedException {
		row[0] = Integer.toString(HPS.get().getCurrentPointNumber());
		row[1] = Integer.toString(year);
		row[2] = justFinishedSubStep.toString();
		int columnNumber = 3;
		for (Column column : columns.get(""))
			row[columnNumber++] = pointValues.get(column.dynamicValueName);
		if (settings.onlyGenotypes)
			for (Entry<String, List<Column>> entry : columns.entrySet()) {
				if (!entry.getKey().equals("")) {
					Habitat habitat = point.getNamedHabitats().get(entry.getKey());
					for (Column column : entry.getValue())
						row[columnNumber++] = Integer.toString(getGenotypeStrength(habitat, column.genotype));
				}
			}
		else
			for (Entry<String, List<Column>> entry : columns.entrySet()) {
				if (!entry.getKey().equals("")) {
					int sumOfGenotype = 0;
					Habitat habitat = point.getNamedHabitats().get(entry.getKey());
					for (Column column : entry.getValue()) {
						if (column.genotype == null) {
							IndividualsGroupState groupState = habitat.getGroupsStates().get(column.genotypeWithAge);
							int strength = groupState != null? groupState.strength : 0;
							row[columnNumber++] = Integer.toString(strength);
							sumOfGenotype += strength;
						}
						else {
							row[columnNumber++] = Integer.toString(sumOfGenotype);
							sumOfGenotype = 0;
						}
					}
				}
			}
		currentPointWriter.write((String.join(";", row) + "\n").getBytes());
	}
	
	private int getGenotypeStrength(Habitat habitat, String genotype) {
		int genotypeStrength = 0;
		int lifeTime = habitat.getViability().getGenotypeLifetime(genotype);
		int spawingTime = habitat.getViability().getGenotypeSpawing(genotype);
		if (settings.onlyMatures) {
			for (int age=lifeTime; age>=spawingTime; age--) {
				IndividualsGroupState groupState = habitat.getGroupsStates().get(new IndividualsGroup(genotype, age));
				genotypeStrength += groupState != null? groupState.strength : 0;
			}
		}
		else {
			for (int age=lifeTime; age>=0; age--) {
				IndividualsGroupState groupState = habitat.getGroupsStates().get(new IndividualsGroup(genotype, age));
				genotypeStrength += groupState != null? groupState.strength : 0;
			}
		}
		return genotypeStrength;
	}
	
	public void finish() throws Throwable {
		currentPointWriter.close();
	}
	
	
	private static class Column {
		String dynamicValueName;
		String genotype;
		IndividualsGroup genotypeWithAge;
		public Column(String genotype) {
			this.genotype = genotype;
			this.genotypeWithAge = null;
			this.dynamicValueName = null;
		}
		public Column(IndividualsGroup genotypeWithAge) {
			this.genotype = null;
			this.genotypeWithAge = genotypeWithAge;
			this.dynamicValueName = null;
		}
		public Column(String dynamicValueName, String genotype) {
			this.genotype = null;
			this.genotypeWithAge = null;
			this.dynamicValueName = dynamicValueName;
		}
		@Override
		public String toString() {
			if (genotypeWithAge != null)
				return genotypeWithAge.toString();
			else if (genotype != null)
				return genotype;
			return dynamicValueName;
		}
	}
	
}
