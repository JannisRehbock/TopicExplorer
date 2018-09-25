package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import cc.commandmanager.core.Context;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;



public class DocWordType {

	public static void docWordType(Context context) {
		
		SparkSession spark = (SparkSession) context.get("spark-session");	
		
		StructType schema =  new StructType(new	StructField[] {
				new StructField("DOCUMENT_ID",DataTypes.IntegerType, false, Metadata.empty()),
				new StructField("TITLE",DataTypes.StringType, false, Metadata.empty()),
				new StructField("URL_MD5",DataTypes.StringType, false, Metadata.empty()),
				new StructField("DOCUMENT_DATE",DataTypes.StringType, false, Metadata.empty()),
				new StructField("SOMETHING",DataTypes.StringType, false, Metadata.empty())
				
		});
				
				
//		Dataset<Row> DocWordTypetest = spark.sql("SELECT * FROM PosType p1 JOIN PosType p2 ON p1.POS = p2.POS");
//		DocWordTypetest.explain();
//		DocWordTypetest.show();		
				
		
		
		Dataset<Row> df = spark.read().format("csv")
			      .option("sep", ",")
			      .schema(schema)
			      .option("header", "false")
			      .load(context.getString("meta"));
		df.createOrReplaceTempView("orgTable_meta");
		df.show();
		
		Dataset<Row> orgTable = spark.sql("SELECT DOCUMENT_ID, TITLE, URL_MD5, DOCUMENT_DATE,SOMETHING "
				+ "FROM orgTable_meta"
				);
		orgTable.show();
		
//		Dataset<Row> DocWordType = spark.sql("SELECT meta.DOCUMENT_ID as DOCUMENT_ID, "
//				+ "p.POS as DOCUMENT_WORDTYPE, COUNT(*) AS TOKEN_COUNT,"
//				+ "COUNT(DISTINCT dt.TERM) as TERM_COUNT, "
//				+ "MIN(LENGTH(dt.TOKEN)) as MIN_TOKEN_LENGTH, "
//				+ "MAX(LENGTH(dt.TOKEN)) as MAX_TOKEN_LENGTH, "
//				+ "SUM(LENGTH(dt.TOKEN)) as SUM_TOKEN_LENGTH "
//				+ "FROM orgTable_meta meta, DocTerm dt, PosType subtype, PosType p "
//				+ "WHERE subtype.POS = dt.WORDTYPE_CLASS AND p.LOW <= subtype.LOW and subtype.HIGH <= p.HIGH and "
//				+ "dt.DOCUMENT_ID=meta.DOCUMENT_ID "
//				+ "GROUP BY meta.DOCUMENT_ID,p.POS");
		
		
		
		Dataset<Row> DocWordType = spark.sql("SELECT * FROM PosType p1 JOIN PosType p2 ON p1.POS = p2.POS");
	
		DocWordType.createOrReplaceTempView("DocWordType");
		DocWordType.show();
//		
		
		//Dataset<Row> Test = spark.sql("SELECT DOCUMENT_ID, TOKEN_COUNT FROM DocWordType");
		
		//Test.show();
		
}

	
	
	
}
