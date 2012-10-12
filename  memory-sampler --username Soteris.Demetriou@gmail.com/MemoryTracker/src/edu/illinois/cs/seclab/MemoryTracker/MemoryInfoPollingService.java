package edu.illinois.cs.seclab.MemoryTracker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;

public class MemoryInfoPollingService extends IntentService{
	public MemoryInfoPollingService() {
		super("MemoryInfoPollingService");
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "MemoryInfoPollingService";
	public static final String GOT_NEW_MEMINFO_DATA = "Got new memory data from Dalvik" ;
	Intent intent;
	int counter = 0;
	Debug.MemoryInfo mI = new Debug.MemoryInfo();
	public static Context ctx ;
	
	@Override
	public void onCreate(){
		super.onCreate();
		intent = new Intent(GOT_NEW_MEMINFO_DATA);	
		Log.i(TAG, "onCreate Service");
		ctx = this;
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		getPSSvaluesOfAllProcesses();
    	Log.i(TAG, "=========================================================\n\n");
        
        Log.i(TAG, "onHandleIntent Service");

        //out = openLogFile(); Nexus S 4G - no memory card slot
	}
	
	@Override
	 public void onDestroy(){
	        
	        Log.i(TAG, "onDestroy Service");
	        super.onDestroy();
	}
	 
	private void getPSSvaluesOfAllProcesses() {
		// TODO Auto-generated method stub
	
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);  
		List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();  
		Map<Integer, String> pidMap = new TreeMap<Integer, String>();  
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses)  
		{  
		    pidMap.put(runningAppProcessInfo.pid, runningAppProcessInfo.processName);  
		}  
		Collection<Integer> keys = pidMap.keySet();  
		for(int key : keys)  
		{  
		    int pids[] = new int[1];  
		    pids[0] = key;  
		    android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
		   // MemoryReader.setValue(memoryInfoArray, pids, pidMap.get(pids[0])); //pids is the current pid, pidMap.get(pids[0] is its name)
		    
		    //print to log for now
		    for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray)  
			    {  
			        Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n",pids[0],pidMap.get(pids[0])));  
			        Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");  
			        Log.i(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");  
			        Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");  
			    }  
	
		} 
		long now = System.currentTimeMillis(); //milliseconds since Jan 1st 1970
		Timestamp tsTemp = new Timestamp(now); 
		Log.i(TAG, "Timestamp2: " + tsTemp.toString() + "\n");
		Log.i(TAG, "============================================================================ \n\n");
	}

}
