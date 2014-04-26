package hps.point_initialization.inputs_areas;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.NotDouble;
import hps.point_initialization.inputs_areas.Resources;

import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {

	@Test(expected = NotDouble.class)
	public void testNotDouble() throws InvalidInput {
		new Resources("fw3g");
	}
	
	@Test
	public void testNominal() throws InvalidInput {
		Resources resource = new Resources(" 255 ;\n");
		Assert.assertEquals(255.0, resource.getResources(), 0.0001);
		resource = new Resources(" 255.0 ;\n");
		Assert.assertEquals(255.0, resource.getResources(), 0.0001);		
	}

}
