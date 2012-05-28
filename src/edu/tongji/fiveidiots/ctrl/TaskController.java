/*
 * Author: Qrc
 * Date:2012-05-28
 */
package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.R.layout;

public class TaskController {
	
	private ArrayList<TaskInfo> taskContainer;
	private TaskInfo tempTask;
	static int oneclock = 25;
	
	TaskController(){
		taskContainer = new ArrayList<TaskInfo>();
		taskContainer.clear();
	}
	public void AddTask(TaskInfo aTask){
		taskContainer.add(aTask);
	}
	public void RemoveTask(int id){
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getId() == id){
				taskContainer.remove(i);
				break;
			}
		}	
	}
	public TaskInfo ShowTaskInfo(int id){
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getId() == id){
				return tempTask;
			}
		}
		return null;
	}
	public Boolean ModifyTaskInfo(int id,TaskInfo aTask){
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getId() == id){
				tempTask.copy(aTask);
				return true;
			}
		}
		return false;
	}
	public ArrayList<TaskInfo> ShowTaskList(){
		return taskContainer;
	}
	
	public ArrayList<TaskInfo> SearchTag(String str){
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
	
	public int calculateTime(Date cur,Date des){
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
				int num = calculateTime(cur,tempTask.getDeadline());
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
		else return ansFac;
	}
	
	public void FinishCycle(int id,int interrupt,double percent,Date cur){
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.getId() == id){
				tempTask.FinishCycle(interrupt,percent,cur);
				break;
			}
		}
	}
	
	
	
}
