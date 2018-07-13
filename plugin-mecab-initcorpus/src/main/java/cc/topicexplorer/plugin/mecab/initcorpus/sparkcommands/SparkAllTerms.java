package cc.topicexplorer.plugin.mecab.initcorpus.sparkcommands;


import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.plugin.mecab.initcorpus.sparkimplementations.AllTerms;


public class SparkAllTerms implements Command {
	private static final Logger logger = Logger.getLogger(AllTerms.class);
	
	public ResultState execute(Context context) {
		
		
		AllTerms.allTerms(context);
		
		return ResultState.success();
	}
	
	
	
	
	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("SparkPosType_old","SparkDocTerm");
	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Sets.newHashSet();
	}
	
	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Sets.newHashSet();
	}
	
}