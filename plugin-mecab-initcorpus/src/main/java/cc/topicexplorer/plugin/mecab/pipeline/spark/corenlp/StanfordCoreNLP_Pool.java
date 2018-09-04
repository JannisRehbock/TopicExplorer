package cc.topicexplorer.plugin.mecab.pipeline.spark.corenlp;

import cc.topicexplorer.plugin.mecab.initcorpus.implementation.treetagger.PreparationWithTreeTagger;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreNLP_Pool extends GenericObjectPool<PreparationWithTreeTagger>{

	public StanfordCoreNLP_Pool(PooledObjectFactory<PreparationWithTreeTagger> factory, GenericObjectPoolConfig config) {
		super(factory, config);
	}
}