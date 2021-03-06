package cc.topicexplorer.plugin.link.actions.bestdocs;

import java.util.Collections;
import java.util.Set;

import cc.topicexplorer.actions.bestdocs.BestDocumentsForGivenTopic;
import cc.commandmanager.core.Context;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {

		BestDocumentsForGivenTopic bestDocAction = context.get(
				"BEST_DOC_ACTION", BestDocumentsForGivenTopic.class);

		bestDocAction.addDocumentColumn("DOCUMENT.LINK$URL",
				"LINK$URL", context.containsKey("term"));

		context.rebind("BEST_DOC_ACTION", bestDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("BestDocsCoreGenerateSQL");	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("BestDocsCoreCreate");	}

	@Override
	public Set<String> getOptionalAfterDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Set<String> getOptionalBeforeDependencies() {
		return Collections.emptySet();
	}
}
