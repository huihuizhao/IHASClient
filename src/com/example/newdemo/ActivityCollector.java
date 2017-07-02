package com.example.newdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollector {
	
	public static List<Activity> avtivities = new ArrayList<Activity>();
	
	public static void addActivity(Activity activity){
		avtivities.add(activity);
	}
	
	public static void removeActivity(Activity activity){
		avtivities.remove(activity);
	}
	
	public static void finishAll(){
		for (Activity activity : avtivities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
}
