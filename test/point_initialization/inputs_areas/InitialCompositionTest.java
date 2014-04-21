package point_initialization.inputs_areas;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import point.components.IndividualsGroup;
import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotGenotype;
import exceptions.Exceptions.NotInteger;
import exceptions.Exceptions.WrongFileStructure;

public class InitialCompositionTest {

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure1() throws InvalidInput {
		new InitialComposition("xRxR-0;xRxR");
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new InitialComposition("xRxR-0;xRxR\nefrwg");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInHeader() throws InvalidInput {
		new InitialComposition("xRxR-0;xRxR-y\n14;342");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInStrengthes() throws InvalidInput {
		new InitialComposition("xRxR-0;xRxR-5\nj4;342");
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotype() throws InvalidInput {
		new InitialComposition("xR(xR-0;xRxR-5\n64;342");
	}

	@Test
	public void testEmpty() throws InvalidInput {
		InitialComposition composition = new InitialComposition("\n;\n;");
		Assert.assertEquals(composition.getInitialComposition().size(), 0);
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		InitialComposition composition = new InitialComposition("xRxR-5;xRyR-9\n25;81");
		Map<IndividualsGroup,Integer> map = composition.getInitialComposition();
		Assert.assertEquals(map.get(new IndividualsGroup("xRxR", 5)).intValue(), 25);
		Assert.assertEquals(map.get(new IndividualsGroup("xRyR", 9)).intValue(), 81);
	}

}
