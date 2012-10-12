package edu.illinois.cs.seclab.memorytracker_v_0_02;

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
	//private static boolean memWork = true;
	Intent intent, intentProc, intentMemInfo;
	int counter = 0;
	Debug.MemoryInfo mI = new Debug.MemoryInfo();
	public static Context ctx ;
	
	//Creation, opening and closing of file could be transfered to ProcPollingService
	public static BufferedWriter out = null; //proc
	
	@Override
	public void onCreate(){
		super.onCreate();
		intent = new Intent(GOT_NEW_MEM_DATA);	
		intentProc = new Intent(this, ProcPollingService.class);
		intentMemInfo = new Intent(this, MemoryInfoPollingService.class);
		Log.i(TAG, "onCreate Service");
		ctx = this;
		
		//Creation, opening and closing of file could be transfered to ProcPollingService
		out = openLogFile("Reading1.txt");

	}
	
	 @Override
	 public int onStartCommand(Intent intent,int flags, int startId) {
		 	Log.i(TAG, "onStart Service");
	        handler.removeCallbacks(sendUpdatesToLog);
	        handler.postDelayed(sendUpdatesToLog, DELAY); 
	        
	        kill = false;

	        return START_NOT_STICKY;
	 }
	 
	private BufferedWriter openLogFile(String fileName){
			BufferedWriter out = null;
			File root = Environment.getExternalStorageDirectory();
			File logFile = new File(root, fileName);
			try{
				FileWriter writer = new FileWriter(logFile);
				out = new BufferedWriter(writer);
			}catch(IOException e){
				// TODO(xzhou) 
				e.printStackTrace();
				Log.e(TAG, "File wasn't created!!");
			}
			return out;
	}

	@Override
	 public void onDestroy(){      
	        Log.i(TAG, "onDestroy Service");
	        kill = true;
	        
	        handler.removeCallbacks(sendUpdatesToLog);  //if it has already started this won't stop it        
			//stopService(intentProc);
			//stopService(intentMemInfo);
			//unregisterReceiver(broadcastReceiverProc);
			//unregisterReceiver(broadcastReceiverMem);
			
	        //Creation, opening and closing of file could be transfered to ProcPollingService
			if (out != null)
	        {
		        try {
		        	out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }

			
	        super.onDestroy();
	}
	
	/**
	 * 
	 * Listen to ProcPollingService Updates
	 */
	private BroadcastReceiver broadcastReceiverProc = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.i(TAG, "Message from ProcPollingService"); 
        	
        }

    };

    /**
	 * 
	 * Listen to MemoryInfoService Updates
	 */
	private BroadcastReceiver broadcastReceiverMem = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Log.i(TAG, "Message from MemoryInfoService"); 
        	//memWork = false;
        }

    };
    
    /**
     * Runnable that a handler triggers every time INTERVAL to 
     * start the IntentServices that monitor Proc files and running processes
     */
	 private Runnable sendUpdatesToLog = new Runnable() 
	 {
	    	public void run() 
	    	{
	    		if(!kill){
	    			
	    			StartMemoryTrackers();    		
	    			handler.postDelayed(this, INTERVAL);
	    		}
	    		else{
	    			//don't start another Tracker Worker Thread
	    		}
	    	}
	  };
	    
	 /**
	  * Being used to start the Services that monitor Memoy usages.
	  * Also registers broadcastReceiver to listen for updates from those Services.
	  * Currently there is no update being broadcasted.   
	  */
	 private void StartMemoryTrackers() {
	    	Log.i(TAG, "entered DisplayLoggingInfo");

	    	startService(intentProc);
			//registerReceiver(broadcastReceiverProc, new IntentFilter(ProcPollingService.GOT_NEW_PROCMEM_DATA));
			startService(intentMemInfo);
			//registerReceiver(broadcastReceiverMem, new IntentFilter(MemoryInfoPollingService.GOT_NEW_MEMINFO_DATA));
	    }


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

}

