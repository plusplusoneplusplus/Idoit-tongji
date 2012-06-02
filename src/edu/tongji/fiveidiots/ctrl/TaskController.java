/*
 * Author: Qrc
 * Date:2012-05-28
 */
package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;

public class TaskController {
	
	private ArrayList<TaskInfo> taskContainer;
	// used as a temp variable
	private TaskInfo tempTask;
	public static int oneclock = 25;
	
	public TaskController(){
		taskContainer = new ArrayList<TaskInfo>();
		tempTask = null;
	}
	
	public void AddTask(TaskInfo aTask){
		taskContainer.add(aTask);
	}
	
	public void RemoveTask(int id){	
		tempTask = GetTaskInfo(id);
		taskContainer.remove(tempTask);
	}
	
	public TaskInfo GetTaskInfo(int id){
		TaskInfo task = null;
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getId() == id){
				task = tempTask;
				break;
			}
		}
		return task;
	}
	
	public boolean ReplaceTaskInfo(int id, TaskInfo after){
		boolean isModified = false;
		tempTask = this.GetTaskInfo(id);
		if(tempTask != null){
			tempTask.copy(after);
			isModified = true;
		}
		return isModified;
	}
	
	public ArrayList<TaskInfo> GetTaskList(){
		return taskContainer;
	}
	
	public ArrayList<TaskInfo> GetTaskListWithTag(String str){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		tempContainer.clear();
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.searchTag(str)){
				tempContainer.add(tempTask);
			}
		}
		return tempContainer;
	}
	
	public int CalculateTime(Date cur,Date des){
		int month [] = {31,28,31,30,31,30,31,31,30,31,30,31};
		return 200;
	}
	
	public TaskInfo Suggest(Date cur){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		tempContainer.clear();
		double minfac = 10000.0;
		int minpri = 10000;
		TaskInfo ansFac = null;
		TaskInfo ansPri = null;
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (!tempTask.IsExpire() && !tempTask.IsFinish()){
				int num = CalculateTime(cur,tempTask.getDeadline());
				int cycleleft = num / oneclock - tempTask.getNcycle();
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
		else 
		{
			return ansFac;
		}
	}
	
	public void FinishCycle(int id,int interrupt,double percent,Date cur){
		tempTask = GetTaskInfo(id);
		tempTask.FinishCycle(interrupt, percent, cur);
	}
	
	
}
