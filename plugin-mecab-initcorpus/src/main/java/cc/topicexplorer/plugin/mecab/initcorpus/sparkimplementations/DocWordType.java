package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;


import cc.commandmanager.core.Context;


import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;



public class DocWordType {

	public static void docWordType(Context context) {
		
		SparkSession spark = (SparkSession) context.get("spark-session");	
		
		
		//Rename DOCUMENT_TERM into XXX <- named in {@linkplain DocTerm}
		Dataset<Row> DocWordType = spark.sql("SELECT meta.DOCUMENT_ID as DOCUMENT_ID, p.POS, COUNT(*) AS TOKEN_COUNT,COUNT(DISTINCT dt.TERM) as TERM_COUNT, "
				+ "MIN(char_length(dt.TOKEN)) as MIN_TOKEN_LENGTH, MAX(char_length(dt.TOKEN)) as MAX_TOKEN_LENGTH, "
				+ "SUM(char_length(dt.TOKEN)) as SUM_TOKEN_LENGTH FROM orgTable_meta as meta, DOCUMENT_TERM dt, PosType subtype, PosType p "
				+ "WHERE subtype.POS = dt.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and subtype.HIGH <= p.HIGH and "
				+ "dt.DOCUMENT_ID=meta.DOCUMENT_ID GROUP BY p.POS)");
	
		DocWordType.createOrReplaceTempView("DocWordType");
		DocWordType.cache();
		
}

	
	
	
}
