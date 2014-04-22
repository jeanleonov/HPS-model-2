package point_initialization.inputs_areas;

import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotDouble;

public class Resources {

	private double resources;
	private final static String INPUT_AREA = "Resources";
	
	public Resources(String input) throws InvalidInput {
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
