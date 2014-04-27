package hps.exceptions;

import hps.point.components.GenotypeHelper;
import hps.point_initialization.inputs_areas.ViabilityReader;

import java.io.IOException;

public class Exceptions {
	
	private Exceptions() {}
	
	public static class InvalidInput extends IOException {
		private static final long serialVersionUID = 1L;
		public InvalidInput(String message, String inputArea, int row, int column) {
			super(String.format("Error in input \"%s\" (row# %d, column# %d):\n%s",
								inputArea, row, column, message));
		}
		public InvalidInput(String message, String inputArea) {
			super(String.format("Error in input \"%s\":\n%s",
								inputArea, message));
		}
	}
	
	public static class NotGenotype extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public NotGenotype(String wrongString, String inputArea, int row, int column) {
			super(getMsg(wrongString), inputArea, row, column);
		}
		private static String getMsg(String wrongString) {
			if (GenotypeHelper.isLookedAsGenotype(wrongString)) {
				String format = "The genotype \"%s\" is unkown. Known are just genotypes, which specified in Viability file";
				return String.format(format, wrongString);
			}
			else
				return String.format("The string \"%s\" is not a presentation of a genotype",
									 wrongString);
		}
	}
	
	public static class NotAllGenotypes extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public NotAllGenotypes() {
			super("All viability files should contain the same set of genotypes", ViabilityReader.INPUT_AREA);
		}
	}
	
	public static class Negative extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public Negative(int value, String inputArea, int row, int column) {
			super(String.format("This parameter cann't be negative, but actuale value is %d", value),
				  inputArea, row, column);
		}
		public Negative(double value, String inputArea, int row, int column) {
			super(String.format("This parameter cann't be negative, but actuale value is %f", value),
				  inputArea, row, column);
		}
	}
	
	public static class NotInRange extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public NotInRange(int min, int max, int value, String inputArea, int row, int column) {
			super(String.format("This parameter cann't be <%d and >%d, but actuale value is %d",
								min, max, value),
				  inputArea, row, column);
		}
		public NotInRange(double min, double max, double value, String inputArea, int row, int column) {
			super(String.format("This parameter cann't be <%f and >%f, but actuale value is %f",
								min, max, value),
				  inputArea, row, column);
		}
	}
	
	public static class NotInteger extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public NotInteger(String actualString, String inputArea, int row, int column) {
			super(String.format("String representation of an integer was expected, "+
								"but actual value is \"%s\"", actualString),
				  inputArea, row, column);
		}
		public NotInteger(String actualString, String inputArea) {
			super(String.format("String representation of an integer was expected, "+
								"but actual value is \"%s\"", actualString), inputArea);
		}
	}
	
	public static class NotDouble extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public NotDouble(String actualString, String inputArea, int row, int column) {
			super(String.format("String representation of an double number was expected, "+
								"but actual value is \"%s\"", actualString),
				  inputArea, row, column);
		}
		public NotDouble(String actualString, String inputArea) {
			super(String.format("String representation of an double number was expected, "+
								"but actual value is \"%s\"", actualString), inputArea);
		}
	}
	
	public static class WrongFileStructure extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public WrongFileStructure(String message, String inputArea) {
			super(String.format("Wrong file structure (%s)", message), inputArea);
		}
	}
	
	public static class UnknownHabitat extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public UnknownHabitat(String actualHabitat, String inputArea, int row, int column) {
			super(String.format("Habitat \"%s\" doesn't exist", actualHabitat),
				  inputArea, row, column);
		}
	}
	
	public static class WrongParentsPair extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public WrongParentsPair(String first, String second, String inputArea, int row, int column) {
			super(String.format("Individuals with the same sex (%s and %s) cann't create posterity", first, second),
				  inputArea, row, column);
		}
	}
	
	public static class ConflictingData extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public ConflictingData(String message, String inputArea, int row, int column) {
			super(String.format("Conflicting data (%s)", message),
				  inputArea, row, column);
		}
	}
	
	public static class WrongDimensionType extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public WrongDimensionType(String actualType, String inputArea, int row, int column) {
			 super(String.format("Dimension type \"%s\" is invalid (only \"integer\", " +
					 			 "\"float\" and \"enumeration\" are supported)", actualType),
				  inputArea, row, column);
		}
	}
	
	public static class UnknownDimension extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public UnknownDimension(String actualDimension, String inputArea) {
			 super(String.format("Dimension \"%s\" is unknown", actualDimension), inputArea);
		}
	}
	
	public static class WrongPointNumber extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public WrongPointNumber(int actualNumber) {
			 super(String.format("Point number \"%d\" is too big or negative", actualNumber), "Program arguments");
		}
	}
	
	public static class WrongUsageOfDimension extends InvalidInput {
		private static final long serialVersionUID = 1L;
		public WrongUsageOfDimension(String message, String inputArea) {
			 super(message, inputArea);
		}
	}
	
	public static class FolderDoesntExist extends IOException {
		private static final long serialVersionUID = 1L;
		public FolderDoesntExist(String folderPath) {
			super(String.format("Folder \"%s\" doesn't exist", folderPath));
		}
		public FolderDoesntExist(String folderPath, String message) {
			super(String.format("Folder \"%s\" doesn't exist (%s)", folderPath, message));
		}
	}
	
	public static class FileDoesntExist extends IOException {
		private static final long serialVersionUID = 1L;
		public FileDoesntExist(String filePath) {
			super(String.format("File \"%s\" doesn't exist", filePath));
		}
		public FileDoesntExist(String filePath, String message) {
			super(String.format("File \"%s\" doesn't exist (%s)", filePath, message));
		}
	}
}