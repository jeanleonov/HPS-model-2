package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.point_initialization.inputs_areas.ResourcesReader;

import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {

	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		new ResourcesReader("fw3g");
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		ResourcesReader resource = new ResourcesReader(" 255 ;\n");
		Assert.assertEquals(255.0, resource.getResources(), 0.0001);
		resource = new ResourcesReader(" 255.0 ;\n");
		Assert.assertEquals(255.0, resource.getResources(), 0.0001);		
	}

}
