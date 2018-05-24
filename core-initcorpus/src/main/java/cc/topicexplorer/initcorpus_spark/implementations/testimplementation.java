package cc.topicexplorer.initcorpus_spark.implementations;


import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.util.Properties;


public class testimplementation {

	public testimplementation(SparkSession spark, Properties prop ){
		
		String filepath = prop.getProperty("spark.csvInputFile") ;
		
		
		 Dataset<Row> df = spark.read().format("csv")
			      .option("sep", ";")
			      .option("inferSchema", "true")
			      .option("header", "true")
			      .load(filepath);
		
		df.cache();
		df.createOrReplaceTempView("Data");
		 
		
		 
		 
	}
	
	
	
	
	
	
	
}
