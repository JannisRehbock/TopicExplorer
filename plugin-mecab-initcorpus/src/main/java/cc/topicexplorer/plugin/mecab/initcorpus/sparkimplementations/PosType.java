package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import cc.commandmanager.core.Context;

public class PosType {

	public static void postype (Context context) {
			
			SparkSession spark = (SparkSession) context.get("spark-session");
	
			
			
			
			Dataset<Row> partOne = spark.sql("SELECT POS, COUNT(TERM) AS TERM_COUNT, SUM(COUNT) AS TOKEN_COUNT " + 
											"FROM AllTerms GROUP BY POS");
			partOne.createOrReplaceTempView("partOne");
			
			
			Dataset<Row> partTwo = spark.sql("SELECT WORDTYPE_CLASS, COUNT(DISTINCT DOCUMENT_ID) AS DOCUMENT_COUNT," + 
											"MIN(MIN_TOKEN_LENGTH) AS MIN_TOKEN_LENGTH," + 
											"MAX(MAX_TOKEN_LENGTH) AS MAX_TOKEN_LENGTH," + 
											"SUM(SUM_TOKEN_LENGTH)/" + 
											"SUM(TOKEN_COUNT) AS AVG_TOKEN_LENGTH " + 
											"FROM DocWordType GROUP BY WORDTYPE_CLASS");
			partTwo.createOrReplaceTempView("partTwo");
			
			Dataset<Row> PosType = spark.sql("SELECT * FROM partOne, partTwo, PosType"
											+ "WHERE partOne.POS = partTwo.WORDTYPE_CLASS "
											+ "AND partONE.POS = PosType.POS");
			
			PosType.createOrReplaceTempView("PosType");
			PosType.repartition(1)
			.write()
			.format("com.databricks.spark.csv")
			.option("header", "true")
			.option("delimiter", ";")
			.save("temp/PosType.csv");
			
			//will be saved as temp/PosType.csv/part-00000  !!
			
			
	}
}

