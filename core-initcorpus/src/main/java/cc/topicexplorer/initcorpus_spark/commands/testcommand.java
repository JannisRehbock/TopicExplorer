package cc.topicexplorer.initcorpus_spark.commands;


import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.initcorpus_spark.implementations.testimplementation;




public class testcommand implements Command {

	
	public ResultState execute(Context context) {
		
		
		testimplementation test= new testimplementation(context.get("spark-session"), context.get("properties")  );
		

		return ResultState.success();
	}
	
	
	static
	
	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("SparkCommand");
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
