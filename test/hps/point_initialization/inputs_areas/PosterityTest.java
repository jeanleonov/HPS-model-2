package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotGenotype;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.exceptions.Exceptions.WrongParentsPair;
import hps.point_initialization.inputs_areas.PosterityReader;
import hps.point_initialization.inputs_areas.ViabilityReader;

import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PosterityTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Set<String> knownGenotypes = ViabilityReader.getKnownGenotypesSet();
		knownGenotypes.add("xRxR");
		knownGenotypes.add("xRyR");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ViabilityReader.getKnownGenotypesSet().clear();
	}

	@Test(expected = WrongFileStructure.class)
	public void testWrongFileStructure2() throws InvalidInput {
		new PosterityReader(";;xRxR;xRyR\nsdff;");
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotypeInHeader() throws InvalidInput {
		new PosterityReader(";;xRxR;xRyL\nxRxR;xRyR;0.25;0.75;");
	}

	@Test(expected = NotGenotype.class)
	public void testNotGenotypeInRow() throws InvalidInput {
		new PosterityReader(";;xRxR;xRyR\nxRxL;xRyR;0.25;0.75;");
	}

	@Test(expected = WrongParentsPair.class)
	public void testWrongParentsPair() throws InvalidInput {
		new PosterityReader(";;xRxR;xRyR\nxRxR;xRxR;0.25;0.75;");
	}

	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		new PosterityReader(";;xRxR;xRyR\nxRxR;xRyR;0.o5;0.75;");
	}
	
	@Test
	public void testEmpty() throws InvalidInput {
		PosterityReader posterity = new PosterityReader(";;\n;");
		Assert.assertNotNull(posterity.getPosterity());
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		PosterityReader posterity = new PosterityReader(";;xRxR;xRyR\nxRxR;xRyR;0.25;0.75;");
		Map<String,Double> composition = posterity.getPosterity().getCompositionFor("xRxR").get("xRyR"); 
		Assert.assertEquals(0.25, composition.get("xRxR"), 0.0001); 
		Assert.assertEquals(0.75, composition.get("xRyR"), 0.0001);
	}
}
