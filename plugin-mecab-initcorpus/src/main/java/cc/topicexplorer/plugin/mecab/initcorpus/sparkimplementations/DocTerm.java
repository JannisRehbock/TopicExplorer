package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;

import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import com.csvreader.CsvReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import cc.commandmanager.core.Context;
import cc.topicexplorer.plugin.mecab.initcorpus.command.DocumentTermFill;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.postagger.JPOSMeCab;
import cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger.PreparationWithTreeTagger;

public class DocTerm {
	
	
	private static final Logger logger = Logger
			.getLogger(DocumentTermFill.class);
	

public static void docTerm(Context context) {
	
	SparkSession spark = (SparkSession) context.get("spark-session");	
	
	HashMap<String,Integer> tag2id = new HashMap<String,Integer>();	
	
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
			
				CsvReader reader = new CsvReader(new FileReader(context.getString("Eng")));
				reader.readHeaders();
				int pos = reader.getIndex("POS");
				int description = reader.getIndex("DESCRIPTION");
				while (reader.readRecord()) {
					tag2id.put(reader.get(description),Integer.parseInt(reader.get(pos)));
				}
				
				/*File filePosRs = new File(context.getString("Eng")); 
				List<String> lines = Files.readAllLines(filePosRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : lines) { 
				   String[] array = line.split(";");
				   System.out.println(array[3] + " + " + array[0]);
				   tag2id.put(array[3],Integer.parseInt(array[0]));
				}*/
				
			}
			else if("/german-utf8.par".equals(treeTaggerModel)) {
				
				CsvReader reader = new CsvReader(new FileReader(context.getString("Ger")));
				reader.readHeaders();
				int pos = reader.getIndex("POS");
				int description = reader.getIndex("DESCRIPTION");
				while (reader.readRecord()) {
					System.out.println(reader.get(description)+"   " +reader.get(pos));
					tag2id.put(reader.get(description),Integer.parseInt(reader.get(pos)));
				}
				
				/*
				File filePosRs = new File(context.getString("Ger")); 
				List<String> lines = Files.readAllLines(filePosRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : lines) { 
				   String[] array = line.split(";");
				   System.out.println(array[3] + " + " + array[0]);
				   tag2id.put(array[3],Integer.parseInt(array[0]));
				}*/
				
				
				
				
				
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
				
				
				
				CsvReader reader = new CsvReader(new FileReader(context.getString("text")));
				while (reader.readRecord()) {
					csvList = jpos.parseString(Integer.parseInt(reader.get(0)),reader.get(1), logger);
				}
				
				/*
				File fileTextRs = new File(context.getString("text")); 
				List<String> textlines = Files.readAllLines(fileTextRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : textlines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   csvList = jpos.parseString(Integer.parseInt(array[0]),array[1], logger); 
				}*/
			} else if ("treetagger".equals(textAnalyzer)) {
				
				
				CsvReader reader = new CsvReader(new FileReader(context.getString("text")));
				while (reader.readRecord()) {
					csvList = treeTaggerAnalyzer.parse(Integer.parseInt(reader.get(0)),reader.get(1));
				}
				/*			
				File fileTextRs = new File(context.getString("text")); 
				List<String> textlines = Files.readAllLines(fileTextRs.toPath(), StandardCharsets.UTF_8); 
				for (String line : textlines) { 
				   String[] array = line.split(";");
				   System.out.println(array[1] + " + " + array[0]);
				   csvList = treeTaggerAnalyzer.parse(Integer.parseInt(array[0]),array[1]);
				}*/
			}
			docTermCSVWriter.write("DOCUMENT_ID,POSITION_OF_TOKEN_IN_DOCUMENT,TERM,TOKEN,WORDTYPE_CLASS,CONTINUATION" + "\n");
			for (String csvEntry : csvList) {
				System.out.println(csvEntry);
				docTermCSVWriter.write(csvEntry + ",0" + "\n");
			}
		
		docTermCSVWriter.flush();
		docTermCSVWriter.close();

		
		Dataset<Row> df = spark.read().format("csv")
			      .option("sep", ",")
			      .option("inferSchema", "true")
			      .option("header", "true")
			      .load("temp/docTerm.sql.csv");
		df.createOrReplaceTempView("DocTerm");
	
	
	
	
	}
	catch(IOException e) {
		
	}
}
}

