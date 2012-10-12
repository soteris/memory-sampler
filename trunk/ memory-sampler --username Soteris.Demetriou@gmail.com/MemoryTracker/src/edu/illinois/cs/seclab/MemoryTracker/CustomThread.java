package edu.illinois.cs.seclab.MemoryTracker;

import android.os.Handler;
import android.util.Log;

public class CustomThread implements Runnable{
	private boolean killme = false;
	public static final String TAG = "CustomThread";
	private final Handler handler = new Handler();
	
	public void run() {
		// TODO Auto-generated method stub
		while(!killme){
			//fetch mem info every 5 seconds
			Log.i(TAG, "ninga running");
			//MemoryTrackService.handler.postDelayed(this, 5000); // 5 seconds
			//handler.postDelayed(this, 5000);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				/*Interrupted exception will be thrown when a sleeping or waiting                
	             * thread is interrupted.                
	             */                
	             Log.i(TAG, "Second Thread is interrupted when it is sleeping" + e); 
			}
		}
		Log.i(TAG, "ninga stopped");
	}
	
	public void kill(){
		killme = true;
		Log.i(TAG, "ninga should stop");
	}

}
