package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.UnknownHabitat;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.point_initialization.inputs_areas.Neighbors;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NeighborsTest {
	
	private static Set<String> existingHabitats;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		existingHabitats = new HashSet<>();
		existingHabitats.add("Habitat-1");
		existingHabitats.add("Habitat-2");
		existingHabitats.add("Habitat-3");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		existingHabitats.clear();
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure1() throws InvalidInput {
		new Neighbors("Habitat-2;Habitat-3;", existingHabitats);
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new Neighbors("Habitat-2;Habitat-3;\n0.43", existingHabitats);
	}

	@Test(expected = UnknownHabitat.class)
	public void testUnknownHabitat() throws InvalidInput {
		new Neighbors("Habitat-2;UnknownHabitat;\n0.52;2.34;;", existingHabitats);
	}

	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		new Neighbors("Habitat-2;Habitat-3;\n0.43;o.34", existingHabitats);
	}
	
	@Test
	public void testEmpty() throws InvalidInput {
		Neighbors neighbors = new Neighbors(";;\n;;", existingHabitats);
		Assert.assertNotNull(neighbors.getMigrationProbabilities());
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		Neighbors neighbors = new Neighbors("Habitat-2;Habitat-3;\n0.43;0.34", existingHabitats);
		Map<String,Double> probabilities = neighbors.getMigrationProbabilities();
		Assert.assertEquals(0.43, probabilities.get("Habitat-2"), 0.0001);
		Assert.assertEquals(0.34, probabilities.get("Habitat-3"), 0.0001);
	}

}
