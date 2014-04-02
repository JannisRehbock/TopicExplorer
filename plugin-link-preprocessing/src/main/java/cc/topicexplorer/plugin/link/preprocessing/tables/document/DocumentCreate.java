package cc.topicexplorer.plugin.link.preprocessing.tables.document;

import java.sql.SQLException;

import cc.topicexplorer.commands.TableCreateCommand;

public class DocumentCreate extends TableCreateCommand {

	@Override
	public void createTable() {
		try {
			database.executeUpdateQuery("ALTER IGNORE TABLE `" + this.tableName
					+ "` ADD COLUMN LINK$URL VARCHAR(500) COLLATE UTF8_BIN,"
					+ " ADD COLUMN LINK$IN_DEGREE INT(11) DEFAULT 1;");
		} catch (SQLException e) {
			logger.error("Columns LINK$URL, LINK$IN_DEGREE could not be added to table " + this.tableName);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void dropTable() {
		try {
			this.database.executeUpdateQuery("ALTER TABLE " + this.tableName + " DROP COLUMN LINK$URL"
					+ ", DROP COLUMN LINK$IN_DEGREE");
		} catch (SQLException e) {
			if (e.getErrorCode() != 1091) { // MySQL Error code for: 'Can't
											// DROP..; check that column/key
											// exists
				logger.error("Document.dropColumns: Cannot drop column.");
				throw new RuntimeException(e);
			} else {
				logger.info("dropColumns: ignored SQL-Exception with error code 1091.");
			}
		}
	}

	@Override
	public void setTableName() {
		tableName = "DOCUMENT";
	}

	@Override
	public void addDependencies() {
		beforeDependencies.add("DocumentCreate");
	}
}