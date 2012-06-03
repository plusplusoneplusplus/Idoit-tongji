/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;

import java.io.FileReader;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Attributes.Name;

import android.content.Context;

//import dalvik.system.DexClassLoader;



/*
 * TaskController 鎵?湁UI璋冪敤鏁版嵁閮介?杩囪绫绘柟娉?
 * 鎴愬憳锛?
 * taskContainer: 瀛樻斁鎵?湁鐨勪换鍔?
 * tempTask: 瀛樻斁涓存椂鏌愪釜浠诲姟璇︾粏淇℃伅
 * 鏂规硶锛?
 * void AddTask(TaskInfo aTask) 鏂板Task
 * void RemoveTask(int id) 鏍规嵁浠诲姟id鍙峰垹闄ょ浉搴斾换鍔?
 * TaskInfo ShowTaskInfo(int id) 鏍规嵁浠诲姟id鍙锋樉绀虹浉搴斾换鍔?
 * Boolean ModifyTaskInfo(int id,TaskInfo aTask) 鏍规嵁浠诲姟id鍙蜂慨鏀圭浉搴斾换鍔?
 * ArrayList<TaskInfo> ShowTaskList() 杩斿洖鎵?湁浠诲姟
 * ArrayList<TaskInfo> GetTodayTask(Date cur) 浼犲叆浠婂ぉ鐨勬棩鏈燂紝杩斿洖浠婂ぉ浠诲姟鍒楄〃
 * ArrayList<TaskInfo> GetFutureTask(Date cur) 浼犲叆浠婂ぉ鐨勬棩鏈燂紝杩斿洖浠婂ぉ浠诲姟鍒楄〃
 * ArrayList<TaskInfo> GetPeriodicTask() 杩斿洖鍛ㄦ湡鎬т换鍔″垪琛?
 * ArrayList<TaskInfo> GetFinishedTask() 杩斿洖宸茬粡瀹屾垚鐨勪换鍔″垪琛?
 * ArrayList<TaskInfo> SearchTag(String str) 鏍规嵁鏌愪釜鏍囩锛岃繑鍥炴嫢鏈夎鏍囩鐨勪换鍔?
 * TaskInfo Suggest(Date cur) 鏍规嵁鐜板湪鐨勬椂闂寸粰鍑轰笅涓?釜浠诲姟鐨勫缓璁?
 * void FinishCycle(int id,int interrupt,double percent,Date cur) 姣忓畬鎴愪竴涓晝鑼勯挓锛屽繀椤昏皟鐢ㄨ鍑芥暟锛屼紶鍏ヤ换鍔d锛屼腑鏂鏁帮紝姝ゆ钑冭寗閽熷懆鏈熸墍瀹屾垚浠诲姟鐧惧垎姣旓紝褰撳墠鐨勬椂闂?
 * void Save() 淇濆瓨
 * void Read() 璇诲彇
 */

public class TaskController {
	static int month [] = {31,28,31,30,31,30,31,31,30,31,30,31,100};
	private ArrayList<TaskInfo> taskContainer;
	private TaskInfo tempTask;
	static int oneclock = 25;
	private Context context;
	
	TaskController(Context context){
		this.context = context;
		taskContainer = new ArrayList<TaskInfo>();
		taskContainer.clear();
	}
	public void AddTask(TaskInfo aTask){
		DatabaseHelper dbHelper = new DatabaseHelper(context);		
		dbHelper.insert(aTask);
	}
	public void RemoveTask(int id){
		String where = "id = ?";
		String[] whereValue = {String.valueOf(id)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		dbHelper.delete(where, whereValue);
	}
	
	public TaskInfo ShowTaskInfo(int id){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.showinfo(id);
	}
	
	public Boolean ModifyTaskInfo(int id,TaskInfo aTask){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		dbHelper.update(id, aTask);
		return true;
	}
	
	public ArrayList<TaskInfo> ShowTaskList(){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(null,null);
	}
	
	public ArrayList<TaskInfo> GetTodayTask(Date cur){
		int prevtime = cur.getYear() << 20 + cur.getMonth() << 16 + cur.getDate() << 11;
		int nexttime = cur.getYear() << 20 + cur.getMonth() << 16 + (cur.getDate()+1) << 11;
		String where = "starttime >= ? AND starttime < ?";
		String[] whereValue = {String.valueOf(prevtime),String.valueOf(nexttime)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> GetFutureTask(Date cur){
		int nexttime = cur.getYear() << 20 + cur.getMonth() << 16 + (cur.getDate()+1) << 11;
		String where = "starttime >= ?";
		String[] whereValue = {String.valueOf(nexttime)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> GetPeriodicTask(){
		String where = "way > ?";
		String[] whereValue = {String.valueOf(0)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> GetFinishedTask(){
		String where = "status = ?";
		String[] whereValue = {String.valueOf(1)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> SearchTag(String str){
		ArrayList<TaskInfo> totalContainer = ShowTaskList();
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		tempContainer.clear();
		for ( int i = 0; i < totalContainer.size(); ++ i){
			tempTask = totalContainer.get(i);
			if (tempTask.searchTag(str)){
				tempContainer.add(tempTask);
			}
		}
		return tempContainer;
	}
	
	public long calculateTime(Date cur,Date des){
		
		long ans = 0;
		ans = (des.getTime() - cur.getTime()) / 60000;
		return ans;
	}
	public TaskInfo Suggest(Date cur){
		ArrayList<TaskInfo> totalContainer = ShowTaskList();
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		tempContainer.clear();
		double minfac = 10000.0;
		int minpri = 10000;
		TaskInfo ansFac = null;
		TaskInfo ansPri = null;
		for ( int i = 0; i < totalContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getStatus() == 0){
				long num = calculateTime(cur,tempTask.getDeadline());
				long cycleleft = num / oneclock - (tempTask.gettotaltime()-tempTask.getusedtime());
				if (cycleleft <= 0){
					if (tempTask.getPri() < minpri){
						ansPri = tempTask;
						minpri = tempTask.getPri();
					}
				}
				else if (tempTask.getPri() * 0.75 + cycleleft * 0.25 < minfac){
					ansFac = tempTask;
					minfac = tempTask.getPri() * 0.75 + cycleleft * 0.25;
				}
			}
		}
		if (minpri < 10000){
			return ansPri;
		}
		else return ansFac;
	}
	
	public void FinishCycle(int id,int interrupt,int time){
		TaskInfo t = ShowTaskInfo(id);
		t.setInterrupt(t.getInterrupt() + interrupt);
		t.setusedtime(t.getusedtime() + time);
		ModifyTaskInfo(id,t);
	}
}
