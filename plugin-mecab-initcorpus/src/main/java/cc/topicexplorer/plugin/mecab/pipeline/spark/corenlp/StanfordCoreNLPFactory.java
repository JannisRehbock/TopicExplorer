package cc.topicexplorer.plugin.mecab.pipeline.spark.corenlp;

import java.util.Properties;

import cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger.PreparationWithTreeTagger;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLPFactory
extends BasePooledObjectFactory<PreparationWithTreeTagger> {

	@Override
	public PreparationWithTreeTagger create() throws Exception {
		 // set up pipeline properties
	    Properties props = new Properties();
	    
	    // set the list of annotators to run
	    
	    // expensive
	    // props.setProperty("annotators", "tokenize,ssplit,pos,lemma,depparse");
	    
	    // very expensive
	    // props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
	    
	    // cheap and fast
	    props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
	    props.setProperty("threads", "1");
	    // build pipeline
	    return new PreparationWithTreeTagger(props);
	}

	@Override
	public PooledObject<PreparationWithTreeTagger> wrap(PreparationWithTreeTagger pipeline) {
		return new DefaultPooledObject<PreparationWithTreeTagger>(pipeline);
	}
}