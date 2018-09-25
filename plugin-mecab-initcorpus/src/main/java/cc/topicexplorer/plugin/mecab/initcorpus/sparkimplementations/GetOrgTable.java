package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;


import java.io.File;
import java.util.Properties;

import cc.commandmanager.core.Context;

public class GetOrgTable {

	public static void getOrgTable(Context context) {
		
		Properties prop = context.get("properties", Properties.class);
		
		File filemeta = new File(prop.getProperty("Mecab_filemeta")); 
		File filetext = new File(prop.getProperty("Mecab_filetext"));
		File fileGer = new File(prop.getProperty("Mecab_fileGer"));
		File fileJap = new File(prop.getProperty("Mecab_fileJap"));
		File fileEng = new File(prop.getProperty("Mecab_fileEng"));
		
		String[] metafile = filemeta.list();
		String[] textfile = filetext.list();
		String[] gerfile = fileGer.list();
		String[] engfile = fileEng.list();
		String[] japfile = fileJap.list();
		
		String Jap = prop.getProperty("Mecab_fileJap")+"/"+japfile[0];		
		String Ger = prop.getProperty("Mecab_fileGer")+"/"+gerfile[0];
		String Eng = prop.getProperty("Mecab_fileEng")+"/"+engfile[0];	
		String text = prop.getProperty("Mecab_filetext")+"/"+textfile[0];	
		String meta = prop.getProperty("Mecab_filemeta")+"/"+metafile[0];	
	
		System.out.println(Jap);
		System.out.println(Eng);
		System.out.println(Ger);
		System.out.println(text);
		System.out.println(meta);
		
		
		
		context.bind("Jap", Jap);
		context.bind("Ger", Ger);
		context.bind("Eng", Eng);
		
		if(textfile.length == 1 && metafile.length == 1) {
			context.bind("meta", meta);
			context.bind("text", text);
		}
		
		
	}
	
	
	
}
