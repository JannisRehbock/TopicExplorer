package cc.topicexplorer.plugin.frame.actions.getframes;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cc.topicexplorer.database.Database;
import cc.topicexplorer.database.SelectMap;

public class Frames {
	private SelectMap frameMap;
	private PrintWriter outWriter;
	private Database database;
	
	public Frames(Database database, PrintWriter pw) {
		frameMap = new SelectMap();
		frameMap.select.add("FRAME");
		frameMap.select.add("COUNT( DISTINCT DOCUMENT_ID ) AS FRAME_COUNT");
		frameMap.select.add("FRAMES.FRAME_ID");
		frameMap.from.add("FRAMES");
		frameMap.groupBy.add("FRAMES.FRAME");
		frameMap.orderBy.add("FRAMES.TOPIC_ID");
		frameMap.orderBy.add("FRAME_COUNT DESC");
		frameMap.limit = 20;
		
		this.setDatabase(database);
		this.setServletWriter(pw);
	}
	
	public SelectMap getFrameMap() {
		return this.frameMap;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	public void setServletWriter(PrintWriter servletWriter) {
		this.outWriter = servletWriter;
	}

	public void addFrameColumn(String frameColumn, String frameColumnName) {
		frameMap.select.add(frameColumn + " as " +frameColumnName);
	}
	
	public void getFrames() {
		ArrayList<String> frameColumnList = frameMap.getCleanColumnNames();
	//	frameColumnList.remove("")
		JSONArray topFrames = new JSONArray();
	
		JSONObject topic = new JSONObject();
		JSONObject topics = new JSONObject();
		JSONObject topFrame = new JSONObject();
		JSONObject frame = new JSONObject();
		JSONObject frames = new JSONObject();
		JSONObject all = new JSONObject();
		
		try {
			ResultSet topicQueryRS = database.executeQuery("SELECT TOPIC_ID,HIERARCHICAL_TOPIC$START FROM TOPIC WHERE HIERARCHICAL_TOPIC$START=HIERARCHICAL_TOPIC$END");
			while (topicQueryRS.next()) {
				SelectMap tempMap = frameMap.clone();
				tempMap.where.add("FRAMES.TOPIC_ID=" + topicQueryRS.getInt("TOPIC_ID"));
				ResultSet frameQueryRS = database.executeQuery(tempMap.getSQLString());
				while (frameQueryRS.next()) {
					topFrame.put("FrameId", frameQueryRS.getString("FRAME_ID"));
					topFrame.put("FrameCount", frameQueryRS.getString("FRAME_COUNT"));
					topFrames.add(topFrame);
					topic.clear();
					for (int i = 0; i < frameColumnList.size(); i++) {
						frame.put(frameColumnList.get(i), frameQueryRS.getString(frameColumnList.get(i)));
					}
					frames.put(frameQueryRS.getString("FRAME_ID"), frame);
					frame.clear();
				}
				topic.put("TopFrames", topFrames);
				topics.put(topicQueryRS.getString("HIERARCHICAL_TOPIC$START"), topic);
				
			}
			all.put("TOPIC", topics);
			all.put("FRAME", frames);
			
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		outWriter.print(all.toString());
	}
}