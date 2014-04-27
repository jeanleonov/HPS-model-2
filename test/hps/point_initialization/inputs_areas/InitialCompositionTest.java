package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.point_initialization.inputs_areas.InitialCompositionReader;
import hps.point_initialization.inputs_areas.ViabilityReader;

import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import hps.point.components.IndividualsGroup;

public class InitialCompositionTest {
	
	@BeforeClass
	public static void initiateKnownGenotypes() {
		Set<String> knownGenotypes = ViabilityReader.getKnownGenotypesSet();
		knownGenotypes.add("xRxR");
		knownGenotypes.add("xRyR");
	}
	
	@AfterClass
	public static void removeKnownGenotypes() {
		ViabilityReader.getKnownGenotypesSet().clear();
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure1() throws InvalidInput {
		new InitialCompositionReader("xRxR-0;xRyR");
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new InitialCompositionReader("xRxR-0;xRyR\nefrwg");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInHeader() throws InvalidInput {
		new InitialCompositionReader("xRxR-0;xRyR-y\n14;342");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInStrengthes() throws InvalidInput {
		new InitialCompositionReader("xRxR-0;xRyR-5\nj4;342");
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotype() throws InvalidInput {
		new InitialCompositionReader("xR(xR-0;xRyR-5\n64;342");
	}

	@Test(expected = NotGenotype.class)
	public void testUnknonwnGenotype() throws InvalidInput {
		new InitialCompositionReader("xRxR-0;xRyL-5\n64;342");
	}

	@Test
	public void testEmpty() throws InvalidInput {
		InitialCompositionReader composition = new InitialCompositionReader("\n;\n;");
		Assert.assertEquals(composition.getInitialComposition().size(), 0);
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		InitialCompositionReader composition = new InitialCompositionReader("xRxR-5;xRyR-9;\n25;81;\n;;;;");
		Map<IndividualsGroup,Integer> map = composition.getInitialComposition();
		Assert.assertEquals(map.get(new IndividualsGroup("xRxR", 5)).intValue(), 25);
		Assert.assertEquals(map.get(new IndividualsGroup("xRyR", 9)).intValue(), 81);
	}

}
