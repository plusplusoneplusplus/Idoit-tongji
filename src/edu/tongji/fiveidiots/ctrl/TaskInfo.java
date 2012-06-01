/*
 * Author: Qrc
 * Date:2012-05-28
 */
package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;

public class TaskInfo {
	
	/** ID用int应该是不够的，还是用long吧 */
	private long id;
	
	private int pri,pre_id,next_id,pcycle,ncycle,way,interrupt;
	private double percent;
	/** 任务的名称 */
	private String name;
	private String addr,hint;
	private Date starttime,deadline;
	private ArrayList<String> tag;
	private Boolean expire,finish;
	
	/**
	 * 空的构造函数，测试方便
	 */
	public TaskInfo() {}
	
	TaskInfo(int id,String name, String addr, String hint, int pri, int pre_id, int next_id, int cycle, int way, Date deadline){
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.hint = hint;
		this.pri = pri;
		this.pre_id = pre_id;
		this.next_id = next_id;
		this.pcycle = 0;
		this.ncycle = cycle;
		this.way = way;
		this.deadline = deadline;
		expire = false;
		finish = false;
		percent = 0.0;
		tag = new ArrayList<String>();
		tag.clear();
	}
	
	public void copy(TaskInfo aTask){
		this.id = aTask.getId();
		this.name = aTask.getName();
		this.addr = aTask.getAddr();
		this.hint = aTask.getHint();
		this.pri = aTask.getPri();
		this.pre_id = aTask.getPre_id();
		this.next_id = aTask.getNext_id();
		this.pcycle = aTask.getPcycle();
		this.ncycle = aTask.getNcycle();
		this.way = aTask.getWay();
		this.deadline = aTask.getDeadline();
		this.percent = aTask.getPercent();
		if (aTask.IsExpire()) this.expire = true;
		else this.expire = false;
		if (aTask.IsFinish()) this.finish = true;
		else this.finish = false;
	}
	
	/**
	 * @return 此task的ID
	 */
	public long getId() {
		return id;
	}
	/**
	 * 设置此task的ID
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	public int getPcycle() {
		return pcycle;
	}
	public void setPcycle(int pcycle) {
		this.pcycle = pcycle;
	}
	public int getNcycle() {
		return ncycle;
	}
	public void setNcycle(int ncycle) {
		this.ncycle = ncycle;
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
	
	public int getWay() {
		return way;
	}
	public void setWay(int way) {
		this.way = way;
	}
	public int getInterrupt() {
		return interrupt;
	}
	public void setInterrupt(int interrupt) {
		this.interrupt = interrupt;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

	/**
	 * @return 此task的名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置此task的名称
	 * @param name
	 */
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
	
	public Boolean IsExpire(){
		return expire;
	}
	public void SetExpire(){
		expire = true;
	}
	
	public Boolean IsFinish(){
		return finish;
	}
	public void SetFinish(){
		finish = true;
	}
	
	public void FinishCycle(int interrupt,double percent,Date cur){
		this.interrupt += interrupt;
		this.percent += percent;
		if (this.percent >= 100) finish = true;
		if (this.deadline.before(cur)) expire = true;
		pcycle ++;
		ncycle = (int)Math.ceil((100.0 - percent) / (percent / pcycle));
	}
	
	
}
