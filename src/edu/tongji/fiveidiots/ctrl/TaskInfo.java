package edu.tongji.fiveidiots.ctrl;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.tongji.fiveidiots.ctrl.DBHelper.TimeAreaSelector;

class TaskInfoBase {
	
	protected long id;
	protected String name;
	protected String hint;
	
	protected Date begTime = new Date();
	protected Date dueTime = new Date();
	
	public enum PRIORITY_TYPE {
		LOW, MID, HIGH
	}
	protected PRIORITY_TYPE priority = PRIORITY_TYPE.LOW;
	
	protected long prevTaskId;
	protected long nextTaskId;
	
	public enum TASK_STATE {
		WORKING, DELAYED, FINISHED, DELETED
	}
	protected TASK_STATE state = TASK_STATE.WORKING;
	
	protected int minutesDone = 0;
	protected int minutesTodo = 50;
	
	protected int alarmTimeAhead = 60;
	
	protected long repeatWay = 0;
	
	/** 
	 * where do we finish the task
	 */
	protected String address;
}

class TaskInfoWithDBBasic extends TaskInfoBase {
	
	public final static String TABLE_NAME = "TASK_INFO";	
	public final static String _TABLE_NAME_ = " TASK_INFO ";
	public final static String TABLE_CONTENT_MAP [][] = {
		{ "id"					, "INTEGER PRIMARY KEY", },
		{ "name"				, "NTEXT"  , },
		{ "hint"				, "NTEXT"  , },
		{ "begin_time"			, "INTEGER", },
		{ "finis_time"			, "INTEGER", },
		{ "priority"			, "INTEGER", },
		{ "prev_task_id"		, "INTEGER", },
		{ "next_task_id"		, "INTEGER", },
		{ "finishing_state"		, "INTEGER", },
		{ "finishing_place"		, "NTEXT"  , },
		{ "minutes_done"		, "INTEGER", },
		{ "minutes_todo"		, "INTEGER", },
		{ "alarm_time_ahead"	, "INTEGER", },
		{ "repeat_way"			, "INTEGER", },
	};
	
	protected static String[] get_TableContentNameArray() {
		String tableContentName[] = new String [TABLE_CONTENT_MAP.length];
		for (int i = 0; i < TABLE_CONTENT_MAP.length; i ++) {
			tableContentName[1] = TABLE_CONTENT_MAP[i][0];
		}
		return tableContentName;
	}
	
	protected static String get_TableContentNameString_() {
		String tableContentName = "" + TABLE_CONTENT_MAP[0][0];
		
		for (int i = 2; i < TABLE_CONTENT_MAP.length; i ++) {
			tableContentName += "," + TABLE_CONTENT_MAP[i][0];
		}
		
		return tableContentName;
	}	
	
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
				address,
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
}

class TaskInfoWithDBComplex extends TaskInfoWithDBBasic {
	
	public TaskInfoWithDBComplex() {}

	/**
	 * Generate a TaskInfo from db cursor
	 * @param dbCursor 
	 */
	public TaskInfoWithDBComplex(Cursor dbCursor) {
		id = dbCursor.getLong(0);
		name = dbCursor.getString(1);
		hint = dbCursor.getString(2);
		
		begTime.setTime(dbCursor.getInt(3));
		dueTime.setTime(dbCursor.getInt(4));
		
		priority = PRIORITY_TYPE.values()[dbCursor.getInt(5)];
		
		prevTaskId = dbCursor.getLong(6);
		nextTaskId = dbCursor.getLong(7);
		
		state = TASK_STATE.values()[dbCursor.getInt(8)];
		
		address = dbCursor.getString(9);
		minutesDone = dbCursor.getInt(10);
		minutesTodo = dbCursor.getInt(11);
		alarmTimeAhead = dbCursor.getInt(12);
		repeatWay = dbCursor.getLong(13);
	}
	
	protected static Cursor select(SQLiteDatabase db, String condition) {
		return db.query(TABLE_NAME, get_TableContentNameArray(), condition, null, null, null, null);
	}
	
	protected static Cursor select(SQLiteDatabase db, String whereClause, String whereArgs[]) {
		return db.query(TABLE_NAME, get_TableContentNameArray(), whereClause, whereArgs, null, null, null);
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
	
	protected void update(SQLiteDatabase db, String condition, String updateKey[], String updateValue[]) {
		ContentValues cv = new ContentValues();
		for (int i = 0; i < updateKey.length; i++) {
			cv.put(updateKey[i], updateValue[i]);
		}
		db.update(TABLE_NAME, cv, condition, null);		
	}
	
	protected void update(SQLiteDatabase db, String condition, String updateKey, String updateValue) {
		ContentValues cv = new ContentValues();
		cv.put(updateKey, updateValue);
		db.update(TABLE_NAME, cv, condition, null);
	}
		
	public void markDeleted(SQLiteDatabase db) {
		update(db, String.format("id = %d", id), "finishing_state", Integer.toString(TASK_STATE.DELETED.ordinal()));
	}
	
	public void markFinished(SQLiteDatabase db) {
		update(db, String.format("id = %d", id), "finishing_state", Integer.toString(TASK_STATE.FINISHED.ordinal()));
	}
}

public class TaskInfo extends TaskInfoWithDBComplex {

	public TaskInfo(Cursor dbCursor) {
		super(dbCursor);
	}
	
	public TaskInfo(String taskName, String taskHint) {
		name = taskName;
		hint = taskHint;
	}
	
	public TaskInfo(String taskName) {
		this(taskName, "");
	}
	
	public void setTimeRange(Date startTime, Date finishTime) {
		begTime = startTime;
		dueTime = finishTime;
	}
	
	public final static int REPEAT_TYPE_NOT_REPEAT    = 0;
	public final static int REPEAT_TYPE_WEEKLY        = 1;
	public final static int REPEAT_TYPE_MONTHLY       = 2;
	
	/**
	 * @param repeatType
	 * 	    "REPEAT_TYPE_NOT_REPEAT, REPEAT_TYPE_WEEKLY, REPEAT_TYPE_MONTHLY"
	 * @param repeatDays
	 * 		array to identify repeat days
	 */
	public void setPerodic(int repeatType, int repeatDays[]) {
		switch (repeatType) {
		case REPEAT_TYPE_NOT_REPEAT:
			repeatWay = 0;
			return ;
			
		case REPEAT_TYPE_WEEKLY:
			repeatWay = 1 << 8;
		case REPEAT_TYPE_MONTHLY:
			repeatWay = 1 << 32;

		default:
			break;
		}
		
		for (int day : repeatDays) {
			repeatWay = repeatWay | (1 << day);
		}
	}
}