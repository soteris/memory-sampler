package edu.illinois.cs.seclab.MemoryTracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MemoryTrackService extends Service{
	
	private static final String TAG = "MemoryTrackService";
	public static final String GOT_NEW_MEM_DATA = "Got new memory data" ;
	private static final int DELAY = 1000; //when starting the thread
	private static final int INTERVAL = 400; //between threat looping 100ms = 10 times per second
	private static final Handler handler = new Handler();
	private static boolean kill = false;
	Intent intent, intentProc, intentMemInfo;
	int counter = 0;
	Debug.MemoryInfo mI = new Debug.MemoryInfo();
	public static Context ctx ;
	//Thread sendUpdatesToUI, getDalvikInfo, getProcMemInfo, getMapInfo; 
	//BufferedWriter out = null; Nexus S 4G - no memory card slot
	
	@Override
	public void onCreate(){
		super.onCreate();
		intent = new Intent(GOT_NEW_MEM_DATA);	
		intentProc = new Intent(this, ProcPollingService.class);
		intentMemInfo = new Intent(this, MemoryInfoPollingService.class);
		Log.i(TAG, "onCreate Service");
		ctx = this;
	}
	
	 @Override
	 public int onStartCommand(Intent intent,int flags, int startId) {
	        handler.removeCallbacks(sendUpdatesToUI);
	        handler.postDelayed(sendUpdatesToUI, DELAY); 
	        //
//	        handler.removeCallbacks(getDalvikInfo);
//	        handler.postDelayed(getDalvikInfo, DELAY); 
//	        //
//	        handler.removeCallbacks(getProcMemInfo);
//	        handler.postDelayed(getProcMemInfo, DELAY); 
//	        //
//	        handler.removeCallbacks(getMapInfo);
//	        handler.postDelayed(getMapInfo, DELAY); 
	        
	        Log.i(TAG, "onStart Service");
	        kill = false;
	        //out = openLogFile(); Nexus S 4G - no memory card slot
	        return START_NOT_STICKY;
	 }
	 
	 private BufferedWriter openLogFile(){
			BufferedWriter out = null;
			File root = Environment.getExternalStorageDirectory();
			File logFile = new File(root, "reading.txt");
			try{
				FileWriter writer = new FileWriter(logFile);
				out = new BufferedWriter(writer);
			}catch(IOException e){
				// TODO(xzhou) 
				e.printStackTrace();
			}
			return out;
		}

	@Override
	 public void onDestroy(){
	        
	        Log.i(TAG, "onDestroy Service");
	        //if it has already started this won't stop it
	        handler.removeCallbacks(sendUpdatesToUI);
	        //handler.removeCallbacks(getDalvikInfo);
	       // handler.removeCallbacks(getProcMemInfo);
	        //handler.removeCallbacks(getMapInfo);
	        unregisterReceiver(broadcastReceiver);
			stopService(intentProc);
			stopService(intentMemInfo);
	        kill = true;
	        //sendUpdatesToUI.kill();
	        super.onDestroy();
	}
	
	/* Listen to Service Updates */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.i(TAG, "Message from ProcPollingService or MemoryInfoService");       
        }

    };

	 //private CustomThread sendUpdatesToUI = new CustomThread();
	 private Runnable sendUpdatesToUI = new Runnable() 
	 {
	    	public void run() 
	    	{
	    		if(!kill){
	    			
	    			DisplayLoggingInfo();    		
	    			handler.postDelayed(this, INTERVAL); // 5 seconds - // use an application visible variable to control interval
	    		}
	    		else{
	    			/*
	    			if(out != null){
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					*/ //Nexus S 4G - no memory card slot
	    		}
	    	}
	  };

	  
	  //private CustomThread sendUpdatesToUI = new CustomThread();
	 private Runnable  getDalvikInfo = new Runnable() 
		 {
		    	public void run() {
		    		if(!kill){
		    			

		    	    	/* Uses MemoryInfo[] Dalvik values */
		    	    	getPSSvaluesOfAllProcesses();
		    	    	Log.i(TAG, "--------------------------------------------------------\n\n");    		
		    			handler.postDelayed(this, INTERVAL); // 5 seconds - // use an application visible variable to control interval
		    		}
		    		else{
		    			/*
		    			if(out != null){
							try {
								out.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						*/ //Nexus S 4G - no memory card slot
		    		}
		    	}
		  };
		  
		//private CustomThread sendUpdatesToUI = new CustomThread();
	 private Runnable getProcMemInfo =  new Runnable() {
			    	public void run() {
			    		if(!kill){
			    			

			    			getProcMemInfo();
			    	    	Log.i(TAG, "--------------------------------------------------------\n\n");   		
			    			handler.postDelayed(this, INTERVAL); // 5 seconds - // use an application visible variable to control interval
			    		}
			    		else{
			    			/*
			    			if(out != null){
								try {
									out.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							*/ //Nexus S 4G - no memory card slot
			    		}
			    	}
		};
			  
			//private CustomThread sendUpdatesToUI = new CustomThread();
	 private Runnable getMapInfo = new Runnable() 
		{
				    	public void run() {
				    		if(!kill){
				    			

				    			/* Uses proc/<PidOfMapsProcess>/statm */
				    	    	String pid = getMapPID("com.google.android.apps.maps");
				    			//Log.i(TAG, "PID: " + pid);
				    	    	getProcPIDstatm(pid); 		
				    			handler.postDelayed(this, INTERVAL); // 5 seconds - // use an application visible variable to control interval
				    		}
				    		else{
				    			/*
				    			if(out != null){
									try {
										out.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								*/ //Nexus S 4G - no memory card slot
				    		}
				    	}
		};
	    
	    
	 private void DisplayLoggingInfo() {
	    	Log.i(TAG, "entered DisplayLoggingInfo");
	    	
	    	/* Uses MemoryInfo[] Dalvik values */
	    	//getPSSvaluesOfAllProcesses();
	    	//Log.i(TAG, "--------------------------------------------------------\n\n");
	    	/* Uses /proc/meminfo */
//	    	getProcMemInfo();
//	    	Log.i(TAG, "--------------------------------------------------------\n\n");
//	    	
//	    	/* Uses proc/<PidOfMapsProcess>/statm */
//	    	String pid = getMapPID("com.google.android.apps.maps");
//			//Log.i(TAG, "PID: " + pid);
//	    	getProcPIDstatm(pid);
//	    	
//	    	Log.i(TAG, "=========================================================\n\n");
	    	
	    	// DON'T UPDATE UI is expensive
	    	//intent.putExtra("Test", "test value");
	    	//intent.putExtra("counter", String.valueOf(++counter));
	    	//sendBroadcast(intent);
	    	startService(intentProc);
			registerReceiver(broadcastReceiver, new IntentFilter(ProcPollingService.GOT_NEW_PROCMEM_DATA));
			startService(intentMemInfo);
			registerReceiver(broadcastReceiver, new IntentFilter(MemoryInfoPollingService.GOT_NEW_MEMINFO_DATA));
	    }
	 
	 /**
		 * void getProcPIDstatm(String pid)
		 * 
		 * @param pid The process's ID which we want to access its proc/pid/statm
		 */
	 private void getProcPIDstatm(String pid) {
		// Read proc/[pid]/statm for the requested PID (size; resident; share; text; lib; data; dt)
//	    	size       total program size
//	        (same as VmSize in /proc/[pid]/status)
//	    	resident   resident set size
//	        (same as VmRSS in /proc/[pid]/status)
//	    	share      shared pages (from shared mappings)
//	    	text       text (code)
//	    	lib        library (unused in Linux 2.6)
//	    	data       data + stack
//	    	dt         dirty pages (unused in Linux 2.6)

	    	///proc/[pid]/stat has a lot of per process info as well
			String fileNamePID = "proc/" + pid + "/statm";
			String tmp;
			String[] arrayOfString;
			
			try {
				FileReader localFileReader = new FileReader(fileNamePID);
				BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
				tmp = localBufferedReader.readLine();
				
				// separate items in line
				arrayOfString = tmp.split("\\s+");
				/**
				for (String item : arrayOfString){
					// TODO : add timestamp
					long now = System.currentTimeMillis(); //milliseconds since Jan 1st 1970
					Timestamp tsTemp = new Timestamp(now); 
					Log.i(TAG, "Timestamp: " + tsTemp.toString() + ", " + tmp + " : " + item + "\t");
					out.append(c)
				}
				*/
				long now = System.currentTimeMillis(); //milliseconds since Jan 1st 1970
				Timestamp tsTemp = new Timestamp(now); 
				//out.append("Timestamp: " + tsTemp.toString() + "; Resident(KB): " + arrayOfString[1] + "\n");Nexus S 4G - no memory card slot
				//Log.i(TAG, "Timestamp: " + tsTemp.toString() + "; Resident(KB): " + arrayOfString[1] + "\n");
				Log.i(TAG, "Timestamp: " + tsTemp.toString() + ", " + tmp + "\n");
				localBufferedReader.close();
			}
			catch (IOException e){
				
			}
	}

	 
	 /** 
	     * getMapPID
	     * 
	     * @return The pid of the map process
	     * */
	  static public String getMapPID(String processName){
		  Process process = null;
		  String pid = new String();
	    //String ps = new String();
		  try {
				process = new ProcessBuilder()
					.command("ps")
					.redirectErrorStream(true)
					.start();
				
				InputStream in = process.getInputStream();
		    	OutputStream out = process.getOutputStream();
		    	
		    	pid = readStream(in, processName);
		    	
		    	//pid = "1848";
		  } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    //pid = null;
			}
		  finally {
			process.destroy();
		  }
		  return pid;
	    	 	
	    
	    } 
	 
	  /**
		 * String readStream(InputStream in)
		 * 
		 * @param in: The input stream of ps command
		 * @return the PID of com.google.android.apps.maps process
		 */
	private static String readStream(InputStream in, String processName) {
		// TODO Auto-generated method stub
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				//StringBuilder ps = new StringBuilder();
				String line;
				String pid = new String();
				String[] arrayOfStrings;
				String mapsLine = new String();;

				try {
					while ( (line = reader.readLine()) != null ){
						//ps.append(line); //this is a char Sequence. we can use ".toString()" to convert it
						//Log.i(TAG, "line: " + line);
						arrayOfStrings = line.split("\\s+");
						for (String item : arrayOfStrings){
							//Log.i(TAG, "ITEM: " + item);
							if (item.compareTo(processName) == 0){
								//found the line
								mapsLine = line;
								//Log.i(TAG, "mapsLine: " + mapsLine);
								break;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//Log.i(TAG, "ps=" + ps);
				//return ps.toString();
				arrayOfStrings = mapsLine.split("\\s+");
				int i = 0;
				for (String item : arrayOfStrings){
					i++;
					if (i == 2) pid = item;
				}
				//Log.i(TAG, "PID: " + pid);
				return pid;
	} 	


	private void getProcMemInfo() {
		// TODO Auto-generated method stub
		 String str1 = "/proc/meminfo";
		    String str2;   
		    String[] arrayOfString;
		    long initial_memory = 0;
		    try {
		    	FileReader localFileReader = new FileReader(str1);
		    	BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
		    	str2 = localBufferedReader.readLine();//meminfo
		    	
		    	//read everything
		    	while (str2 != null){
		    		
		    		if (str2.startsWith("MemTotal:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
			    		initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024; 
				    	Log.i(TAG, "\nInitial_memory: "+ initial_memory + "\n");
		    		}
		    		
		    		if (str2.startsWith("MemTree:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("Buffers:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("Cached:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("Active:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("Inactive:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("SwapTotal:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		if (str2.startsWith("Dirty:")){
		    			arrayOfString = str2.split("\\s+"); 
			    		for (String num : arrayOfString) {
			    			Log.i(TAG, str2 + " : " + num + "\t");
			    		}
		    		}
		    		
		    		str2 = localBufferedReader.readLine();
		    	}
		    	
		    	
//		    	arrayOfString = str2.split("\\s+"); 
//		    	for (String num : arrayOfString) {
//		    		Log.i(TAG, "str2 : num \t" + str2 + " : " + num + "\t");
//		    	}
		    	//total Memory
//		    	initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024; 
//		    	Log.i(TAG, "\nInitial_memory: "+ initial_memory + "\n");
		    	localBufferedReader.close();
		    } 
		    catch (IOException e) 
		    {       
		    }
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
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
