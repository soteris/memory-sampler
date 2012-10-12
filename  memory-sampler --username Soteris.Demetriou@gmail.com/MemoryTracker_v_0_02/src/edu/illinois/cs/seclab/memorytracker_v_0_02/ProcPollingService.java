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

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class ProcPollingService extends IntentService{
	public ProcPollingService() {
		super("ProcPollingService");
		// TODO Auto-generated constructor stub
	}

	private static final String TAG = "ProcPollingService";
	public static final String GOT_NEW_PROCMEM_DATA = "Got new memory data" ;
	Intent intent;
	int counter = 0;
	Debug.MemoryInfo mI = new Debug.MemoryInfo();
	public static Context ctx ;
	
	@Override
	public void onCreate(){
		super.onCreate();		
		Log.i(TAG, "onCreate Service");
		intent = new Intent(GOT_NEW_PROCMEM_DATA);	
		ctx = this;
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onHandleIntent Service");
		
		getProcMemInfo();
    	Log.i(TAG, "--------------------------------------------------------\n");   
    	/* Uses proc/<PidOfMapsProcess>/statm */
    	String pid = getMapPID("com.google.android.apps.maps");
		//Log.i(TAG, "PID: " + pid);
    	getProcPIDstatm(pid); 	
    	try {
			MemoryTrackService.out.append("EOFMESUREMENT \n");
		} 
    	catch(NullPointerException e){
			Log.e(TAG, "Out does not exist. Can't write.");
		}
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i(TAG, "=========================================================\n\n");      
 
	}
	
	@Override
	 public void onDestroy(){	        
	        Log.i(TAG, "onDestroy Service");	
	        
	        super.onDestroy();
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
				/*
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
				try{
					MemoryTrackService.out.append(tsTemp.toString() + "; " + tmp + "\n"); //Nexus S 4G - no memory card slot
				}
				catch(NullPointerException e){
					Log.e(TAG, "Out does not exist. Can't write.");
				}
				//Log.i(TAG, "Timestamp: " + tsTemp.toString() + "; Resident(KB): " + arrayOfString[1] + "\n");
				Log.i(TAG, "Timestamp: " + tsTemp.toString() + ", " + tmp + "\n");
				localBufferedReader.close();
			}
			catch (IOException e){
				
			}
	}

	 
	 /** 
	     * getMapPID
	     * @param processName The name of the running process we are looking for
	     * @return the PID of <i>processName</i>
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
		 * @param in The input stream of <i>ps</i> Linux command
		 * @param processName The name of the running process we are looking for
		 * @return the PID of <i>processName</i>
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

	/**
	 * getProcMemInfo()
	 * This method reads specific content from the /proc/meminfo file
	 * and dumps it to the Log.i
	 */
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
		    	try{
					MemoryTrackService.out.append(str2 + "\n"); 
				}
				catch(NullPointerException e){
					Log.e(TAG, "Out does not exist. Can't write.");
				}
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
		    		try{
						MemoryTrackService.out.append(str2 + "\n"); 
					}
					catch(NullPointerException e){
						Log.e(TAG, "Out does not exist. Can't write.");
					}
		    	}
		    	
		    	localBufferedReader.close();
		    } 
		    catch (IOException e) 
		    {       
		    }
	}

}
