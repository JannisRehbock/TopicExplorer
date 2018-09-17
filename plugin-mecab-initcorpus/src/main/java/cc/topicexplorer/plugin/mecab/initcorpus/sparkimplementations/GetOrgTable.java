package cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations;


import java.io.File;
import java.util.Properties;

import cc.commandmanager.core.Context;

public class GetOrgTable {

	public static void getOrgTable(Context context) {
		
		Properties prop = context.get("properties", Properties.class);
		
		File filemeta = new File(prop.getProperty("filemeta")); 
		File filetext = new File(prop.getProperty("filetext"));
		String[] metafile = filemeta.list();
		String[] textfile = filetext.list();
		if(textfile.length == 1 && metafile.length == 1) {
			context.bind("meta", metafile[0]);
			context.bind("text", textfile[0]);
		}
		
		
	}
	
	
	
}
