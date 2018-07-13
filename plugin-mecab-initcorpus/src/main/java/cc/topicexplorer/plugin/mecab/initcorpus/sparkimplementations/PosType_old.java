package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;

import java.util.Properties;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import cc.commandmanager.core.Context;


public class PosType_old{

	public static void posType(Context context) {
		Properties properties = context.get("properties", Properties.class);
		String textAnalyzer = properties.getProperty("Mecab_text-analyzer").trim();
		SparkSession spark = (SparkSession) context.get("spark-session");
		
			if ("mecab".equals(textAnalyzer)) {
				
				Dataset<Row> df = spark.read().format("csv")
					      .option("sep", ";")
					      .option("inferSchema", "true")
					      .option("header", "true")
					      .load("mecabJap.csv");
				df.createOrReplaceTempView("PosType");
				
			} else if ("treetagger".equals(textAnalyzer)) {
				
				String treeTaggerModel = properties.getProperty("Mecab_treetagger-model").trim();
				
				if ("/german-utf8.par".equals(treeTaggerModel)) {
					
					Dataset<Row> df = spark.read().format("csv")
						      .option("sep", ";")
						      .option("inferSchema", "true")
						      .option("header", "true")
						      .load("mecabGer.csv");
					df.createOrReplaceTempView("PosType");
				
					
				} else if ("/english-utf8.par".equals(treeTaggerModel)) {

					Dataset<Row> df = spark.read().format("csv")
						      .option("sep", ";")
						      .option("inferSchema", "true")
						      .option("header", "true")
						      .load("mecabEng.csv");
					df.createOrReplaceTempView("PosType");
				}
				
				
			}
			
			
	}
		
}
		
		
	
	
	

