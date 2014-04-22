package point_initialization.inputs_areas;

import org.junit.Assert;
import org.junit.Test;

import exceptions.Exceptions.InvalidInput;
import exceptions.Exceptions.NotDouble;

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
