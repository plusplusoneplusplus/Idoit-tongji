/*
 * Author: Qrc
 * Date:2012-05-28
 */
package edu.tongji.fiveidiots.ctrl;

import java.util.ArrayList;
import java.util.Date;

public class TaskInfo {
	private int pri,pre_id,next_id,cycle,way,interrupt;
	private double percent;
	private String name,addr,hint;
	private Date starttime,deadline;
	private ArrayList<String> tag;
	private Boolean expire;
	
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
	public int getCycle() {
		return cycle;
	}
	public void setCycle(int cycle) {
		this.cycle = cycle;
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
	
	public Boolean Is
	
}
