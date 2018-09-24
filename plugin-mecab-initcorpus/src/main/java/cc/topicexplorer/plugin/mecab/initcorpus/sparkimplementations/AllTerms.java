package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;



import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import cc.commandmanager.core.Context;

public class AllTerms {

	public static void allTerms(Context context) {
		
		SparkSession spark = (SparkSession) context.get("spark-session");
		
		
		Dataset<Row> AllTerms = spark.sql("SELECT DocTerm.TERM, COUNT(DISTINCT DocTerm.DOCUMENT_ID) AS COUNT, p2.POS " + 
											"FROM DocTerm, PosType p1, PosType p2 " + 
											"WHERE p1.POS=DocTerm.WORDTYPE_CLASS AND p1.LOW>=p2.LOW " + 
											"AND p1.HIGH<=p2.HIGH " + 
											"GROUP BY DocTerm.TERM, p2.POS");
	
			AllTerms.createOrReplaceTempView("AllTerms");
			
	}
	
	
	
}
