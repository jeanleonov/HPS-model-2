package hps.statistic_saving;

import hps.point.Point;
import hps.point_movement.PointMover.IterationSubStep;

public interface StatisticSubcriber {

	void saveSystemState(Point point, int year, IterationSubStep justFinishedSubStep) throws Throwable;
	
}