package edu.tongji.fiveidiots.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.tongji.fiveidiots.ctrl.TaskInfo;

/**
 * 用于帮助测试，生成测试数据等
 * @author Andriy
 */
public class TestingHelper {

	private static int randomID = 1;
	
	public static TaskInfo getRandomTask() {
		TaskInfo info = new TaskInfo();
		info.setId(randomID);
		info.setAddr("addr: " + getRandomString(5));
		info.setDeadline(null);
		info.SetExpire();
		info.setHint("hint: " + getRandomString(5));
		info.setInterrupt(0);
		info.setName("name: " + getRandomString(5));

		randomID++;
		return info;
	}
	
	public static List<TaskInfo> getRandomTaskList() {
		ArrayList<TaskInfo> list = new ArrayList<TaskInfo>();
		int length = new Random().nextInt(5) + 5;
		for (int i = 0; i < length; i++) {
			list.add(getRandomTask());
		}
		return list;
	}
	
	private static String getRandomString(int length) {
		String source = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder builder = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			builder.append(source.charAt(random.nextInt(source.length())));
		}
		return builder.toString();
	}
}
