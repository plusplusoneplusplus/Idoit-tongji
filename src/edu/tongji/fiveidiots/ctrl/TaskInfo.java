/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;
import java.util.ArrayList;
import java.util.Date;

/*
 * TaskInfo记录每个任务详细信息
 * 成员：见下面注释
 * 方法：该类中方法不对外部开放，仅供TaskController类调用
 * void copy(TaskInfo aTask) 复制aTask的任务信息
 * void ImportTag(ArrayList<String> tag) 将tag数组中内容复制给任务中的标签
 * ArrayList<String> ExportTag() 导出任务中标签的内容
 * void addTag(String atag) 新增一个tag
 * void deleteTag(int id) 删除一个tag
 * boolean searchTag(String str) 搜索一个tag如果存在，返回true
 * boolean IsExpire() 返回该任务是否过期
 * void SetExpire() 设置该任务过期
 * boolean IsFinish() 返回该任务是否完成
 * void SetFinish() 设置该任务状态为完成
 * boolean IsDetermine()
 * void SetDetermine()
 * void FinishCycle(int interrupt,double percent,Date cur) 该任务完成一个蕃茄钟，调用一次该函数
 */

public class TaskInfo {
	// 优先级，前驱任务ID，后继ID，已经完成的番茄时钟数，尚待完成的番茄时钟数，完成任务的方式，中断个数，任务ID
	private int priority, preTaskId, nextTaskId, finishedCycle, unfinishedCycle, way, interrupt, id; 
	private double percent; // 完成任务的百分比
	private String name, addr, hint; // 任务名称，地址，注释
	private Date starttime, deadline; // 任务开始时间，截止时间
	private ArrayList<String> tag; // 任务标签们
	private boolean expire, finish, determine; // 任务是否过期，是否完成
	public TaskInfo(){
		
	}

	public TaskInfo(int pri, int pre_id, int next_id, int pcycle, int ncycle,
			int way, int interrupt, int id, double percent, String name,
			String addr, String hint, Date starttime, Date deadline,
			ArrayList<String> tag, boolean expire, boolean finish,
			boolean determine) {
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.hint = hint;
		this.priority = pri;
		this.preTaskId = pre_id;
		this.nextTaskId = next_id;
		this.finishedCycle = pcycle;
		this.unfinishedCycle = ncycle;
		this.way = way;
		this.deadline = deadline;
		this.starttime = starttime;
		this.expire = expire;
		this.finish = finish;
		this.percent = percent;
		this.tag = new ArrayList<String>(tag);
		this.determine = determine;
	}
	
	public TaskInfo(int pri,int id,String name,ArrayList<String> tag){
		this.id = id;
		this.name = name;
		this.addr = new String();
		this.hint = new String();
		this.priority = pri;
		this.preTaskId = 0;
		this.nextTaskId = 0;
		this.finishedCycle = 0;
		this.unfinishedCycle = 0;
		this.way = 0;
		this.deadline = new Date(0,0,1,0,0);
		this.starttime = new Date(0,0,1,0,0);
		this.expire = false;
		this.finish = false;
		this.percent = 0.0;
		this.tag = new ArrayList<String>(tag);
		this.determine = false;
	}
	
	public void copy(TaskInfo aTask){
		this.id = aTask.getId();
		this.name = aTask.getName();
		this.addr = aTask.getAddr();
		this.hint = aTask.getHint();
		this.priority = aTask.getPriority();
		this.preTaskId = aTask.getPreTaskId();
		this.nextTaskId = aTask.getNextTaskId();
		this.finishedCycle = aTask.getFinishedCycle();
		this.unfinishedCycle = aTask.getUnfinishedCycle();
		this.way = aTask.getWay();
		this.deadline = aTask.getDeadline();
		this.percent = aTask.getPercent();
		this.expire = aTask.IsExpire();
		this.finish = aTask.IsFinish();
		this.determine = aTask.IsDetermine();
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getPreTaskId() {
		return preTaskId;
	}

	public void setPreTaskId(int preTaskId) {
		this.preTaskId = preTaskId;
	}

	public int getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(int nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFinishedCycle() {
		return finishedCycle;
	}

	public void setFinishedCycle(int finishedCycle) {
		this.finishedCycle = finishedCycle;
	}

	public int getUnfinishedCycle() {
		return unfinishedCycle;
	}
	

	public void setUnfinishedCycle(int unfinishedCycle) {
		this.unfinishedCycle = unfinishedCycle;
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

	public boolean containsTag(String str){
		for ( int i = 0; i < tag.size(); ++ i){
			if (str.equals(tag.get(i))){
				return true;
			}
		}
		return false;
	}
	
	public boolean IsExpire(){
		return expire;
	}
	public void SetExpire(){
		expire = true;
	}
	
	public boolean IsFinish(){
		return finish;
	}
	public void SetFinish(){
		finish = true;
	}
	
	public boolean IsDetermine(){
		return determine;
	}
	public void SetDetermine(){
		determine = true;
	}
	
	public void FinishCycle(int interrupt,double percent,Date cur){
		this.interrupt += interrupt;
		this.percent += percent;
		if (this.percent >= 100)
			finish = true;
		if (this.deadline.before(cur))
			expire = true;
		finishedCycle++;
		unfinishedCycle = (int) Math.ceil((100.0 - percent) / (percent / finishedCycle));
		if (this.percent >= 100) finish = true;
		if (this.deadline.before(cur)) expire = true;
		finishedCycle ++;
		unfinishedCycle = (int)Math.ceil((100.0 - percent) / (percent / finishedCycle));
	}
	
}
