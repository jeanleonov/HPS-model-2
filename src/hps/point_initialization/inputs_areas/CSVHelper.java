package hps.point_initialization.inputs_areas;

import java.util.Arrays;

public class CSVHelper {

	private CSVHelper() {}

	public static boolean isInputsEmpty(String[][] rows) {
		return Arrays.stream(rows).allMatch(row -> row.length == 0);
	}

	public static String[][] getTrimmedTable(String input) {
		String[] lines = input.split("\n");
		return getTrimmedTable(lines);
	}

	private static String[][] getTrimmedTable(String[] lines) {
		String[][] rows = new String[lines.length][];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = trimRow(lines[i]).split(";");
			for (int j = 0; j < rows[i].length; j++)
				rows[i][j] = rows[i][j].trim();
		}
		int emptyRows=0;
		for (int i = rows.length-1; i>=0; i--, emptyRows++) {
			boolean isRowEmpty=true;
			for (int j = 0; j<rows[i].length; j++)
				if (!rows[i][j].isEmpty()) {
					isRowEmpty = false;
					break;
				}
			if (!isRowEmpty)
				break;
		}
		if (emptyRows != 0) {
			String[][] filledRows = new String[lines.length-emptyRows][];
			for (int i=0; i<filledRows.length; i++)
				filledRows[i] = rows[i];
			return filledRows;
		}
		return rows;
	}

	private static String trimRow(String line) {
		String result = line.trim();
		if (result.endsWith(";"))
			return result.substring(0, result.length() - 1);
		return result;
	}

	public static boolean isTableConsistent(String[][] rows) {
		int firstRowLength = rows[0].length;
		return Arrays.stream(rows).allMatch(row -> row.length == firstRowLength);
	}
}
