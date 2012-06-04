/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;

/**
 * TaskInfo记录每个任务详细信息
 * 成员：见下面注释
 * 注意：所有的set,add,import相关函数只是修改了TaskInfo的内容，不会存入数据库，必须调用TaskController里面的ModifyTask函数将TaskInfo的信息存入数据库
 * void ImportTag(ArrayList<String> tag) 将tag数组中内容复制给任务中的标签
 * ArrayList<String> ExportTag() 导出任务中标签的内容
 * void addTag(String atag) 新增一个tag
 * void deleteTag(long id) 删除一个tag
 * Boolean searchTag(String str) 搜索一个tag如果存在，返回true
 */
public class TaskInfo {
	
	/**
	 * 任务ID, -1表示新生成的task，还没存到数据库里过
	 */
	private long id = -1;
	/**
	 * 优先级
	 */
	private int priority = PRIORITY_UNSET;

	public static final int PRIORITY_UNSET = -1;
	public static final int PRIORITY_HIGH = 0;
	public static final int PRIORITY_MIDDLE = 1;
	public static final int PRIORITY_LOW = 2;

	/**
	 * 前驱任务ID
	 */
	private long prevTaskId = -1;
	/**
	 * 后继ID
	 */
	private long nextTaskId = -1;
	/**
	 * 已经花费的时间
	 */
	private int usedtime = 0;
	/**
	 * 预计任务完成总时间
	 */
	private int totaltime = -1;
	/**
	 * 周期任务的相关信息，way变量的赋值：非周期任务：0  
	 * 每周特定日执行的任务：其二进制第8位数值为1，剩下7位记录特定日，
	 * 例如周一，周三，周四，周日要执行的任务：数值二进制表示为：11001101  
	 * 如果是每隔特定日期执行的任务，其二进制表示的第9，8位为10，剩下7位表示相隔的日期。
	 */
	private int way = 0;
	/**
	 * 中断个数
	 */
	private int interrupt = 0;
	/**
	 * 任务名称
	 */
	private String name;
	/**
	 * 地址
	 */
	private String address = null;
	/**
	 * 备注
	 */
	private String hint = null;
	/**
	 * 任务开始时间
	 */
	private Date startTime = null;
	/**
	 * 截止时间
	 */
	private Date deadline = null;
	/**
	 * 任务提醒时间
	 */
	private Date alarm = null;
	/**
	 * 任务标签们
	 */
	private ArrayList<String> tags = new ArrayList<String>();
	/**
	 * 任务状态：1为完成，2为删除，0为正常状态。
	 */
	private int status = STATUS_NORMAL;
	
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_DELETED = 2;
	
	
	/**
	 * TaskInfo其他都可以没有，就是不能没有name
	 * @param name
	 */
	public TaskInfo(String name) {
		this.name = name;
	}
	
	public TaskInfo(long id,String name) {
		this.id = id;
		this.name = name;
	}
	
	public TaskInfo(String name,Date starttime,int way,int status){
		this.name = name;
		this.startTime = starttime;
		this.way = way;
		this.status = status;
	}
	
	public TaskInfo(String name,ArrayList<String> tag){
		this.name = name;
		this.tags = tag;
	}
	
	public TaskInfo(String name,Date deadline,int pri,int usedtime,int totaltime,int status){
		this.name = name;
		this.deadline = deadline;
		this.priority = pri;
		this.usedtime = usedtime;
		this.totaltime = totaltime;
		this.status = status;
	}
	
	public TaskInfo(int id,String name,int interrupt,int usedtime){
		this.id = id;
		this.name = name;
		this.interrupt = interrupt;
		this.usedtime = usedtime;
	}

	
	public TaskInfo(long id,String name,String addr,String hint,ArrayList<String> tag,Date starttime,Date deadline,Date alarm,int way,int pri, int pre_id,int next_id, int usedtime, int totaltime,int interrupt, int status){
		this.id = id;
		this.name = name;
		this.address = addr;
		this.hint = hint;
		this.priority = pri;
		this.prevTaskId = pre_id;
		this.nextTaskId = next_id;
		this.usedtime = usedtime;
		this.totaltime = totaltime;
		this.way = way;
		this.deadline = deadline;
		this.startTime = starttime;
		this.alarm = alarm;
		this.status = status;
		this.tags = tag;
		this.interrupt = interrupt;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getUsedTime() {
		return usedtime;
	}
	public void setUsedTime(int usedtime) {
		this.usedtime = usedtime;
	}
	public int getTotalTime() {
		return totaltime;
	}
	public void setTotalTime(int totaltime) {
		this.totaltime = totaltime;
	}

	public void setNextTaskId(long nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public long getNextTaskId() {
		return this.nextTaskId;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public PeriodInfo getPeriodInfo() {
		PeriodInfo p = new PeriodInfo(way);
		return p;
	}

	public void setPeriodInfo(PeriodInfo p) {
		this.way = p.TranslateKey();
	}
	public int getIntWay(){
		return way;
	}
	public void setIntWay(int way){
		this.way = way;
	}
	public int getInterrupt() {
		return interrupt;
	}
	public void setInterrupt(int interrupt) {
		this.interrupt = interrupt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getAddr() {
		return address;
	}
	public void setAddr(String addr) {
		this.address = addr;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date starttime) {
		this.startTime = starttime;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	
	/**
	 * 设置提醒
	 * @param aDate
	 */
	public void setAlarm(Date aDate) {
		this.alarm = aDate;
	}
	
	/**
	 * @return 提醒
	 */
	public Date getAlarm() {
		return this.alarm;
	}
	
	public void ImportTags(ArrayList<String> tag){
		this.tags.clear();
		this.tags.addAll(tag);
	}

	public ArrayList<String> ExportTags(){
		return this.tags;
	}
	
	public void addTag(String atag){
		tags.add(atag);
	}
	public void deleteTag(long id){
		tags.remove(id);
	}

	public boolean containsTag(String str){
		for ( int i = 0; i < tags.size(); ++ i){
			if (str.equals(tags.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getPrevTaskId() {
		return prevTaskId;
	}

	public void setPrevTaskId(long prevTaskId) {
		this.prevTaskId = prevTaskId;
	}

}
