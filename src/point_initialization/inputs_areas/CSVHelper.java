package point_initialization.inputs_areas;

public class CSVHelper {

	private CSVHelper(){}
	
	public static boolean isInputsEmpty(String[] lines) {
		if (lines.length == 0)
			return true;
		for(String line : lines)
			if (!line.trim().isEmpty())
				return false;
		return true;
	}
	
	public static boolean isInputsEmpty(String[][] rows) {
		if (rows.length == 0)
			return true;
		for(String[] row : rows)
			if (row.length != 0)
				return false;
		return true;
	}
	
	public static String trimRow(String line) {
		String result = line.trim();
		if (result.endsWith(";"))
			return result.substring(0, result.length()-1);
		return result;
	}
	
	public static void trimRows(String[] lines) {
		for (int i=0; i<lines.length; i++)
			lines[i] = trimRow(lines[i]);
	}
	
	public static String[][] getTrimmedTable(String input) {
		String[] lines = input.split("\n");
		return getTrimmedTable(lines);
	}
	
	public static String[][] getTrimmedTable(String[] lines) {
		String[][] rows = new String[lines.length][];
		for (int i=0; i<rows.length; i++) {
			rows[i] = trimRow(lines[i]).split(";");
			for (int j=0; j<rows[i].length; j++)
				rows[i][j] = rows[i][j].trim();
		}
		return rows;
	}
	
	public static boolean isTableConsistent(String[][] rows) {
		int firstRowLength = rows[0].length;
		for (int i=1; i<rows.length; i++)
			if (rows[i].length != firstRowLength)
				return false;
		return true;
	}
}
