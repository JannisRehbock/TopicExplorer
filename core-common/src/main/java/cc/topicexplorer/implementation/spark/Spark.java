package cc.topicexplorer.implementation.spark;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.SparkConf;


import java.util.Properties;

import org.apache.log4j.Logger;

public class Spark {
	private static final Logger logger = Logger.getLogger(Spark.class);
	

	
//	private Properties properties;
	
	
	private String master = "local[*]"; 	// gets overwritten by respective Property
	
	private void Memorycheck() {
		
		final long currentHeapSize = Runtime.getRuntime().totalMemory();
		final long maxHeapSize = Runtime.getRuntime().maxMemory();
		logger.info("jvm Current Heap Size: " + currentHeapSize/1024 + "MB");
		logger.info("jvm Max Heap Size: " + maxHeapSize/1024 + "MB");
		
	}
	
	
	public SparkSession CreateSpark(Properties prop) {
		
		
		
		SparkConf conf =new SparkConf();
		conf.setMaster(master);			//gets changed if Property is specified in .properties file
		//conf = this.setProps(prop, conf);
		
		
	    SparkSession spark = SparkSession
	      .builder()
	      .appName("TE-Spark")
	      .config(conf)
	      .getOrCreate();
	    
	/*    if(conf.get("Master") == "local") {
	    	this.Memorycheck();
	    }*/
	    
	    return spark;
	  
	}
	
	private SparkConf setProps(Properties prop, SparkConf conf) {
		
		
		conf.setMaster(prop.getProperty("master")) ;
//		conf.set("spark.executor.memory", this.properties.getProperty(spark.executorMemory));
		
		return conf;
	}
	
	
	
}
