package hps.point_initialization;

import hps.exceptions.Exceptions.InvalidInput;
import hps.exceptions.Exceptions.Negative;
import hps.exceptions.Exceptions.NotDouble;
import hps.exceptions.Exceptions.NotInteger;
import hps.exceptions.Exceptions.UnknownDimension;
import hps.exceptions.Exceptions.WrongDimensionType;
import hps.exceptions.Exceptions.WrongFileStructure;
import hps.exceptions.Exceptions.WrongPointNumber;
import hps.exceptions.Exceptions.WrongUsageOfDimension;
import hps.point_initialization.InputsPreparer;

import org.junit.Test;
import org.junit.Assert;

public class InputsPreparerTest {

	@Test(expected = WrongFileStructure.class)
	public void testWeongFileStructure() throws InvalidInput {
		new InputsPreparer("dim32;enu;54\ndim54;fff");
	}

	@Test(expected = NotInteger.class)
	public void testNotInteger() throws InvalidInput {
		new InputsPreparer("dim1;enumeration;23.5");
	}

	@Test(expected = Negative.class)
	public void testNegative() throws InvalidInput {
		new InputsPreparer("dim1;integer;-23");
	}

	@Test(expected = WrongDimensionType.class)
	public void testWrongDimensionType() throws InvalidInput {
		new InputsPreparer("dim1;ointegero;23");
	}

	@Test(expected = WrongPointNumber.class)
	public void testTooLowPointNumber() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(0);
	}

	@Test(expected = WrongPointNumber.class)
	public void testTooBigPointNumber() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(501);
	}

	@Test(expected = UnknownDimension.class)
	public void testUnknownDimension() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(1);
		preparer.getPreparedContent("Trololo #[unknownDim:10-100] helolo", "Some area");
	}

	@Test(expected = WrongUsageOfDimension.class)
	public void testWrongUsageOfDimension() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(1);
		preparer.getPreparedContent("Trololo #{dim1}{#value1#}{#value2#}{#value3#}", "Some area");
	}

	@Test(expected = NotInteger.class)
	public void testNotIntegerInContent() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(1);
		preparer.getPreparedContent("Trololo #[dim1:12.3-54] helolo", "Some area");
	}

	@Test(expected = NotDouble.class)
	public void testNotDoubleInContent() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;5");
		preparer.setPoint(1);
		preparer.getPreparedContent("Trololo #[dim2:12.3-5t4] helolo", "Some area");
	}

	@Test
	public void testNominal() throws InvalidInput {
		InputsPreparer preparer = new InputsPreparer("dim1;integer;25\ndim2;float;4\ndim3;enumeration;3");
		preparer.setPoint(1);
		String content = preparer.getPreparedContent("Trololo #[dim2:12.3-54] helolo", "Some area");
		Assert.assertEquals("Trololo 12.3 helolo", content);
		preparer.setPoint(300);
		content = preparer.getPreparedContent("Trololo #{dim3}{#value1#}{#value2#}{#value3#} helolo #[dim1:10-100]", "Some area");
		Assert.assertEquals("Trololo value3 helolo 100", content);
		preparer.setPoint(6);
		content = preparer.getPreparedContent("Trololo #{dim3}{#value1#}{#value2#}{#value3#} helolo #[dim1:10-100] helolo #[dim2:0-30]", "Some area");
		Assert.assertEquals("Trololo value3 helolo 10 helolo 10.0", content);
	}
}
