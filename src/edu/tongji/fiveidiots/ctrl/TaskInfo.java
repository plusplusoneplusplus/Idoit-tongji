package edu.tongji.fiveidiots.ctrl;

import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.tongji.fiveidiots.ctrl.DBHelper.TimeAreaSelector;

/**
 * @author luyiguang
 * @hint 下面的代码中，变量或函数两边有下划线的意思是返回的String两边都回加空格
 */
public class TaskInfo {
	public final static String TABLE_NAME = "TASK_INFO";	
	public final static String _TABLE_NAME_ = " TASK_INFO ";
	public final static String TABLE_CONTENT_ARRAY [] = {
		"id", "INTEGER PRIMARY KEY",
		"name", "NTEXT",
		"hint", "NTEXT",
		"begin_time", "INTEGER",
		"finis_time", "INTEGER",
		"priority", "INTEGER",
		"prev_task_id", "INTEGER",
		"next_task_id", "INTEGER",
		"finishing_state", "INTEGER",
		"finishing_place", "NTEXT",
		"minutes_done", "INTEGER",
		"minutes_todo", "INTEGER",
		"alarm_time_ahead", "INTEGER",
		"repeat_way", "INTEGER"
	};
	
	private long id;
	private String name;
	private String hint;
	
	private Date begTime = new Date();
	private Date dueTime = new Date();
	
	public enum PRIORITY_TYPE {
		LOW, MID, HIGH
	}
	private PRIORITY_TYPE priority = PRIORITY_TYPE.LOW;
	
	private long prevTaskId;
	private long nextTaskId;
	
	public enum TASK_STATE {
		WORKING, DELAYED, FINISHED, DELETED
	}
	private TASK_STATE state = TASK_STATE.WORKING;
	
	private int minutesDone = 0;
	private int minutesTodo = 50;
	
	private int alarmTimeAhead = 60;
	
	private long repeatWay = 0;
	
	/** 
	 * where do we finish the task
	 */
	private String where;
		
	/**
	 * Get TaskInfo db table content value WITHOUT table name
	 * @return tables to string
	 */
	public String get_DBTableContentValueString_() {
		String table_value[] = new String [] {
				Long.toString(id),
				name,
				hint,
				Long.toString(begTime.getTime()),
				Long.toString(dueTime.getTime()),
				Integer.toString(priority.ordinal()),
				Long.toString(prevTaskId),
				Long.toString(nextTaskId),
				Integer.toString(state.ordinal()),
				where,
				Integer.toString(minutesDone),
				Integer.toString(minutesTodo),
				Integer.toString(alarmTimeAhead),
				Long.toString(repeatWay)
		};
		
		String value = " values(";
		for (int i = 0; i < table_value.length; i++) {
			value += table_value[i];
			if (i + 1 < table_value.length) {
				value += ",";
			}
		}
		value += ") ";
		return value;
	}
	
	/**
	 * Generate a TaskInfo from db cursor
	 * @param dbCursor 
	 */
	public TaskInfo(Cursor dbCursor) {
		id = dbCursor.getLong(0);
		name = dbCursor.getString(1);
		hint = dbCursor.getString(2);
		
		begTime.setTime(dbCursor.getInt(3));
		dueTime.setTime(dbCursor.getInt(4));
		
		priority = PRIORITY_TYPE.values()[dbCursor.getInt(5)];
		
		prevTaskId = dbCursor.getLong(6);
		nextTaskId = dbCursor.getLong(7);
		
		state = TASK_STATE.values()[dbCursor.getInt(8)];
		
		where = dbCursor.getString(9);
		minutesDone = dbCursor.getInt(10);
		minutesTodo = dbCursor.getInt(11);
		alarmTimeAhead = dbCursor.getInt(12);
		repeatWay = dbCursor.getLong(13);
	}
	
	private static String[] get_TableContentNameArray() {
		String tableContentName[] = new String [TABLE_CONTENT_ARRAY.length >> 1];
		for (int i = 0; i < TABLE_CONTENT_ARRAY.length; i += 2) {
			tableContentName[i >> 1] = TABLE_CONTENT_ARRAY[i];
		}
		return tableContentName;
	}
	
	private static String get_TableContentNameString_() {
		String tableContentName = "" + TABLE_CONTENT_ARRAY[0];
		
		for (int i = 2; i < TABLE_CONTENT_ARRAY.length; i += 2) {
			tableContentName += "," + TABLE_CONTENT_ARRAY[i];
		}
		
		return tableContentName;
	}

	public void insert(SQLiteDatabase db) {
		db.execSQL("INSERT INTO" + _TABLE_NAME_ + get_TableContentNameString_() + get_DBTableContentValueString_());
	}
	
	public void delete(SQLiteDatabase db) {
		db.execSQL("DELETE FROM" + _TABLE_NAME_ + "where id = " + id);
	}
	
	public static void createTable(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE" + _TABLE_NAME_ + get_TableContentNameString_());
	}
	
	public static void dropTable(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + _TABLE_NAME_);
	}
	
	private static Cursor select(SQLiteDatabase db, String condition) {
		return db.query(TABLE_NAME, get_TableContentNameArray(), condition, null, null, null, null);
	}
	
	public static Cursor select(SQLiteDatabase db, TimeAreaSelector selector) {
		String condition = "";
		condition += "begin_time < " + selector.getFinisTime() + " AND begin_time > " + selector.getStartTime() + " ";
		if ((selector.flag & DBHelper.INCLUDE_DELETED) == 0) {
			condition += " AND finishing_state <> " + TASK_STATE.DELETED.ordinal();
		}
		if ((selector.flag & DBHelper.INCLUDE_FINISHED) == 0) {
			condition += " AND finishing_state <> " + TASK_STATE.FINISHED.ordinal();
		}
		
		return select(db, condition);
	}
}