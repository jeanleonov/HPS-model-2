package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.ConflictingData;
import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.point_initialization.inputs_areas.Scenario;
import hps.point_initialization.inputs_areas.Viability;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import hps.point.components.IndividualsGroup;

public class ScenarioTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Set<String> knownGenotypes = Viability.getKnownGenotypesSet();
		knownGenotypes.add("xRxR");
		knownGenotypes.add("xRyR");
		knownGenotypes.add("xLxL");
		knownGenotypes.add("xLyL");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Viability.getKnownGenotypesSet().clear();
	}
	
	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure1() throws InvalidInput {
		new Scenario(";xRxR-5;xLxL-5\n15;65");
	}
	
	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new Scenario(";WRONG;xRxR-5;xLxL-5\n15;65;43;54;");
	}
	
	@Test(expected = NotGenotype.class)
	public void testNotGenotype() throws InvalidInput {
		new Scenario(";xRxR-4;xLfL-4\n15;43;54;");
	}
	
	@Test(expected = NotGenotype.class)
	public void testUnknownGenotype() throws InvalidInput {
		new Scenario(";xRxR-6;xL(xL)-6\n15;43;54;");
	}
	
	@Test(expected = NotInteger.class)
	public void testNotIntegerAge() throws InvalidInput {
		new Scenario(";xRxR-5;xLxL-g\n15;43;54;");
	}
	
	@Test(expected = NotInteger.class)
	public void testNotIntegerYear() throws InvalidInput {
		new Scenario(";xRxR-5;xLxL-5\nj5;75;54;");
	}
	
	@Test(expected = NotInteger.class)
	public void testNotIntegerStrength() throws InvalidInput {
		new Scenario(";xRxR-5;xLxL-5\n15;f5;54;");
	}
	
	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		new Scenario(";Resource+;xRxR-5;xLxL-5\n15;98.p;65;54;");
	}
	
	@Test(expected = ConflictingData.class)
	public void testConflictingData() throws InvalidInput {
		new Scenario(";Resource+;Resource*;\n15;98;65;");
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		Scenario scenario = new Scenario(";Resource+;Resource*;xRxR-10\n15;98;-;-;\n16;-;-;70;");
		Assert.assertEquals(100.0, scenario.getScenario().getResources(15, 2), 0.0001);
		Assert.assertEquals(2.0, scenario.getScenario().getResources(16, 2), 0.0001);
		Assert.assertEquals(70, scenario.getScenario().getImmigration(16).get(new IndividualsGroup("xRxR", 10)), 0.0001);
	}
}
