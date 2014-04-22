package point_initialization.inputs_areas;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.Negative;
import exceptions.Exceptions.NotAllGenotypes;
import exceptions.Exceptions.NotDouble;
import exceptions.Exceptions.NotGenotype;
import exceptions.Exceptions.NotInRange;
import exceptions.Exceptions.NotInteger;
import exceptions.Exceptions.WrongFileStructure;

public class ViabilityTest {
	
	private String[][] rows;
	private static final String input = 
	";(xL)xR;(xL)yR\n" +
	"Продолжительность жизни;11;9;\n" +
	"Возраст 1-го нереста;5;4\n" +
	"Выживаемость;0.52;0.45\n" +
	"Возраст достижения выживаемости S;5;4;\n" +
	"Коэффициент после достижения S;0.06;0.03;\n" +
	"I-го года;0.04;0.04;\n" +
	"Коэффициент до достижения S;0.5;0.5\n" +
	"Конкурентоспособность;0.7;0.9;\n" +
	"Возраст достижения C;5;4\n" +
	"Коэффициент после достижения С;0.2;0.2;\n" +
	"I-го года;0.1;0.1;\n" +
	"Коэффициент до достижения С;0.25;0.328\n" +
	"Вероятность размножения;0.9;0.7;\n" +
	"Коэффициент;0.1;0.1\n" +
	"Плодовитость;1200;0.9;\n" +
	"Коэффициент;0.2;0.2\n" +
	"Вероят.повт. амплексусов;0;0.5;\n" +
	"Коэффициент;0;0.4\n" +
	"Потребность в ресурсах;1;1\n" +
	"Потребность в ресурсах;4;4;\n" +
	"Потребность в ресурсах;9;9\n" +
	"Потребность в ресурсах;16;16;\n" +
	"Потребность в ресурсах;22;19;\n" +
	"Потребность в ресурсах;25;20\n" +
	"Потребность в ресурсах;27;21\n" +
	"Потребность в ресурсах;28;21\n" +
	"Потребность в ресурсах;28;21;\n" +
	"Потребность в ресурсах;28;21;\n";

	@Before
	public void setUpBefore() throws Exception {
		rows = CSVHelper.getTrimmedTable(input);
	}
	
	@After
	public void tearDownAfter() throws Exception {
		Viability.getKnownGenotypesSet().clear();
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure() throws InvalidInput {
		rows[6] = new String[7];
		String input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotype() throws InvalidInput {
		rows[0][1] = "xR(xr(";
		String input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotGenotype.class)
	public void testUnknownGenotype() throws InvalidInput {
		String input = getInput(rows);
		new Viability(input);
		rows[0][1] = "xRxR";
		input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotAllGenotypes.class)
	public void testNotAllGenotypes() throws InvalidInput {
		String input = 
		";(xL)xR;(xL)yR;xLyL\n" +
		"Продолжительность жизни;11;9;9;\n" +
		"Возраст 1-го нереста;5;4;4\n" +
		"Выживаемость;0.52;0.45;0.45\n" +
		"Возраст достижения выживаемости S;5;4;4;\n" +
		"Коэффициент после достижения S;0.06;0.03;0.03;\n" +
		"I-го года;0.04;0.04;0.04;\n" +
		"Коэффициент до достижения S;0.5;0.5;0.5\n" +
		"Конкурентоспособность;0.7;0.9;0.9;\n" +
		"Возраст достижения C;5;4;4\n" +
		"Коэффициент после достижения С;0.2;0.2;0.2;\n" +
		"I-го года;0.1;0.1;0.1;\n" +
		"Коэффициент до достижения С;0.25;0.328;0.328\n" +
		"Вероятность размножения;0.9;0.7;0.7;\n" +
		"Коэффициент;0.1;0.1;0.1\n" +
		"Плодовитость;1200;0.9;0.9;\n" +
		"Коэффициент;0.2;0.2;0.2\n" +
		"Вероят.повт. амплексусов;0;0.5;0.5;\n" +
		"Коэффициент;0;0.4;0.4\n" +
		"Потребность в ресурсах;1;1;1\n" +
		"Потребность в ресурсах;4;4;4;\n" +
		"Потребность в ресурсах;9;9;9\n" +
		"Потребность в ресурсах;16;16;16;\n" +
		"Потребность в ресурсах;22;19;19;\n" +
		"Потребность в ресурсах;25;20;20\n" +
		"Потребность в ресурсах;27;21;21\n" +
		"Потребность в ресурсах;28;21;21\n" +
		"Потребность в ресурсах;28;21;21;\n" +
		"Потребность в ресурсах;28;21;21;\n";
		new Viability(input);
		input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		rows[20][1] = "notDouble";
		String input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotInteger.class)
	public void testInteger() throws InvalidInput {
		rows[1][1] = "notInteger";
		String input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = Negative.class)
	public void testNegative() throws InvalidInput {
		rows[1][1] = "-17";
		String input = getInput(rows);
		new Viability(input);
	}

	@Test(expected = NotInRange.class)
	public void testNotInRange() throws InvalidInput {
		rows[3][1] = "1.5";
		String input = getInput(rows);
		new Viability(input);
	}
	
	private String getInput(String[][] rows) {
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<rows.length; i++) {
			for (int j=0; j<rows[i].length; j++)
				buffer.append(rows[i][j]).append(';');
			buffer.append('\n');
		}
		return buffer.toString();
	}

}
