package edu.illinois.cs.seclab.MemoryTracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

import org.afree.chart.demo.activity.TimeSeriesChartDemo1Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity{

	private static final String TAG = "MainActivity";
	private Intent intent, intentProc;
	private Button startMem, stopMem, totalPss, getMemInfoArr, memProc, memProcPID;
	Thread procService;
	private TextView txtTest,txtCounter;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Process process = null;
        setContentView(R.layout.activity_main);
        findViews();       
        intent = new Intent(this, MemoryTrackService.class);
       // intentProc = new Intent(this, ProcPollingService.class);
        Log.i(TAG, "onCreate Main Activity");       
        registerButtons();
//        try {
//			 process = new ProcessBuilder()
//				.command("netcfg")
//				.redirectErrorStream(true)
//				.start();
//			
//			InputStream in = process.getInputStream();
//	    	OutputStream out = process.getOutputStream();
//	    	
//	    	BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
//	    	Log.i(TAG, reader.readLine());
//	    	
//	    	//pid = "1848";
//	  } catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		    //pid = null;
//		}
//	  finally {
//		process.destroy();
//	  }
    }
    
    private void findViews() {
		// TODO Auto-generated method stub
    	txtTest = (TextView) findViewById(R.id.textView1);  	
    	txtCounter = (TextView) findViewById(R.id.textView2);
	}

	/* register our UI's buttons*/
    private void registerButtons() {
		// TODO Auto-generated method stub
    	registerMemServiceStartButton();
        registerMemServiceStopButton();
        /*
        registerTotalPssButton();
        registerMemInfoArrButton();
        registerProcMemInfoButton();
        registerProcMemPIDInfoButton();
        */
	}

    /* Listen to Service Updates */
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	updateUI(intent);       
        }

    };
    
    /**
     * Button to start the MemoryTrackingService
     */
    public void registerMemServiceStartButton(){
    	startMem = (Button) findViewById(R.id.ButtonStartMem);
    	startMem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick: starting service");
//				procService = new Thread(){
//					public void run(){
//						startService(intent);
//						registerReceiver(broadcastReceiver, new IntentFilter(MemoryTrackService.GOT_NEW_MEM_DATA));
//						Log.i(TAG, "Service thread called");
//					}
//				};
//				procService.start();
				startService(intent);
				registerReceiver(broadcastReceiver, new IntentFilter(MemoryTrackService.GOT_NEW_MEM_DATA));
				txtTest.setText("Service Started!");
				txtTest.setTextColor(Color.GREEN);
				txtCounter.setText("");
			}
		});
    }
    
    /**
     * Button to stop MemoryTrackingService
     */
    public void registerMemServiceStopButton(){
    	stopMem = (Button) findViewById(R.id.ButtonStopMem);
    	stopMem.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onClick: stopping service");
				unregisterReceiver(broadcastReceiver);
				stopService(intent);
				txtTest.setText("Service Stopped!");
				txtTest.setTextColor(Color.RED);
				//kill thread
			}
		});
    	
    }
    
    /**
     * Button that fetches and displays to the log the Total PSS value as given by Dalvik
     * it included all the processes
     */
    /*
    public void registerTotalPssButton(){
    	totalPss = (Button) findViewById(R.id.ButtonTotPss);
    	totalPss.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				Log.i(TAG, "onClick: MemoryReader.getTotalPss(): " + MemoryReader.getTotalPss());
				
			}
		});
    	
    }
    */
    
    /**
     * Button to directly fetch a list with all the ProcessInfo objects = all the processes stored by the service.
     * For testing purposes we calculate the totalPSS and print it to the Log 
     */
//    public void registerMemInfoArrButton(){
//    	getMemInfoArr = (Button) findViewById(R.id.ButtonMemInfoArr);
//    	getMemInfoArr.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				long totPss = 0;
//				LinkedList<ProcessInfo> procInfoList = MemoryReader.getValues();
//				Log.i(TAG, "onClick:  MemoryReader.getValues()\n");
//				
//				if (!procInfoList.isEmpty()) {
//					for(ProcessInfo pcI : procInfoList){
//						MemoryInfo[] memArr = pcI.getMemoryInfoArray();
//						for(android.os.Debug.MemoryInfo pidMemoryInfo: memArr)  
//						{  
//							totPss += pidMemoryInfo.getTotalPss();
//							Log.i(TAG, "getTotalPss(), totPss: " + totPss + "\n");  
//						}  
//					}
//					//return (long) totalPSS;
//					Log.i(TAG, "getTotalPss(), totPss: " + totPss + "\n"); 
//				}
//				else Log.i(TAG, "LinkedList empty \n"); 
//			}
//		});
//    	
//    }
    
    /**
     * Read /proc/meminfo
     * 
     */
//    public void registerProcMemInfoButton(){
//    	memProc = (Button) findViewById(R.id.ButtonProcMem);
//    	memProc.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub				
//				//Log.i(TAG, "onClick: MemoryReader.getTotalPss(): " + MemoryReader.);
//				MemoryReader.getTotalMemory();
//				
//			}
//		});
//    	
//    }
    
    /**
     * Read /proc/PID/stam
     * 
     */
//    public void registerProcMemPIDInfoButton(){
//    	memProcPID = (Button) findViewById(R.id.ButtonProcMemPID);
//    	memProcPID.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// TODO Auto-generated method stub				
//				//Log.i(TAG, "onClick: MemoryReader.getTotalPss(): " + MemoryReader.);
//				String pid = MemoryReader.getMapPID();
//				//Log.i(TAG, "PID: " + pid);
//				MemoryReader.getPIDMemory(pid);
//				
//				///////CRASHES///////////////////////////////////////////////////////////////////////
//				//Intent intent = new Intent(MainActivity.this, TimeSeriesChartDemo1Activity.class);
//				//MainActivity.this.startActivity(intent);
//			}
//		});
//    	
//    }
//    
    
    
    private void updateUI(Intent intent) {
    	String counter = intent.getStringExtra("counter"); 
    	String test = intent.getStringExtra("Test");
    	Log.i(TAG, "Test value from Service:" + test);
    	Log.i(TAG, "Counter from Service:" + counter);
 
    	txtTest.setText("Service Started at least once");
    	txtCounter.setText("Iteration:" + counter);
    }
    
    
}
