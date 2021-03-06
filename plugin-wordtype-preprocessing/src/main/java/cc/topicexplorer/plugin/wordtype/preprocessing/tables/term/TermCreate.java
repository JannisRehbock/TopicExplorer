package cc.topicexplorer.plugin.wordtype.preprocessing.tables.term;

import java.sql.SQLException;
import java.util.Set;

import org.apache.log4j.Logger;

import cc.topicexplorer.commands.TableCreateCommand;

import com.google.common.collect.Sets;

public class TermCreate extends TableCreateCommand {

	private static final Logger logger = Logger.getLogger(TermCreate.class);

	@Override
	public void createTable() {
		try {
			this.database.executeUpdateQuery("ALTER IGNORE TABLE " + this.tableName
					+ " ADD COLUMN WORDTYPE$WORDTYPE VARCHAR(100) ");
		} catch (SQLException e) {
			logger.error("Column TEXT$WORD_TYPE could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN WORDTYPE$WORDTYPE");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't //
											// DROP..; check that column/key //
											// exists
				logger.error("Term.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "TERM";
	}

	@Override
	public Set<String> getAfterDependencies() {
		return Sets.newHashSet("WordType_TermFill");
	}

	@Override
	public Set<String> getBeforeDependencies() {
		return Sets.newHashSet("TermCreate");
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
