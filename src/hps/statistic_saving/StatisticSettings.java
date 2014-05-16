package hps.statistic_saving;

import hps.point_movement.PointMover.IterationSubStep;
import hps.program_starter.HPS;
import hps.tools.CMDArgument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticSettings {

	private static StatisticSettings instance = null;
	public static StatisticSettings get() throws IOException, InterruptedException {
		if (instance == null)
			instance = new StatisticSettings();
		return instance;
	}
	
	public EnumSet<IterationSubStep> subStepsToSave;
	public IterationSubStep shortStatisticAfter = IterationSubStep.MOVEMENT;
	public boolean onlyMatures = false;
	public boolean onlyGenotypes = false;
	public boolean onlyShort = false;
	public File statisticFolder;
	
	private static final Pattern SETTINGS_PATTERN = Pattern.compile(
		   "(?<onlyG>only_genotypes)"
			+"|(?<onlyM>only_matures)"
			+"|(?<onlyS>only_short)"
			+"|(?<afterEach>after_each)"
			+"|(?<shortAfterM>short_after_movement)"
			+"|(?<shortAfterG>short_after_growing)"
			+"|(?<shortAfterR>short_after_reproduction)"
			+"|(?<shortAfterC>short_after_competition)"
			+"|(?<shortAfterD>short_after_dieing)"
			+"|(?<afterM>after_movement)"
			+"|(?<afterG>after_growing)"
			+"|(?<afterR>after_reproduction)"
			+"|(?<afterC>after_competition)"
			+"|(?<afterD>after_dieing)");
	
	private StatisticSettings() throws IOException, InterruptedException {
		statisticFolder = new File(HPS.get().getOutputsFolder().getPath() + "/statistic");
		if (!statisticFolder.exists())
			statisticFolder.mkdirs();
		String settings = (String)CMDArgument.STATISTIC.getValue();
		Matcher matcher = SETTINGS_PATTERN.matcher(settings);
		ArrayList<IterationSubStep> subStepsToSaveList = new ArrayList<>(IterationSubStep.values().length);
		while (matcher.find()) {
			onlyMatures = onlyMatures || matcher.group("onlyM") != null;
			onlyGenotypes = onlyGenotypes || matcher.group("onlyG") != null;
			onlyShort = onlyShort || matcher.group("onlyS") != null;
			if (matcher.group("shortAfterG") != null)
				shortStatisticAfter = IterationSubStep.GROWING_UP;
			else if (matcher.group("shortAfterR") != null)
				shortStatisticAfter = IterationSubStep.REPRODUCTION;
			else if (matcher.group("shortAfterC") != null)
				shortStatisticAfter = IterationSubStep.COMPETITION;
			else if (matcher.group("shortAfterD") != null)
				shortStatisticAfter = IterationSubStep.DIEING;
			else
				shortStatisticAfter = IterationSubStep.MOVEMENT;
			if (matcher.group("afterM") != null)
				subStepsToSaveList.add(IterationSubStep.MOVEMENT);
			if (matcher.group("afterG") != null)
				subStepsToSaveList.add(IterationSubStep.GROWING_UP);
			if (matcher.group("afterR") != null)
				subStepsToSaveList.add(IterationSubStep.REPRODUCTION);
			if (matcher.group("afterC") != null)
				subStepsToSaveList.add(IterationSubStep.COMPETITION);
			if (matcher.group("afterD") != null)
				subStepsToSaveList.add(IterationSubStep.DIEING);
			if (matcher.group("afterEach") != null)
				for (IterationSubStep step : IterationSubStep.values())
					subStepsToSaveList.add(step);
		}
		if (subStepsToSaveList.size() == 0)
			for (IterationSubStep step : IterationSubStep.values())
				subStepsToSaveList.add(step);
		subStepsToSave = EnumSet.copyOf(subStepsToSaveList);
	}
}
