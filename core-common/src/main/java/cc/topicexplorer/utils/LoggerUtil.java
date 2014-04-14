package cc.topicexplorer.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerUtil {

	private static Logger logger = Logger.getLogger(LoggerUtil.class);
	private static final String GLOBAL_PROPERTIES_FILENAME = "log4j.global.properties";
	private static final String LOCAL_PROPERTIES_FILENAME = "log4j.local.properties";

	private LoggerUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Initializes a system wide log4j instance. Will look for
	 * log4j.global.properties in the core-common's classpath. Global properties
	 * then will be overwritten with log4j.local.properties located in
	 * core-common/local/main/resources if they exist. See Apache manual
	 * (https://logging.apache.org/log4j/1.2/manual.html) for information on how
	 * to structure a log4j property file.
	 */
	public static void initializeLogger() {
		PropertyConfigurator.configure(loadProperties(GLOBAL_PROPERTIES_FILENAME, LOCAL_PROPERTIES_FILENAME));
		logger.info("Logger successfully initialized");
	}

	private static Properties loadProperties(String globalProperties, String localProperties) {
		Properties logProperties = new Properties();
		try {
			logProperties.load(LoggerUtil.class.getResourceAsStream("/" + globalProperties));
			return logProperties;
		} catch (IOException e) {
			logger.error("resources not found.");
			throw new RuntimeException(e);
		}
	}

}
