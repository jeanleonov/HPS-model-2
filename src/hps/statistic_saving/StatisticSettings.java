package hps.statistic_saving;

import hps.point_movement.PointMover.IterationSubStep;
import hps.tools.CMDArgument;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticSettings {

	private static StatisticSettings instance = null;
	public static StatisticSettings get() {
		if (instance == null)
			instance = new StatisticSettings();
		return instance;
	}
	
	public final EnumSet<IterationSubStep> subStepsToSave;
	public final IterationSubStep shortStatisticAfter;
	public final boolean onlyMatures;
	public final boolean onlyGenotypes;
	public final boolean onlyShort;
	public final File statisticFolder;
	
	private static final Pattern SETTINGS_PATTERN = Pattern.compile(
		   "(:?(?<onlyG>only_genotypes)"
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
			+"|(?<afterD>after_dieing))*");
	
	private StatisticSettings() {
		statisticFolder = new File((String)CMDArgument.STATISTIC_FOLDER.getValue());
		if (!statisticFolder.exists())
			statisticFolder.mkdirs();
		String settings = (String)CMDArgument.STATISTIC.getValue();
		Matcher matcher = SETTINGS_PATTERN.matcher(settings);
		matcher.find();
		onlyMatures = matcher.group("onlyM") != null;
		onlyGenotypes = matcher.group("onlyG") != null;
		onlyShort = matcher.group("onlyS") != null;
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
		ArrayList<IterationSubStep> subStepsToSaveList = new ArrayList<>(IterationSubStep.values().length);
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
		if (matcher.group("afterEach") != null || subStepsToSaveList.size()==0)
			for (IterationSubStep step : IterationSubStep.values())
				subStepsToSaveList.add(step);
		subStepsToSave = EnumSet.copyOf(subStepsToSaveList);
	}
}
