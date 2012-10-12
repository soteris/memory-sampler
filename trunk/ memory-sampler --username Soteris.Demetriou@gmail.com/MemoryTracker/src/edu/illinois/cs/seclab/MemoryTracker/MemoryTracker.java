package edu.illinois.cs.seclab.MemoryTracker;

import android.app.Application;
import android.content.Context;

public class MemoryTracker extends Application{
	private static Context context;
	
	public void onCreate(){
		super.onCreate();
		MemoryTracker.context = getApplicationContext();
		
	}
	
	public static Context getAppContext(){
		return context;
	}

}
