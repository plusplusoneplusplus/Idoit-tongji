package edu.tongji.fiveidiots.ctrl;

import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		TaskInfo.createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		TaskInfo.dropTable(db);
		onCreate(db);
	}
	
	public static final int NORMAL = 0;
	public static final int INCLUDE_DELETED = 1;
	public static final int INCLUDE_FINISHED = 2;
	abstract public class TimeAreaSelector {		
		public int flag = 0;
		abstract public long getStartTime();
		abstract public long getFinisTime();
	}
	public class SelectToday extends TimeAreaSelector {
		
		public SelectToday(int select_flag) {
			flag = select_flag;
		}

		@Override
		public long getStartTime() {
			return new Date().getTime();
		}

		@Override
		public long getFinisTime() {
			return new Date().getTime() + 86400000;
		}
	}
	public class SelectFuture extends TimeAreaSelector {
		
		public SelectFuture(int select_flag) {
			flag = select_flag;
		}

		@Override
		public long getStartTime() {
			return new Date().getTime();
		}

		@Override
		public long getFinisTime() {
			return Long.MAX_VALUE;
		}
	}
	public class SelectThisWeek extends TimeAreaSelector {

		public SelectThisWeek(int select_flag) {
			flag = select_flag;
		}
		
		@Override
		public long getStartTime() {
			return new Date().getTime();
		}

		@Override
		public long getFinisTime() {
			return new Date().getTime() + 86400000 * 7;
		}
	}
	public class SelectAll extends TimeAreaSelector {

		public SelectAll(int select_flag) {
			flag = select_flag;
		}
		
		@Override
		public long getStartTime() {
			return 0;
		}

		@Override
		public long getFinisTime() {
			return Long.MAX_VALUE;
		}
	}
	
	/**
	 * @param selector can choose 
	 * 			{ SelectToday, SelectAll, SelectThisWeek, SelectFuture }
	 *  		constructors 'selector(flag)', flag can be 
	 *  		{ INCLUDE_DELETED, NORMAL, INCLUDE_FINISHED }
	 * @return TaskList
	 */
	public TaskList select(TimeAreaSelector selector) {
		Cursor c = TaskInfo.select(getReadableDatabase(), selector);
		TaskList tasks = new TaskList();
		
		if (c.moveToFirst()) {
			do {
				tasks.add(new TaskInfo(c));
			} while (c.moveToNext());
		}
		
		return tasks;
	}
	
	public void markDeleted(TaskInfo task) {
		task.markDeleted(getWritableDatabase());
	}
	
	public void markDeleted(TaskInfo tasks[]) {
		for (TaskInfo task : tasks) {
			task.markDeleted(getWritableDatabase());
		}
	}
}

