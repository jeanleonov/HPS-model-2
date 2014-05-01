package hps.tools;

import hps.program_starter.HPS;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class Logger {
	
	private Logger() {}
	
	
	public static void fatal(Throwable e) {
		logger.fatal(throwableToString(e));
	}
	
	public static void fatal(String message) {
		logger.fatal(message);
	}
	
	public static void error(Throwable e) {
		logger.error(throwableToString(e));
	}
	
	public static void error(String message) {
		logger.error(message);
	}
	
	public static void warning(Throwable e) {
		logger.warn(throwableToString(e));
	}
	
	public static void warning(String message) {
		logger.warn(message);
	}
	
	public static void info(Throwable e) {
		logger.info(throwableToString(e));
	}
	
	public static void info(String message) {
		logger.info(message);
	}
	
	public static void debug(Throwable e) {
		logger.debug(throwableToString(e));
	}
	
	public static void debug(String message) {
		logger.debug(message);
	}
	
	
	private static long startPointTime;
	public static void openPoint() throws IOException {
		startPointTime = System.currentTimeMillis();
		info(String.format("Modeling of point %d have been just started",
						   HPS.get().getCurrentPointNumber()));
	}
	
	public static void closePoint() throws IOException {
		long executingTime = System.currentTimeMillis()-startPointTime,
				 hour = executingTime/1000/60/60,
				 min = executingTime/1000/60 - hour*60,
				 sec = executingTime/1000 - min*60 - hour*3600,
				 msec = executingTime - sec*1000 - min*60000 - hour*3600000;
		info(String.format("Point: %s, Executing time:	[%2s:%2s:%2s.%3s]",
						   HPS.get().getCurrentPointName(),hour,min,sec,msec ));
	}

	
	private static long startExperimentTime;
	public static void openExperiment() throws IOException {
		startExperimentTime = System.currentTimeMillis();
		info(String.format("Modeling of experiment %d on point %d have been just started",
				           HPS.get().getCurrentExperimentNumber(),
				           HPS.get().getCurrentPointNumber()));
	}
	
	public static void closeExperiment() throws IOException {
		long executingTime = System.currentTimeMillis()-startExperimentTime,
				 hour = executingTime/1000/60/60,
				 min = executingTime/1000/60 - hour*60,
				 sec = executingTime/1000 - min*60 - hour*3600,
				 msec = executingTime - sec*1000 - min*60000 - hour*3600000;
		info(String.format("Point: %s, Experiment: %s, Executing time:	[%2s:%2s:%2s.%3s]",
					       HPS.get().getCurrentPointName(),
					       HPS.get().getCurrentExperimentName(),hour,min,sec,msec ));
	}
	
	
	private static String throwableToString(Throwable throwable) {
		StringBuffer stackTrace = new StringBuffer();
		stackTrace.append(throwable.getMessage());
		stackTrace.append('\n');
		StackTraceElement[] stack = throwable.getStackTrace();
		for (int i=0; i<stack.length; i++)
			stackTrace.append("\t"+stack[i].toString()+"\n");
		return stackTrace.toString();
	}
	
	private final static String CONFIGURATION = 
	"<log4j:configuration>" +
	
		"<appender name=\"ConsoleAppender\" class=\"org.apache.log4j.ConsoleAppender\">" +
			"<param name=\"target\" value=\"System.out\"/>" +
			"<layout class=\"org.apache.log4j.PatternLayout\">" +
				"<param name=\"ConversionPattern\" value=\"%d{ISO8601} [%5p] %m%n\"/>" +
			"</layout>" +
			"<filter class=\"org.apache.log4j.varia.LevelRangeFilter\">" +
				"<param name=\"LevelMin\" value=\"INFO\"/>" +
			"</filter>" +
		"</appender>" +
	
		"<appender name=\"FileAppender\" class=\"org.apache.log4j.DailyRollingFileAppender\">" +
			"<param name=\"File\" value=\""+(String)CMDArgument.LOGS_FOLDER.getValue()+"/logs\" />" +
			"<param name=\"immediateFlush\" value=\"true\"/>" +
			"<param name=\"DatePattern\" value=\"'-'yyyy-MM-dd-HH'.log'\" />" +
			"<layout class=\"org.apache.log4j.PatternLayout\">" +
				"<param name=\"ConversionPattern\" value=\"%d{ISO8601} [%5p] %m%n\"/>" +
			"</layout>" +
			"<filter class=\"org.apache.log4j.varia.LevelRangeFilter\">" +
				"<param name=\"LevelMin\" value=\"INFO\"/>" +
			"</filter>" +
		"</appender>" +
	
		"<appender name=\"ErrorsFileAppender\" class=\"org.apache.log4j.DailyRollingFileAppender\">" +
			"<param name=\"File\" value=\""+(String)CMDArgument.LOGS_FOLDER.getValue()+"/errors\" />" +
			"<param name=\"DatePattern\" value=\"'-'yyyy-MM-dd-HH'.log'\" />" +
			"<param name=\"immediateFlush\" value=\"true\"/>" +
			"<layout class=\"org.apache.log4j.PatternLayout\">" +
				"<param name=\"ConversionPattern\" value=\"%d{ISO8601} [%5p] %m at %l%n\"/>" +
			"</layout>" +
			"<filter class=\"org.apache.log4j.varia.LevelRangeFilter\">" +
				"<param name=\"LevelMin\" value=\"ERROR\"/>" +
			"</filter>" +
		"</appender>" +
	
		"<logger name=\"HPSLogger\">" +
			"<level value=\"INFO\"/>" +
			"<appender-ref ref=\"ErrorsFileAppender\"/>" +
			"<appender-ref ref=\"FileAppender\"/>" +
			"<appender-ref ref=\"ConsoleAppender\"/>" +
		"</logger>" +
	
	"</log4j:configuration>";
	
	static {
		File logsFolder = new File((String)CMDArgument.LOGS_FOLDER.getValue());
		if (!logsFolder.exists() || !logsFolder.isDirectory())
			logsFolder.mkdirs();
		Element node = null;
		try {
			node = DocumentBuilderFactory
					    .newInstance()
					    .newDocumentBuilder()
					    .parse(new ByteArrayInputStream(CONFIGURATION.getBytes()))
					    .getDocumentElement();
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// This situation is impossible
			e.printStackTrace();
		}
		DOMConfigurator.configure(node);
	}
	
	public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("HPSLogger");

}
