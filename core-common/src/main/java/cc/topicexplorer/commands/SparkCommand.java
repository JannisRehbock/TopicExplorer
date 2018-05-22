package cc.topicexplorer.commands;

import java.util.Properties;
import java.util.Set;

import cc.commandmanager.core.Command;
import cc.commandmanager.core.Context;
import cc.commandmanager.core.ResultState;
import cc.topicexplorer.implementation.spark.*;

import com.google.common.collect.Sets;

public class SparkCommand implements Command {

	@Override
	public ResultState execute(Context context) {
		Spark spark = new Spark(context.get("properties", Properties.class));
		context.bind("spark-session", spark);
		return ResultState.success();
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet();
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("PropertiesCommand");
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
