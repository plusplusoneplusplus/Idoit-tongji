/*
 * Author: Qrc
 */
package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String DB_NAME = "aaa_db";
	private static final int DB_VERSION = 1;
	private static final String TABLE_NAME = "aaa";
	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String HINT = "hint";
	private static final String WAY = "way";
	private static final String STARTTIME = "starttime";
	private static final String DEADLINE = "deadline";
	private static final String PRIORITY = "priority";
	private static final String ADDRESS = "address";
	private static final String TAG = "tag";
	private static final String PREID = "preid";
	private static final String NEXTID = "nextid";
	private static final String STATUS = "status";
	private static final String ALARM = "alarm";
	private static final String USEDTIME = "usedtime";
	private static final String TOTALTIME = "totaltime";
	private static final String INTERRUPT = "interrupt";
	
	
	
	
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("create a Database");
		
		String sql = "CREATE TABLE " + TABLE_NAME + " (" 
				+ ID + " long, " + NAME + " ntext, " + ADDRESS + " ntext, " 
				+ HINT + " ntext, " + TAG + " ntext, " + STARTTIME + " int, " 
				+ DEADLINE + " int, " + ALARM + " int, " + WAY + " int, " + PRIORITY + " int, " 
				+ PREID + " int, " + NEXTID + " int, " + USEDTIME + " int, " + TOTALTIME + " int, " 
				+ INTERRUPT + " int, " + STATUS + " int) ";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public ContentValues seperateTask(TaskInfo aTask){
		long id,  starttime, deadline, alarm;
		String  tag; 
		System.out.println(4);
		id = aTask.getId();
		Date curDate = new Date();
		if (id == -1) id = curDate.getTime();
		System.out.println(3);
		ArrayList<String> tagArrayList = aTask.ExportTags();
		if (tagArrayList == null || tagArrayList.size() == 0) tag = null;
		else{
			tag = tagArrayList.get(0);
			for ( int i = 1; i < tagArrayList.size(); ++ i){
				tag = tag + " " + tagArrayList.get(i);
			}
		}
		System.out.println(2);
		Date start = aTask.getStartTime();
		Date dead = aTask.getDeadline();
		Date alarmDate = aTask.getAlarm();
		if (start != null ) starttime = (start.getYear() << 20) + (start.getMonth() << 16) + (start.getDate() << 11) + (start.getHours() << 6) + start.getMinutes();
		else starttime = -1;
		//if (start != null) System.out.println(start.getYear() + " " + start.getMonth() + " " + start.getDate() + " ");
		if (dead != null) deadline = (dead.getYear() << 20) + (dead.getMonth() << 16) + (dead.getDate() << 11) + (dead.getHours() << 6) + dead.getMinutes();
		else deadline = -1;
		if (alarmDate != null) alarm = (alarmDate.getYear() << 20) + (alarmDate.getMonth() << 16) + (alarmDate.getDate() << 11) + (alarmDate.getHours() << 6) + alarmDate.getMinutes();
		else alarm = -1;
		System.out.println(1);
		ContentValues tcv = new ContentValues();
		tcv.put(ID, id);
		tcv.put(NAME, aTask.getName());
		tcv.put(ADDRESS, aTask.getAddr());
		tcv.put(HINT, aTask.getHint());
		tcv.put(TAG, tag);
		tcv.put(STARTTIME, starttime);
		tcv.put(DEADLINE, deadline);
		tcv.put(ALARM, alarm);
		tcv.put(WAY, aTask.getIntWay());
		tcv.put(PRIORITY, aTask.getPriority());
		tcv.put(PREID, aTask.getPrevTaskId());
		tcv.put(NEXTID, aTask.getNextTaskId());
		tcv.put(USEDTIME, aTask.getUsedTime());
		tcv.put(TOTALTIME, aTask.getTotalTime());
		tcv.put(INTERRUPT, aTask.getInterrupt());
		tcv.put(STATUS, aTask.getStatus());
		return tcv;
	}
	
	public long insert(TaskInfo aTask) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = seperateTask(aTask);
		long row = db.insert(TABLE_NAME, null, cv);
		db.close();
		System.out.println("Insert Completed!");
		return row;
	}
	
	public void delete(String where,String [] whereValue) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, where, whereValue);
		db.close();
	}
	
	public void update(long id,TaskInfo aTask) {
		SQLiteDatabase db = this.getWritableDatabase();
		String[] whereValue = {String.valueOf(id)};
		ContentValues cv = seperateTask(aTask);
		db.update(TABLE_NAME, cv, "id = ?", whereValue);
		db.close();
	}
	
	
	public Date CombineDate(int index){
		int year = index >> 20;
		index -= (year << 20);
		int month = index >> 16;
		index -= (month << 16);
		int day = index >> 11;
		index -= (day << 11);
		int hour = index >> 6;
		index -= (hour << 6);
		int minute = index;
		Date tempDate = new Date(year,month,day,hour,minute,0);
		return tempDate;
	}
	
	public TaskInfo combineTask(Cursor cursor){
		Date starttime,deadline,alarm;
		int tt = cursor.getInt(5);
		if (tt == -1) starttime = null;
		else starttime = CombineDate(tt);
		tt = cursor.getInt(6);
		if (tt == -1) deadline = null;
		else deadline = CombineDate(tt);
		tt = cursor.getInt(7);
		if (tt == -1) alarm = null;
		else alarm = CombineDate(tt);
		
		ArrayList<String> tag;
		if (cursor.getString(4) == null) tag = null;
		else {
			String [] tagStrings = cursor.getString(4).split(" ");
			tag = new ArrayList<String>();
			tag.clear();
			for ( int i = 0; i < tagStrings.length; ++ i){
				tag.add(tagStrings[i]);
			}
		}
		
		TaskInfo tempTaskInfo = new TaskInfo(cursor.getLong(0) , cursor.getString(1) , cursor.getString(2) , cursor.getString(3)
				, tag , starttime , deadline , alarm , cursor.getInt(8)
				 , cursor.getInt(9) , cursor.getInt(10) , cursor.getInt(11) , cursor.getInt(12) , cursor.getInt(13)
				  , cursor.getInt(14) , cursor.getInt(15));
		return tempTaskInfo;
	}
	
	public TaskInfo showinfo(long id){
		
		SQLiteDatabase db = this.getReadableDatabase();
		String[] whereValue = {String.valueOf(id)};
		Cursor cursor = db.query(TABLE_NAME, null, "id = ?", whereValue, null, null, null);
		cursor.moveToFirst();
		TaskInfo t = combineTask(cursor);
		db.close();
		return t;
	}
	
	public ArrayList<TaskInfo> query(String where, String [] wherevalue) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, where, wherevalue, null, null, null);
		cursor.moveToFirst();
		//System.out.println(cursor.getCount());
		ArrayList<TaskInfo> c = new ArrayList<TaskInfo>();
		c.clear();
		for (int i = 0; i < cursor.getCount(); i++) {
			c.add(combineTask(cursor));
			cursor.moveToNext();
		}
		db.close();
		return c;
	}
	
	public void showall() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		cursor.moveToFirst();
		System.out.println(cursor.getCount());
		
		for (int i = 0; i < cursor.getCount(); i++) {
			System.out.println(cursor.getLong(0) + " " + cursor.getString(1) + " " + cursor.getString(2) + " " + cursor.getString(3)
			+ " " + cursor.getString(4) + " " + cursor.getInt(5) + " " + cursor.getInt(6) + " " + cursor.getInt(7) + " " + cursor.getInt(8)
			 + " " + cursor.getInt(9) + " " + cursor.getInt(10) + " " + cursor.getInt(11) + " " + cursor.getInt(12) + " " + cursor.getInt(13)
			  + " " + cursor.getInt(14) + " " + cursor.getInt(15));
			cursor.moveToNext();
		}

		db.close();
	}
}
