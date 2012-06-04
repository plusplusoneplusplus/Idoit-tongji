/*
 * Author: Qrc
 * Date:2012-05-28
 */

package edu.tongji.fiveidiots.ctrl;
import java.util.ArrayList;
import java.util.Date;

import android.R.integer;



/*
 * TaskInfo璁板綍姣忎釜浠诲姟璇︾粏淇℃伅
 * 鎴愬憳锛氳涓嬮潰娉ㄩ噴
 * 鏂规硶锛氳绫讳腑鏂规硶涓嶅澶栭儴寮?斁锛屼粎渚汿askController绫昏皟鐢?
 * void copy(TaskInfo aTask) 澶嶅埗aTask鐨勪换鍔′俊鎭?
 * void ImportTag(ArrayList<String> tag) 灏唗ag鏁扮粍涓唴瀹瑰鍒剁粰浠诲姟涓殑鏍囩
 * ArrayList<String> ExportTag() 瀵煎嚭浠诲姟涓爣绛剧殑鍐呭
 * void addTag(String atag) 鏂板涓?釜tag
 * void deleteTag(int id) 鍒犻櫎涓?釜tag
 * Boolean searchTag(String str) 鎼滅储涓?釜tag濡傛灉瀛樺湪锛岃繑鍥瀟rue
 * void FinishCycle(int interrupt,double percent,Date cur) 璇ヤ换鍔″畬鎴愪竴涓晝鑼勯挓锛岃皟鐢ㄤ竴娆¤鍑芥暟
 * 
 */
public class TaskInfo {
	private int pri,pre_id,next_id,usedtime,totaltime,way,interrupt,id; //浼樺厛绾э紝鍓嶉┍浠诲姟ID锛屽悗缁D锛屽凡缁忓畬鎴愮殑鐣寗鏃堕挓鏁帮紝灏氬緟瀹屾垚鐨勭暘鑼勬椂閽熸暟锛屾槸鍚︿负鍛ㄦ湡浠诲姟锛屼腑鏂釜鏁帮紝浠诲姟ID
	//way鍙橀噺鐨勮祴鍊硷細闈炲懆鏈熶换鍔★細0  姣忓懆鐗瑰畾鏃ユ墽琛岀殑浠诲姟锛氬叾浜岃繘鍒剁22浣嶆暟鍊间负1锛屽墿涓?1浣嶈褰曠壒瀹氭棩锛屼緥濡傚懆涓?紝鍛ㄤ笁锛屽懆鍥涳紝鍛ㄦ棩瑕佹墽琛岀殑浠诲姟锛氭暟鍊间簩杩涘埗琛ㄧず涓猴細1 000 000 000 001 011 100 111  濡傛灉鏄瘡闅旂壒瀹氭棩鏈熸墽琛岀殑浠诲姟锛屽叾浜岃繘鍒惰〃绀虹殑绗?3锛?2浣嶄负10锛屽墿涓?1浣嶈〃绀虹浉闅旂殑鏃ユ湡銆?
	
	private String name,addr,hint; //浠诲姟鍚嶇О锛屽湴鍧?紝娉ㄩ噴
	private Date starttime,deadline,alarm;  //浠诲姟寮?鏃堕棿锛屾埅姝㈡椂闂?
	private ArrayList<String> tag;  //浠诲姟鏍囩浠?
	private int status;  //1为Finish
	
	

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
