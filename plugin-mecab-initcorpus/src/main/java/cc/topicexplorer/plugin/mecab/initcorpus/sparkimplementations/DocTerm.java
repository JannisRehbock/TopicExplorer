package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;
//import org.apache.spark.api.java.function.Function;
//import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
//import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
//import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
	
	//***************************************
	//Wo wird orgTable_text / orgTable_meta eingelesen/ gespeichert??
	//Kann direkt aus der CSV eingelesen werden weiter unten je nachdem wo es herkommt.
	//***************************************
	
	Dataset<Row> textRs = spark.sql("SELECT DOCUMENT_ID, DOCUMENT_TEXT FROM orgTable_text");
	textRs.createOrReplaceTempView("textRs");
	
	// Fill tag2id directly from .csv File
	
	textRs.write().format("csv")
	.option("sep", ";")
    .option("inferSchema", "true")
    .option("header", "true")
    .save("temp/textRs.csv");
	
	
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
			String treeTaggerModel = properties.getProperty("Mecab_treetagger-model").trim();
			if("/english-utf8.par".equals(treeTaggerModel)) {
			
				File filePosRs = new File("resources/mecabEng.csv"); 
				List<String> lines = Files.readAllLines(filePosRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : lines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   tag2id.put(array[1],Integer.parseInt(array[0]));
				}
			}
			else if("/german-utf8.par".equals(treeTaggerModel)) {
				File filePosRs = new File("resources/mecabGer.csv"); 
				List<String> lines = Files.readAllLines(filePosRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : lines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   tag2id.put(array[1],Integer.parseInt(array[0]));
				}
				
				
				
				
				
			}
			treeTaggerAnalyzer = new PreparationWithTreeTagger(',',
					properties.getProperty("Mecab_treetagger-path").trim(), properties.getProperty("Mecab_treetagger-model").trim(), tag2id);

			treeTaggerAnalyzer.setLogger(org.apache.log4j.Logger.getRootLogger());
			
		}
		BufferedWriter docTermCSVWriter = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(fileName, true), "UTF-8"));

			List<String> csvList = null;
			if ("mecab".equals(textAnalyzer)) {
				File fileTextRs = new File("temp/textRs.csv"); 
				List<String> textlines = Files.readAllLines(fileTextRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : textlines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   csvList = jpos.parseString(Integer.parseInt(array[0]),array[1], logger); 
				}
			} else if ("treetagger".equals(textAnalyzer)) {
				
				File fileTextRs = new File("temp/textRs.csv"); 
				List<String> textlines = Files.readAllLines(fileTextRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : textlines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   csvList = treeTaggerAnalyzer.parse(Integer.parseInt(array[0]),array[1]);
				}
			}
			for (String csvEntry : csvList) {
				docTermCSVWriter.write(csvEntry + "\n");
			}
		
		docTermCSVWriter.flush();
		docTermCSVWriter.close();

		
		Dataset<Row> df = spark.read().format("csv")
			      .option("sep", ";")
			      .option("inferSchema", "true")
			      .option("header", "true")
			      .load("temp/docTerm.sql.csv");
		df.createOrReplaceTempView("DocTerm");
	
	
	
	
	}
	catch(IOException e) {
		
	}
}
}

