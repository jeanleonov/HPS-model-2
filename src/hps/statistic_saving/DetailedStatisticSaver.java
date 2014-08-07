package hps.statistic_saving;

import hps.point.Point;
import hps.point.components.Habitat;
import hps.point.components.IndividualsGroup;
import hps.point.components.IndividualsGroupState;
import hps.point_movement.PointMover.IterationSubStep;
import hps.program_starter.HPS;
import hps.tools.AsyncOutputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public class DetailedStatisticSaver implements StatisticSubcriber {
	
	private LinkedHashMap<String, List<Column>> columns=null;
	private AsyncOutputStream currentExperimentWriter=null;
	private String currentPoint;
	private String currentExperiment;
	private String[] row;
	private StatisticSettings settings;
	
	public DetailedStatisticSaver() throws IOException, InterruptedException {
		settings = StatisticSettings.get();
	}

	@Override
	public void saveSystemState(Point point, int experiment, int year, IterationSubStep justFinishedSubStep) throws IOException, InterruptedException {
		if (settings.onlyShort || !settings.subStepsToSave.contains(justFinishedSubStep))
			return;
		if (columns == null)
			initiateColumns(point);
		if (currentExperimentWriter == null || 
			!HPS.get().getCurrentPointName().equals(currentPoint) ||
			!HPS.get().getCurrentExperimentName().equals(currentExperiment))
		{
			currentPoint = HPS.get().getCurrentPointName();
			currentExperiment = HPS.get().getCurrentExperimentName();
			if (currentExperimentWriter != null)
				currentExperimentWriter.close();
			File pointFolder = new File(settings.statisticFolder.getPath() + "/" + HPS.get().getCurrentPointName());
			if (!pointFolder.exists())
				pointFolder.mkdirs();
			File experimentStatisticFile = new File(pointFolder.getPath() + "/" + HPS.get().getCurrentExperimentName() + ".csv");
			experimentStatisticFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(experimentStatisticFile);
			currentExperimentWriter = new AsyncOutputStream(new BufferedOutputStream(fout));
			writeHeader();
		}
		writeRow(point, year, justFinishedSubStep);
	}
	
	private void initiateColumns(Point point) {
		columns = new LinkedHashMap<>();
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
		int result = 2;
		for (Entry<String, List<Column>> entry : columns.entrySet())
			result += entry.getValue().size();
		return result;
	}
	
	private void writeHeader() throws IOException {
		row[0] = row[1] = "";
		int columnNumber = 2;
		for (Entry<String, List<Column>> entry : columns.entrySet())
			for (int i=0; i<entry.getValue().size(); i++)
				row[columnNumber++] = entry.getKey();
		currentExperimentWriter.write((String.join(";", row) + "\n").getBytes());
		row[0] = "Year#";
		row[1] = "After subiteration";
		columnNumber = 2;
		for (Entry<String, List<Column>> entry : columns.entrySet())
			for (Column column : entry.getValue())
				row[columnNumber++] = column.toString();
		currentExperimentWriter.write((String.join(";", row) + "\n").getBytes());
	}
	
	private void writeRow(Point point, Integer year, IterationSubStep justFinishedSubStep) throws IOException {
		row[0] = year.toString();
		row[1] = justFinishedSubStep.toString();
		int columnNumber = 2;
		if (settings.onlyGenotypes)
			for (Entry<String, List<Column>> entry : columns.entrySet()) {
				Habitat habitat = point.getNamedHabitats().get(entry.getKey());
				for (Column column : entry.getValue())
					row[columnNumber++] = Integer.toString(getGenotypeStrength(habitat, column.genotype));
			}
		else
			for (Entry<String, List<Column>> entry : columns.entrySet()) {
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
		currentExperimentWriter.write((String.join(";", row) + "\n").getBytes());
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
		if (currentExperimentWriter != null)
			currentExperimentWriter.close();
	}
	
	
	private static class Column {
		String genotype;
		IndividualsGroup genotypeWithAge;
		public Column(String genotype) {
			this.genotype = genotype;
			this.genotypeWithAge = null;
		}
		public Column(IndividualsGroup genotypeWithAge) {
			this.genotype = null;
			this.genotypeWithAge = genotypeWithAge;
		}
		@Override
		public String toString() {
			if (genotypeWithAge == null)
				return genotype;
			else
				return genotypeWithAge.toString();
		}
	}

}
