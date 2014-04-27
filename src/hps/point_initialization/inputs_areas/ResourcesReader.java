package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;

public class ResourcesReader {

	private double resources;
	public final static String INPUT_AREA = "Resources";
	
	public ResourcesReader(String input) throws InvalidInput {
		String[][] rows = CSVHelper.getTrimmedTable(input);
		if (CSVHelper.isInputsEmpty(rows))
			resources = 0;
		else {
			try {
				resources = Double.parseDouble(rows[0][0]);
			} catch (NumberFormatException e) {
				throw new NotDouble(input, INPUT_AREA, 1, 1);
			}
		}
	}

	public double getResources() {
		return resources;
	}

}
