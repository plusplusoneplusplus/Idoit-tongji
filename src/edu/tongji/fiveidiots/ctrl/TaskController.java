/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;
import android.content.Context;


/**
 * TaskController 所有UI调用数据都通过该类方法
 * 成员：
 * tempTask: 存放临时某个任务详细信息
 * 鏂规硶锛?
 * void AddTask(TaskInfo aTask) 新增Task
 * void RemoveTask(long id) 根据任务id号删除相应任务
 * TaskInfo ShowTaskInfo(long id) 根据任务id号显示相应任务
 * Boolean ModifyTaskInfo(long id,TaskInfo aTask) 根据任务id号修改相应任务
 * ArrayList<TaskInfo> ShowTaskList() 返回所有任务
 * ArrayList<TaskInfo> GetTodayTask(Date cur) 传入今天的日期，返回今天任务列表
 * ArrayList<TaskInfo> GetFutureTask(Date cur) 传入今天的日期，返回未来任务列表
 * ArrayList<TaskInfo> GetPeriodicTask() 返回周期性任务列表
 * ArrayList<TaskInfo> GetFinishedTask() 返回已经完成的任务列表
 * ArrayList<TaskInfo> SearchTag(String str) 根据某个标签，返回拥有该标签的任务
 * TaskInfo Suggest(Date cur,int cycletime) 根据现在的时间和下一个蕃茄钟的时间长度给出下一个任务的建议
 * void FinishCycle(long id,int interrupt,int time) 每完成一个蕃茄钟，必须调用该函数，传入任务id，中断次数，此次蕃茄钟周期的时间
 * void InterruptTask(long id,int time) 任务未完成而发生中断，必须调用该函数，传入任务id和已经花费的时间
 */
public class TaskController {
	static int month [] = {31,28,31,30,31,30,31,31,30,31,30,31,100};
	private TaskInfo tempTask;
	static int oneclock = 25;
	private Context context;
	
	public TaskController(Context context){
		this.context = context;
	}
	public void AddTask(TaskInfo aTask){
		DatabaseHelper dbHelper = new DatabaseHelper(context);		
		dbHelper.insert(aTask);
	}
	public void RemoveTask(long id){
		String where = "id = ?";
		String[] whereValue = {String.valueOf(id)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		dbHelper.delete(where, whereValue);
	}
	
	public TaskInfo ShowTaskInfo(long id){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.showinfo(id);
	}
	
	public Boolean ModifyTaskInfo(long id,TaskInfo aTask){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		dbHelper.update(id, aTask);
		return true;
	}
	
	public ArrayList<TaskInfo> ShowTaskList(){
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(null,null);
	}
	
	public ArrayList<TaskInfo> GetTodayTask(Date cur){
		int nexttime = (cur.getYear() << 20) + (cur.getMonth() << 16) + ((cur.getDate()+1) << 11);
		String where = "starttime < ? AND status = ? AND NOT starttime = ?";
		String[] whereValue = {String.valueOf(nexttime),String.valueOf(0),String.valueOf(-1)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> GetFutureTask(Date cur){
		int nexttime = (cur.getYear() << 20) + (cur.getMonth() << 16) + ((cur.getDate()+1) << 11);
		String where = "starttime >= ? AND status = ?";
		String[] whereValue = {String.valueOf(nexttime),String.valueOf(0)};
		DatabaseHelper dbHelper = new DatabaseHelper(context);
		return dbHelper.query(where,whereValue);
	}
	
	public ArrayList<TaskInfo> GetPeriodicTask(){
		String where = "way > ? AND status = ?";
		String[] whereValue = {String.valueOf(0),String.valueOf(0)};
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
			if (tempTask.containsTag(str)){
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

	public TaskInfo Suggest(Date cur,int cycletime){
		ArrayList<TaskInfo> totalContainer = ShowTaskList();
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		tempContainer.clear();
		double minfac = 10000.0;
		int minpri = 10000;
		TaskInfo ansFac = null;
		TaskInfo ansPri = null;
		for ( int i = 0; i < totalContainer.size(); ++ i){
			tempTask = totalContainer.get(i);
			if (tempTask.getStatus() == 0 && tempTask.getDeadline() != null && tempTask.getTotalTime() != -1 && tempTask.getUsedTime() != -1 && tempTask.getPriority() != -1){
				long num = calculateTime(cur,tempTask.getDeadline());
				long timeleft = tempTask.getTotalTime() - tempTask.getUsedTime() + cycletime - num;
				if (timeleft < 0){
					if (tempTask.getPriority() < minpri){
						ansPri = tempTask;
						minpri = tempTask.getPriority();
					}
				}
				else if (tempTask.getPriority() * 0.75 + (timeleft / cycletime) * 0.25 < minfac){
					ansFac = tempTask;
					minfac = tempTask.getPriority() * 0.75 + (timeleft / cycletime) * 0.25;
				}
			}
		}
		if (minpri < 10000){
			return ansPri;
		}
		else
		{
			return ansFac;
		}
	}

	public void FinishCycle(long id,int interrupt,int time){
		TaskInfo t = ShowTaskInfo(id);
		System.out.println("ok");
		t.setInterrupt(t.getInterrupt() + interrupt);
		t.setUsedTime(t.getUsedTime() + time);
		System.out.println(t.getInterrupt() + " " + t.getUsedTime());
		ModifyTaskInfo(id,t);
		System.out.println("gogogogogo");
	}
	
	public void InterruptTask(long id,int time){
		FinishCycle(id, 1, time);
	}
}
