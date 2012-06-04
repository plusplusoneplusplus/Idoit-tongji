/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;
import java.util.ArrayList;
import java.util.Date;

import android.R.integer;



/*
 * TaskInfo记录每个任务详细信息
 * 成员：见下面注释
 * 注意：所有的set,add,import相关函数只是修改了TaskInfo的内容，不会存入数据库，必须调用TaskController里面的ModifyTask函数将TaskInfo的信息存入数据库
 * void ImportTag(ArrayList<String> tag) 将tag数组中内容复制给任务中的标签
 * ArrayList<String> ExportTag() 导出任务中标签的内容
 * void addTag(String atag) 新增一个tag
 * void deleteTag(int id) 删除一个tag
 * Boolean searchTag(String str) 搜索一个tag如果存在，返回true
 */
public class TaskInfo {
	private int pri,pre_id,next_id,usedtime,totaltime,way,interrupt,id; //优先级，前驱任务ID，后继ID，已经花费的时间，预计任务完成总时间，是否为周期任务，中断个数，任务ID
	//way变量的赋值：非周期任务：0  每周特定日执行的任务：其二进制第8位数值为1，剩下7位记录特定日，例如周一，周三，周四，周日要执行的任务：数值二进制表示为：11001101  如果是每隔特定日期执行的任务，其二进制表示的第9，8位为10，剩下7位表示相隔的日期。
	
	private String name,addr,hint; //任务名称，地址，注释
	private Date starttime,deadline,alarm;  //任务开始时间，截止时间，任务提醒时间
	private ArrayList<String> tag;  //任务标签们
	private int status;  //任务状态：1为Finish，2为删除，0为正常状态。
	
	

	public TaskInfo(int id,String name,String addr,String hint,ArrayList<String> tag,Date starttime,Date deadline,Date alarm,int way,int pri, int pre_id,int next_id, int usedtime, int totaltime,int interrupt, int status){
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.hint = hint;
		this.pri = pri;
		this.pre_id = pre_id;
		this.next_id = next_id;
		this.usedtime = usedtime;
		this.totaltime = totaltime;
		this.way = way;
		this.deadline = new Date(deadline.getYear(),deadline.getMonth(),deadline.getDate(),deadline.getHours(),deadline.getMinutes());
		this.starttime = new Date(starttime.getYear(),starttime.getMonth(),starttime.getDate(),starttime.getHours(),starttime.getMinutes());
		this.alarm = new Date(alarm.getYear(),alarm.getMonth(),alarm.getDate(),alarm.getHours(),alarm.getMinutes());
		this.status = status;
		this.interrupt = interrupt;
		this.tag = tag;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getusedtime() {
		return usedtime;
	}
	public void setusedtime(int usedtime) {
		this.usedtime = usedtime;
	}
	public int gettotaltime() {
		return totaltime;
	}
	public void settotaltime(int totaltime) {
		this.totaltime = totaltime;
	}
	public int getPri() {
		return pri;
	}
	public void setPri(int pri) {
		this.pri = pri;
	}
	public int getPre_id() {
		return pre_id;
	}
	public void setPre_id(int pre_id) {
		this.pre_id = pre_id;
	}
	public int getNext_id() {
		return next_id;
	}
	public void setNext_id(int next_id) {
		this.next_id = next_id;
	}
	
	public PeriodInfo getWay() {
		PeriodInfo p = new PeriodInfo(way);
		return p;
	}
	public void setWay(PeriodInfo p) {
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
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public Date getAlarm() {
		return alarm;
	}

	public void setAlarm(Date alarm) {
		this.alarm = alarm;
	}
	
	public void ImportTag(ArrayList<String> tag){
		this.tag.clear();
		this.tag.addAll(tag);
	}	
	public ArrayList<String> ExportTag(){
		return this.tag;
	}
	
	public void addTag(String atag){
		tag.add(atag);
	}
	public void deleteTag(int id){
		tag.remove(id);
	}
	public Boolean searchTag(String str){
		for ( int i = 0; i < tag.size(); ++ i){
			if (str.equals(tag.get(i))){
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
	
	
	
	
	
	
}
