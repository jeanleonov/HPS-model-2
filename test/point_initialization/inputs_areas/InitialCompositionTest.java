package point_initialization.inputs_areas;

import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import point.components.IndividualsGroup;
import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotGenotype;
import exceptions.Exceptions.NotInteger;
import exceptions.Exceptions.WrongFileStructure;

public class InitialCompositionTest {
	
	@BeforeClass
	public static void initiateKnownGenotypes() {
		Set<String> knownGenotypes = Viability.getKnownGenotypesSet();
		knownGenotypes.add("xRxR");
		knownGenotypes.add("xRyR");
	}
	
	@AfterClass
	public static void removeKnownGenotypes() {
		Viability.getKnownGenotypesSet().clear();
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure1() throws InvalidInput {
		new InitialComposition("xRxR-0;xRyR");
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new InitialComposition("xRxR-0;xRyR\nefrwg");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInHeader() throws InvalidInput {
		new InitialComposition("xRxR-0;xRyR-y\n14;342");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInStrengthes() throws InvalidInput {
		new InitialComposition("xRxR-0;xRyR-5\nj4;342");
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotype() throws InvalidInput {
		new InitialComposition("xR(xR-0;xRyR-5\n64;342");
	}

	@Test(expected = NotGenotype.class)
	public void testUnknonwnGenotype() throws InvalidInput {
		new InitialComposition("xRxR-0;xRyL-5\n64;342");
	}

	@Test
	public void testEmpty() throws InvalidInput {
		InitialComposition composition = new InitialComposition("\n;\n;");
		Assert.assertEquals(composition.getInitialComposition().size(), 0);
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		InitialComposition composition = new InitialComposition("xRxR-5;xRyR-9;\n25;81;\n;;;;");
		Map<IndividualsGroup,Integer> map = composition.getInitialComposition();
		Assert.assertEquals(map.get(new IndividualsGroup("xRxR", 5)).intValue(), 25);
		Assert.assertEquals(map.get(new IndividualsGroup("xRyR", 9)).intValue(), 81);
	}

}
