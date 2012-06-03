package edu.tongji.fiveidiots.ctrl;

import java.util.LinkedList;

public class TaskList {
	private LinkedList<TaskInfo> tasks;
	
	public void add(TaskInfo singleTask) {
		tasks.add(singleTask);
	}
}
