package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

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
					      .option("sep", ",")
					      .option("inferSchema", "true")
					      .option("header", "true")
					      .load(context.getString("Jap"));
				df.createOrReplaceTempView("PosType");
				
			} else if ("treetagger".equals(textAnalyzer)) {
				
				String treeTaggerModel = properties.getProperty("Mecab_treetagger-model").trim();
				
				if ("/german-utf8.par".equals(treeTaggerModel)) {
					
					StructType schema =  new StructType(new	StructField[] {
							new StructField("POS",DataTypes.IntegerType, false, Metadata.empty()),
							new StructField("HIGH",DataTypes.IntegerType, false, Metadata.empty()),
							new StructField("LOW",DataTypes.IntegerType, false, Metadata.empty()),
							new StructField("DESCRIPTION",DataTypes.StringType, false, Metadata.empty()),
							new StructField("LONG_DESCRIPTION",DataTypes.StringType, false, Metadata.empty()),
							new StructField("PARENT_POS",DataTypes.IntegerType, false, Metadata.empty())
							
					});
					
					
					Dataset<Row> df = spark.read().format("csv")
						      .option("sep", ",")
						      .schema(schema)
						      .option("mode", "FAILFAST")
						      .option("header", "true")
						      .load(context.getString("Ger"));
					df.createOrReplaceTempView("PosType");					
					df.cache();
					
					
//					Dataset<Row> Test = spark.read().format("csv")
//						      .option("sep", ",")
//						      .schema(schema)
//						      .option("header", "true")
//						      .load(context.getString("Ger2"));
//					Test.createOrReplaceTempView("PosTypeTest");					
//					
					Dataset<Row> DocWordType = spark.sql("SELECT * FROM PosType p1 JOIN PosType p2 ON p1.POS = p2.POS");
					
					DocWordType.show();
					
					//Dataset<Row> selfjoin = df.as("a").join(df.as("b"), "POS");
					//selfjoin.show();
					
					
					
					
					
					
					
//					Dataset<Row> Test2 = spark.sql("SELECT POS, HIGH, LOW ,DESCRIPTION, LONG_DESCRIPTION FROM PosType");
//					Test2.show(1000);
//					Test2.createOrReplaceTempView("PosTypeTest2");
					
				} else if ("/english-utf8.par".equals(treeTaggerModel)) {

					Dataset<Row> df = spark.read().format("csv")
						      .option("sep", ",")
						      .option("inferSchema", "true")
						      .option("header", "true")
						      .load(context.getString("Eng"));
					df.createOrReplaceTempView("PosType");
				}
				
				
			}
			
			
	}
		
}
		
		
	
	
	

