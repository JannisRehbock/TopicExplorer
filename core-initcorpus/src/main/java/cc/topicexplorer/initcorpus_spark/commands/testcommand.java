package cc.topicexplorer.initcorpus_spark.commands;


import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Sets;

import org.apache.spark.sql.SparkSession;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.initcorpus_spark.implementations.testimplementation;




public class testcommand implements Command {

	
	public ResultState execute(Context context) {
		
		SparkSession spark = context.get("spark-session", SparkSession.class );
		Properties prop = context.get("properties", Properties.class);
		
		testimplementation testimp= new testimplementation();
		testimp.test(spark, prop);

		return ResultState.success();
	}
	
	
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
