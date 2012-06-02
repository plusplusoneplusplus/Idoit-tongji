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

//import dalvik.system.DexClassLoader;



/*
 * TaskController 所有UI调用数据都通过该类方法
 * 成员：
 * taskContainer: 存放所有的任务
 * tempTask: 存放临时某个任务详细信息
 * 方法：
 * void AddTask(TaskInfo aTask) 新增Task
 * void RemoveTask(int id) 根据任务id号删除相应任务
 * TaskInfo ShowTaskInfo(int id) 根据任务id号显示相应任务
 * Boolean ModifyTaskInfo(int id,TaskInfo aTask) 根据任务id号修改相应任务
 * ArrayList<TaskInfo> ShowTaskList() 返回所有任务
 * ArrayList<TaskInfo> GetTodayTask(Date cur) 传入今天的日期，返回今天任务列表
 * ArrayList<TaskInfo> GetFutureTask(Date cur) 传入今天的日期，返回今天任务列表
 * ArrayList<TaskInfo> GetPeriodicTask() 返回周期性任务列表
 * ArrayList<TaskInfo> GetFinishedTask() 返回已经完成的任务列表
 * ArrayList<TaskInfo> SearchTag(String str) 根据某个标签，返回拥有该标签的任务
 * TaskInfo Suggest(Date cur) 根据现在的时间给出下一个任务的建议
 * void FinishCycle(int id,int interrupt,double percent,Date cur) 每完成一个蕃茄钟，必须调用该函数，传入任务id，中断次数，此次蕃茄钟周期所完成任务百分比，当前的时间
 * void Save() 保存
 * void Read() 读取
 */

public class TaskController {
	static int month [] = {31,28,31,30,31,30,31,31,30,31,30,31,100};
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
	
	public ArrayList<TaskInfo> GetTodayTask(Date cur){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		Date tempDate;
		tempContainer.clear();
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			tempDate = tempTask.getStarttime();
			if (tempDate.getYear() == cur.getYear() && tempDate.getMonth() == cur.getMonth() && tempDate.getDate() == cur.getDate() && !tempTask.IsFinish() && !tempTask.IsExpire()){
				tempContainer.add(tempTask);
			}
		}
		return tempContainer;
	}
	
	public ArrayList<TaskInfo> GetFutureTask(Date cur){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		Date tempDate;
		tempContainer.clear();
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			tempDate = tempTask.getStarttime();
			if (tempDate.after(cur) && !tempTask.IsFinish() && !tempTask.IsExpire()){
				tempContainer.add(tempTask);
			}
		}
		return tempContainer;
	}
	
	public ArrayList<TaskInfo> GetPeriodicTask(){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		Date tempDate;
		tempContainer.clear();
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			int tt = tempTask.getWay() >> 21;
			if (tt > 0 && !tempTask.IsFinish() && !tempTask.IsExpire()) tempContainer.add(tempTask);
		}
		return tempContainer;
	}
	
	public ArrayList<TaskInfo> GetFinishedTask(){
		ArrayList<TaskInfo> tempContainer = new ArrayList<TaskInfo>();
		Date tempDate;
		tempContainer.clear();
		for ( int i = 0; i < taskContainer.size(); ++ i){
			tempTask = taskContainer.get(i);
			if (tempTask.IsFinish()) tempContainer.add(tempTask);
		}
		return tempContainer;
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
	/*
	public void maintain(int aa,int ab,int ac,int ad,int ae){
		if (ae >= 59){
			ae = 0;
			++ ad;
		}
		if (ae < 0){
			ae = 59;
			-- ad;
		}
		if (ad >= 24){
			ad = 0;
			++ ac;
		}
		if (ad < 0){
			ad = 23;
			-- ac;
		}
		if (aa % 4 == 0 && aa % 100 != 0){
			month[1] = 29;
		}
		else{
			month[1] = 28;
		}
		
		if (ac > month[ab]){
			ac = 1;
			++ ab;
		}
		if (ac <= 0){
			ac = month[ab];
			-- ab;
		}
		if (ab >= 12){
			ab -= 12;
			++ aa;
		}
		if (ab < 0){
			ab = 0;
			-- aa;
		}		
	}
	
	public int compareDate(int aa,int ab,int ac,int ad,int ae,int ba,int bb,int bc,int bd,int be){
		if (aa != ba) return aa-ba;
		else {
			if (ab != bb) return ab-bb;
			else {
				if (ac != bc) return ac-bc;
				else {
					if (ad != bd) return ad-bd;
					else {
						return ae-be;
					}
				}
			}
		}
	}
	*/
	public long calculateTime(Date cur,Date des){
		
		long ans = 0;
		/*
		int tmp;
		int aa,ab,ac,ad,ae,ba,bb,bc,bd,be;
		aa = cur.getYear()+1900;
		ab = cur.getMonth();
		ac = cur.getDate();
		ad = cur.getHours();
		ae = cur.getMinutes();
		ba = des.getYear();
		bb = des.getMonth();
		bc = des.getDate();
		bd = des.getHours();
		be = des.getMinutes();
		
		tmp = month[ab];
		++ ab;
		
		maintain(aa,ab,ac,ad,ae);
		while (compareDate(aa,ab,ac,ad,ae,ba,bb,bc,bd,be) <= 0){
			ans += tmp * 24 * 60;
			tmp = month[ab];
			++ ab;
			maintain(aa, ab, ac, ad, ae);
		}
		-- ab;
		maintain(aa, ab, ac, ad, ae);
		
		++ ac;
		maintain(aa,ab,ac,ad,ae);
		while (compareDate(aa,ab,ac,ad,ae,ba,bb,bc,bd,be) <= 0){
			ans += 24 * 60;
			++ ac;
			maintain(aa, ab, ac, ad, ae);
		}
		-- ac;
		maintain(aa, ab, ac, ad, ae);
		
		++ ad;
		maintain(aa,ab,ac,ad,ae);
		while (compareDate(aa,ab,ac,ad,ae,ba,bb,bc,bd,be) <= 0){
			ans += 60;
			++ ad;
			maintain(aa, ab, ac, ad, ae);
		}
		-- ad;
		maintain(aa, ab, ac, ad, ae);
		
		++ ae;
		maintain(aa,ab,ac,ad,ae);
		while (compareDate(aa,ab,ac,ad,ae,ba,bb,bc,bd,be) <= 0){
			++ ans;
			++ ae;
			maintain(aa, ab, ac, ad, ae);
		}
		-- ae;
		maintain(aa, ab, ac, ad, ae);
		*/
		ans = (des.getTime() - cur.getTime()) / 60000;
		return ans;
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
				long num = calculateTime(cur,tempTask.getDeadline());
				long cycleleft = num / oneclock - tempTask.getNcycle();
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
	
	public void Save(){
		try{
			FileWriter fout = new FileWriter("User.txt");
			fout.write(taskContainer.size() + "\r\n");
			for (int i = 0; i < taskContainer.size(); ++ i){
				tempTask = taskContainer.get(i);
				fout.write(tempTask.getPri() + " " + tempTask.getPre_id() + " " + tempTask.getNext_id() + " " + tempTask.getPcycle() + " " + tempTask.getNcycle() + " " + tempTask.getWay() + " " + tempTask.getInterrupt() + " " + tempTask.getId() + " " + tempTask.getPercent() + "\r\n");
				fout.write(tempTask.getName() + "\r\n");
				fout.write(tempTask.getAddr() + "\r\n");
				fout.write(tempTask.getHint() + "\r\n");
				Date tempDate = tempTask.getStarttime();
				fout.write(tempDate.getYear() + " " + tempDate.getMonth() + " " + tempDate.getDate() + " " + tempDate.getHours() + " " + tempDate.getMinutes() + "\r\n");
				tempDate = tempTask.getDeadline();
				fout.write(tempDate.getYear() + " " + tempDate.getMonth() + " " + tempDate.getDate() + " " + tempDate.getHours() + " " + tempDate.getMinutes() + "\r\n");
				ArrayList<String> tempTag = tempTask.ExportTag();
				int number = tempTag.size();
				fout.write(number + "\r\n");
				for ( int j = 0; j < tempTag.size(); ++ j){
					fout.write(tempTag.get(j) + "\r\n");
				}
				if (tempTask.IsExpire()) fout.write("1");
				else fout.write("0");
				fout.write(" ");
				if (tempTask.IsFinish()) fout.write("1");
				else fout.write("0");
				fout.write(" ");
				if (tempTask.IsDetermine()) fout.write("1");
				else fout.write("0");
				fout.write("\r\n");
				
			}
			fout.close();
		}
		catch (Exception e){
			System.out.println(e);
		}
		
	}
	
	public void Read(){
		try{
			FileReader fin = new FileReader("User.txt");
			Scanner scanner = new Scanner(fin);
			taskContainer.clear();
			int pri,pre_id,next_id,pcycle,ncycle,way,interrupt,id;
			double percent;
			String name,addr,hint;
			Date starttime,deadline;
			ArrayList<String> tag = new ArrayList<String>();
			Boolean expire,finish,determine;
			int num;
			
			num = scanner.nextInt();
			for ( int i = 0; i < num; ++ i){
				pri = scanner.nextInt();
				
				pre_id = scanner.nextInt();
				next_id = scanner.nextInt();
				pcycle = scanner.nextInt();
				ncycle = scanner.nextInt();
				way = scanner.nextInt();
				interrupt = scanner.nextInt();
				id = scanner.nextInt();
				percent = scanner.nextDouble();
				
				name = scanner.nextLine();
				name = scanner.nextLine();
				addr = scanner.nextLine();
				hint = scanner.nextLine();
				
				starttime = new Date(scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt());
				deadline = new Date(scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt(),scanner.nextInt());
				int number = scanner.nextInt();
				scanner.nextLine();
				tag.clear();
				for ( int j = 0; j < number; ++ j){
					tag.add(scanner.nextLine());
				}
				int a = scanner.nextInt();
				if (a == 1) expire = true;
				else expire = false;
				a = scanner.nextInt();
				if (a == 1) finish = true;
				else finish = false;
				a = scanner.nextInt();
				if (a == 1) determine = true;
				else determine = false;
				TaskInfo tt = new TaskInfo(pri,pre_id,next_id,pcycle,ncycle,way,interrupt,id,percent,name,addr,hint,starttime,deadline,tag,expire,finish,determine);
				taskContainer.add(tt);
				
			}
			fin.close();
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
	
	
}
