package cc.topicexplorer.initcorpus_spark.implementations;


import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.util.Properties;


public class testimplementation {

	public void test(SparkSession spark, Properties prop ){
		
		String filepath = prop.getProperty("spark.csvInputFile") ;
		
		
		 Dataset<Row> df = spark.read().format("csv")
			      .option("sep", ";")
			      .option("inferSchema", "true")
			      .option("header", "true")
			      .load(filepath);
		
		df.cache();
		df.createOrReplaceTempView("Data");
		 
		Dataset<Row> DocH = spark.sql("SELECT count(DISTINCT DOCUMENT_ID) AS N FROM Data");
//		DocH.show();
		DocH.cache();
		DocH.createOrReplaceTempView("DocH");
		  
		Dataset<Row> IDF = spark.sql("SELECT Term, count(DISTINCT DOCUMENT_ID) AS DocW FROM Data GROUP BY Term ORDER BY Term");
	//	IDF.show();
		IDF.cache();
		IDF.createOrReplaceTempView("IDF");
		  
		Dataset<Row> CJ = spark.sql("SELECT IDF.Term, IDF.DocW, DocH.N FROM IDF CROSS JOIN DocH");
	//	CJ.show();
		CJ.cache();
		CJ.createOrReplaceTempView("CJ");
		  
		 
		Dataset<Row> TFIDF = spark.sql("SELECT DISTINCT a.Term, ln(b.N/b.DocW)*c.TermAnzahl AS TFIDF, a.DOCUMENT_ID "
			  		+ "FROM Data AS a RIGHT JOIN CJ AS b ON a.Term = b.TERM JOIN TermH as c ON c.Term = a.Term "
			  		+ "ORDER BY TFIDF DESC "
			  		+ "LIMIT 1000");
		 
		TFIDF.cache();
	//	TFIDF.show();
		TFIDF.createOrReplaceTempView("TFIDF");
		 
		 
	
		Dataset<Row> NOOFDOCS = spark.sql("SELECT TERM, count(DISTINCT DOCUMENT_ID) as DocNo "
		 		+ "FROM Data "
		 		+ "GROUP BY TERM "
		 		+ "ORDER BY DocNo DESC");		 
	//	NOOFDOCS.show();
		NOOFDOCS.cache();
		NOOFDOCS.createOrReplaceTempView("NOOFDOCS");
		
		 
		 
		Dataset<Row> NOOFCOMMONDOCS = spark.sql("SELECT a.TERM as Term1, b.TERM as Term2, count(DISTINCT a.DOCUMENT_ID) as NoOfCoDocs "
		 		+ "FROM TFIDF AS a JOIN Data AS b ON a.DOCUMENT_ID = b.DOCUMENT_ID "
		 		+ "WHERE a.TERM != b.TERM "
		 		+ "GROUP BY a.TERM, b.TERM "
		 		+ "ORDER BY NoOfCoDocs DESC");
	//	NOOFCOMMONDOCS.show();
		NOOFCOMMONDOCS.cache();
		NOOFCOMMONDOCS.createOrReplaceTempView("NOOFCOMMONDOCS");
		 
				 
		 
		Dataset<Row> Jaccard = spark.sql("SELECT TFIDF.TERM AS TOPTERME, Data.TERM AS TERME,"
			  		+ "NOOFCOMMONDOCS.NoOfCoDocs/(a.DocNo + b.DocNo - NOOFCOMMONDOCS.NoOfCoDocs) AS Similarity "
			  		+ "FROM TFIDF JOIN Data "
			  		+ "ON TFIDF.DOCUMENT_ID = Data.DOCUMENT_ID "
					+ "JOIN NOOFCOMMONDOCS "
					+ "ON NOOFCOMMONDOCS.Term1 = TFIDF.TERM "
					+ "AND NOOFCOMMONDOCS.Term2 = Data.Term "
					+ "JOIN NOOFDOCS as a "
					+ "ON a.Term = NOOFCOMMONDOCS.Term1 "
					+ "JOIN NOOFDOCS as b "
					+ "ON b.Term = NOOFCOMMONDOCS.Term2 "
			  		+ "ORDER BY Similarity DESC"
					);
			 
		Jaccard.show();
		Jaccard.cache();
		Jaccard.createOrReplaceTempView("Jaccard");
	
		
			 
		Dataset<Row> Ranking = spark.sql(
					 "SELECT TOPTERME, TERME, Similarity, "
					 + "RANK() OVER (PARTITION BY TOPTERME ORDER BY Similarity DESC) as Rank "
					 + "FROM Jaccard "
					 + "GROUP BY TOPTERME, Terme, Similarity "
					 + "ORDER BY TOPTERME"
					 );
	//	Ranking.show();
		Ranking.cache();
		Ranking.createOrReplaceTempView("Ranking");
			 
		 
		Dataset<Row> Top = spark.sql(
					 " SELECT * "
					 + "FROM Ranking "
					 + "WHERE Rank <= 10 "
					 + "ORDER BY TOPTERME, Rank");
		Top.show();
		
		
		
		 
		 
	}
	
	
	
	
	
	
	
}
