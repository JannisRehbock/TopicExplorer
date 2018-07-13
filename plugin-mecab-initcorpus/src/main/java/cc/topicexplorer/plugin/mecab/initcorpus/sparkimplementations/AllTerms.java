package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;



import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import cc.commandmanager.core.Context;

public class AllTerms {

	public static void allTerms(Context context) {
		
		SparkSession spark = (SparkSession) context.get("spark-session");
		
		
		//Rename DOCUMENT_TERM into XXX <- named in {@linkplain DocTerm}
		Dataset<Row> AllTerms = spark.sql("SELECT DOCUMENT_TERM.TERM, COUNT(DISTINCT DOCUMENT_TERM.DOCUMENT_ID) AS COUNT, p2.POS " + 
											"FROM DOCUMENT_TERM, POS_TYPE p1, POS_TYPE p2 " + 
											"WHERE p1.POS=DOCUMENT_TERM.WORDTYPE_CLASS AND p1.LOW>=p2.LOW " + 
											"AND p1.HIGH<=p2.HIGH " + 
											"GROUP BY DOCUMENT_TERM.TERM, p2.POS");
	
			AllTerms.createOrReplaceTempView("AllTerms");
			
	}
	
	
	
}
