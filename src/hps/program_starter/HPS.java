package hps.program_starter;

import hps.exceptions.Exceptions.InvalidInput;
import hps.tools.CMDArgument;
import hps.tools.CMDLineParser;
import hps.tools.Logger;

public class HPS {

	public static void main(String[] args) {
		try {
			parseArgs(args);
			
		} catch(InvalidInput e) {
			Logger.warning(e);
		} catch(Throwable e) {
			Logger.error(e);
		}
	}
	
	private static void parseArgs(String[] args) {
		try {
	        CMDArgument.parse(args);
	    }
	    catch(CMDLineParser.OptionException e) {
	    	Logger.error(e);
	        System.out.println(CMDArgument.HELP_TEXT);
	        System.exit(2);
	    }
		if((Boolean) CMDArgument.HELP.getValue()) {
	        System.out.println(CMDArgument.HELP_TEXT);
	        System.exit(0);
		}
	}

}
