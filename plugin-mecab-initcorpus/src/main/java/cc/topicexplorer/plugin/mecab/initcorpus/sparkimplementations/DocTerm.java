package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
//import org.spark_project.jetty.servlet.ServletContextHandler.Context;
import cc.topicexplorer.plugin.mecab.initcorpus.command.DocumentTermFill;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger.JPOSMeCab;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger.PreparationWithTreeTagger;

public class DocTerm {
	
	
	private static final Logger logger = Logger
			.getLogger(DocumentTermFill.class);
	

public static void docTerm(Context context) {
	
	SparkSession spark = (SparkSession) context.get("spark-session");	
	
	HashMap<String,Integer> tag2id = new HashMap<String,Integer>();
	final ArrayList<String> description = new ArrayList<String>();
	final ArrayList<Integer> pos = new ArrayList<Integer>();
	
	Dataset<Row> posRs = spark.sql("SELECT POS,DESCRIPTION FROM PosType");
	posRs.createOrReplaceTempView("posRs");
	
	Dataset<Row> textRs = spark.sql("SELECT DOCUMENT_ID, DOCUMENT_TEXT FROM orgTable_text");
	posRs.createOrReplaceTempView("textRs");
	
	/*pos.add(posRs.select("POS").rdd().map(
		new Function <Integer, Integer>(){
			public Integer call(Integer i) {
				return i;
			}
		}, Encoders.INT()));
	*/
	
	/*posRs.select("POS").rdd().map(
		new Function <Integer, Integer>(){
			public Integer call(Integer i) {
				pos.add(i);
				return i;
			}
		}, Encoders.INT()));
	*/
	
	
	
	posRs.select("DESCRIPTION").rdd().map(new Function <String,String>(){
		
		public String call(String i) {
			description.add(i);
			return i;
		}
	}, Encoders.STRING());
	
	
	
	if(pos.size()==description.size()) {
		for(int i=0;i<pos.size();i++) {
			tag2id.put(description.get(i),pos.get(i));	
		}
	}else {
		logger.error("uneven length in arrays");
	}
	
	
	
	
	
	Properties properties = (Properties) context.get("properties");
	String textAnalyzer = properties.getProperty("Mecab_text-analyzer").trim();

	String fileName = "temp/docTerm.sql.csv";
	File fileTemp = new File(fileName);
	if (fileTemp.exists()) {
		fileTemp.delete();
	}
	try {
		JPOSMeCab jpos = null;
		PreparationWithTreeTagger treeTaggerAnalyzer = null;
		if ("mecab".equals(textAnalyzer)) {
			if (!properties.containsKey("Mecab_LibraryPath")) {
				logger.error("Mecab library path not set. Did you enable mecab plugin in config.properties?");
				throw new RuntimeException("Mecab library path not set.");
			}
			jpos = new JPOSMeCab(properties
					.getProperty("Mecab_LibraryPath").trim(), logger);
		} else if ("treetagger".equals(textAnalyzer)) {
			if (!properties.containsKey("Mecab_treetagger-path")
					|| !properties.containsKey("Mecab_treetagger-model")) {
				logger.error("TreeTagger path or model not set.");
				throw new RuntimeException(
						"TreeTagger path or model not set.");
			}

			HashMap<String,Integer> tag2id = new HashMap<String,Integer>();
			Statement posType_stmt = database.getConnection().createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			posType_stmt.setFetchSize(Integer.MIN_VALUE);

			ResultSet posRs = posType_stmt
					.executeQuery("SELECT POS,DESCRIPTION FROM POS_TYPE;");
			while (posRs.next()) {
				tag2id.put(posRs.getString("DESCRIPTION"), posRs.getInt("POS"));
			}
			posRs.close();
			posType_stmt.close();

			treeTaggerAnalyzer = new PreparationWithTreeTagger(',',
					properties.getProperty("Mecab_treetagger-path").trim(), properties.getProperty("Mecab_treetagger-model").trim(), tag2id);

			treeTaggerAnalyzer.setLogger(org.apache.log4j.Logger.getRootLogger());
			
		}
		BufferedWriter docTermCSVWriter = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(fileName, true), "UTF-8"));

		Statement stmt = database.getConnection().createStatement(
				java.sql.ResultSet.TYPE_FORWARD_ONLY,
				java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);

		ResultSet textRs = stmt
				.executeQuery("SELECT DOCUMENT_ID, DOCUMENT_TEXT FROM orgTable_text");
		while (textRs.next()) {
			List<String> csvList = null;
			if ("mecab".equals(textAnalyzer)) {
				csvList = jpos.parseString(textRs.getInt("DOCUMENT_ID"),
						textRs.getString("DOCUMENT_TEXT"), logger);
			} else if ("treetagger".equals(textAnalyzer)) {
				csvList = treeTaggerAnalyzer.parse(textRs.getInt("DOCUMENT_ID"),
						textRs.getString("DOCUMENT_TEXT"));
			}
			for (String csvEntry : csvList) {
				docTermCSVWriter.write(csvEntry + "\n");
			}
		}
		docTermCSVWriter.flush();
		docTermCSVWriter.close();

	/*	database.executeUpdateQuery("LOAD DATA LOCAL INFILE '"
				+ fileName
				+ "' IGNORE INTO TABLE "
				+ tableName
				+ " CHARACTER SET utf8 FIELDS TERMINATED BY ',' ENCLOSED BY '\"' (`DOCUMENT_ID`, "
				+ "`POSITION_OF_TOKEN_IN_DOCUMENT`, `TERM`, `TOKEN`, `WORDTYPE_CLASS`, `CONTINUATION`);");
		stmt.close();

		database.executeUpdateQuery("CREATE INDEX TERM_WORDCLASS ON DOCUMENT_TERM(TERM,WORDTYPE_CLASS,DOCUMENT_ID)");
	*/
	
	
	
	
}
}
}

