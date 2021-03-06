package cc.topicexplorer.plugin.text.actions.showdoc;

import java.util.Set;

import cc.commandmanager.core.Context;
import cc.topicexplorer.actions.showdoc.ShowDoc;
import cc.topicexplorer.commands.TableSelectCommand;

import com.google.common.collect.Sets;

public class Collect extends TableSelectCommand {

	@Override
	public void tableExecute(Context context) {
		ShowDoc showDocAction = context.get("SHOW_DOC_ACTION", ShowDoc.class);
		
		showDocAction.addDocumentColumn("DOCUMENT.TEXT$TITLE", "TEXT$TITLE");
		showDocAction.addDocumentColumn("DOCUMENT.TEXT$FULLTEXT", "TEXT$FULLTEXT");
		if (properties.getProperty("plugins").contains("fulltext")) {
			showDocAction.addDocumentColumn("CONCAT(' ', DOCUMENT.FULLTEXT$FULLTEXT, ' ')",  "SEARCH_TEXT");
		} else {
			showDocAction.addDocumentColumn("CONCAT(' ', DOCUMENT.TEXT$FULLTEXT, ' ')", "SEARCH_TEXT");
		}
		context.rebind("SHOW_DOC_ACTION", showDocAction);
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("ShowDocCoreGenerateSQL");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("ShowDocCoreCreate");
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
