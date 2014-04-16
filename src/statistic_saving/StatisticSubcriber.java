package statistic_saving;

import point.Point;
import point_movement.PointMover.IterationSubStep;

public interface StatisticSubcriber {

	void saveSystemState(Point point, int year, IterationSubStep justFinishedSubStep);
	
}