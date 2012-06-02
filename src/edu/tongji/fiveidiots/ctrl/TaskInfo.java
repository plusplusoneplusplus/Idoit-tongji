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
 * Boolean searchTag(String str) 搜索一个tag如果存在，返回true
 * Boolean IsExpire() 返回该任务是否过期
 * void SetExpire() 设置该任务过期
 * Boolean IsFinish() 返回该任务是否完成
 * void SetFinish() 设置该任务状态为完成
 * Boolean IsDetermine()
 * void SetDetermine()
 * void FinishCycle(int interrupt,double percent,Date cur) 该任务完成一个蕃茄钟，调用一次该函数
 * 
 */
public class TaskInfo {
	private int pri,pre_id,next_id,pcycle,ncycle,way,interrupt,id; //优先级，前驱任务ID，后继ID，已经完成的番茄时钟数，尚待完成的番茄时钟数，是否为周期任务，中断个数，任务ID
	//way变量的赋值：非周期任务：0  每周特定日执行的任务：其二进制第22位数值为1，剩下21位记录特定日，例如周一，周三，周四，周日要执行的任务：数值二进制表示为：1 000 000 000 001 011 100 111  如果是每隔特定日期执行的任务，其二进制表示的第23，22位为10，剩下21位表示相隔的日期。
	private double percent;  //完成任务的百分比
	private String name,addr,hint; //任务名称，地址，注释
	private Date starttime,deadline;  //任务开始时间，截止时间
	private ArrayList<String> tag;  //任务标签们
	private Boolean expire,finish,determine;  //任务是否过期，是否完成
	
	public TaskInfo(int pri, int pre_id, int next_id, int pcycle, int ncycle,int way,int interrupt, int id, double percent, String name, String addr, String hint, Date starttime, Date deadline, ArrayList<String> tag, Boolean expire, Boolean finish, Boolean determine){
		this.id = id;
		this.name = name;
		this.addr = addr;
		this.hint = hint;
		this.pri = pri;
		this.pre_id = pre_id;
		this.next_id = next_id;
		this.pcycle = pcycle;
		this.ncycle = ncycle;
		this.way = way;
		this.deadline = new Date(deadline.getYear(),deadline.getMonth(),deadline.getDate(),deadline.getHours(),deadline.getMinutes());
		this.starttime = new Date(starttime.getYear(),starttime.getMonth(),starttime.getDate(),starttime.getHours(),starttime.getMinutes());
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
		this.pri = pri;
		this.pre_id = 0;
		this.next_id = 0;
		this.pcycle = 0;
		this.ncycle = 0;
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
		if (aTask.IsDetermine()) this.determine = true;
		else this.determine = false;
		this.tag = aTask.ExportTag();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
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
	
	public Boolean IsDetermine(){
		return determine;
	}
	public void SetDetermine(){
		determine = true;
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
