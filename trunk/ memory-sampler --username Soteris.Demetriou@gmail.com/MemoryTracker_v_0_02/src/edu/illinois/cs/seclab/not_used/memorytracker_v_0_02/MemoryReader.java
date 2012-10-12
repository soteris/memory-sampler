package edu.illinois.cs.seclab.not_used.memorytracker_v_0_02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import android.os.Debug.MemoryInfo;
import android.util.Log;

public class MemoryReader {
	 private static String TAG ="Memory Reader";
	 private static Map<Integer, String> pidMap;
	 private static LinkedList<ProcessInfo> ll_memInfo = new LinkedList<ProcessInfo>();;
	
	 /*************** USING DALVIK'S INFo *******************************************/
	 
	/**
	* 
	* @param name The process's name
	* @return an ProcessInfo object or null if failed
	*/
	static public ProcessInfo getProcess(String name){
				//Long res = new Long(null);
		if (!ll_memInfo.isEmpty())
		{
			for(ProcessInfo pcI : ll_memInfo){
				if (name == pcI.getMyProcName()){
					return pcI;
				}
			}
		}
		return null;
	} 
	 
	/**
	* 
	* @param uid process's uid
	* @return an ProcessInfo object or null if failed
	*/
	static public ProcessInfo getProcess(int uid){
			//Long res = new Long(null);
		if (!ll_memInfo.isEmpty())
		{
			for(ProcessInfo pcI : ll_memInfo){
				if (uid == pcI.getMyPid()){
					return pcI;
				}
			}
		}
		return null;
	} 
	 
	
	/**
	 * 
	 * @param uid
	 * @return Return process name 
	 */
	static public String getName(int uid){
		//Long res = new Long(null);
		if (!ll_memInfo.isEmpty())
		{
			for(ProcessInfo pcI : ll_memInfo){
				if (uid == pcI.getMyPid()){
					return pcI.getMyProcName();
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param uid The process's PID
	 * @return Return PSS Memory metric of a process. 
	 */
	static public Long getValue(int uid){
		//Long res = new Long(null);
		if (!ll_memInfo.isEmpty())
		{
			for(ProcessInfo pcI : ll_memInfo){
				if (uid == pcI.getMyPid()){
					return pcI.getMyPSS();
				}
			}
		}
		return null;
	}
	
//	private Long getPssForPid(ProcessInfo pcI, int uid) {
//		// TODO Auto-generated method stub
//		Long res = null;
//		Collection<Integer> keys = pcI.getPidMap().keySet();
//		for(int key : keys){
//			if (key == uid){
//				//uid found
//				res = true;
//			}
//			else res = false;
//		}
//		return res;
//	}

	/**
	 * getTotalPss()
	 * 
	 * @return total PSS memory metric of all running processes or null if not available. <br />It returns a linked list with Memory Info 
	 * Arrays for each process or null if the list is empty.
	 * <p>Pss number is a metric the kernel computes that
	 *  takes into account memory sharing -- basically each page of 
	 *  RAM in a process is scaled by a ratio of the number of other processes 
	 *  also using that page. This way you can (in theory) add up the pss across 
	 *  all processes to see the total RAM they are using, and compare pss between 
	 *  processes to get a rough idea of their relative weight.
	 *  </p>
	 */
	static public Long getTotalPss(){
		// TODO(){
			int totalPSS = 0;
			//return the total memory? PSS for now
			if (!ll_memInfo.isEmpty()) {
				for(ProcessInfo pcI : ll_memInfo){
					MemoryInfo[] memArr = pcI.getMemoryInfoArray();
					for(android.os.Debug.MemoryInfo pidMemoryInfo: memArr)  
					{  
						totalPSS += pidMemoryInfo.getTotalPss();  
					}  
				}
				return (long) totalPSS;
			}
			else return null;
	}
	
	/**
	 * getValues()
	 * 
	 * @return a LinkedList for all ProcessInfo objects or null if it is empty
	 */
	static public LinkedList<ProcessInfo> getValues(){		
		// TODO()
		//Return the whole list
		if (!ll_memInfo.isEmpty()) return ll_memInfo;
		else return null;
	}
	
	/**
	 * setValue: It is used by Memory Track Service to update the information
	 * 	in MemoryReader class. It creates a new ProcessInfo object and adds it a linked list.
	 * 
	 * @param info An array: android.os.Debug.MemoryInfo[]
	 * @param pids an array with the PID - we use only pid[0] TODO: FIX IT
	 * @param procName A human readable description for this PID (preferably its package name)
	 */
	static public void setValue(android.os.Debug.MemoryInfo[] info, int pids[], String procName){		
		// TODO()
		pidMap = new TreeMap<Integer, String>(); 
		pidMap.put(pids[0], procName);
		
		ProcessInfo psI = new ProcessInfo(info, pidMap);
		
		ll_memInfo.add(psI);
		
		
//		for(android.os.Debug.MemoryInfo pidMemoryInfo: memoryInfoArray)  
//	    {  
//	        Log.i(TAG, String.format("** MEMINFO in pid %d [%s] **\n",pids[0],pidMap.get(pids[0])));  
//	        Log.i(TAG, " pidMemoryInfo.getTotalPrivateDirty(): " + pidMemoryInfo.getTotalPrivateDirty() + "\n");  
//	        Log.i(TAG, " pidMemoryInfo.getTotalPss(): " + pidMemoryInfo.getTotalPss() + "\n");  
//	        Log.i(TAG, " pidMemoryInfo.getTotalSharedDirty(): " + pidMemoryInfo.getTotalSharedDirty() + "\n");  
//	    }  
	}
		
		 /*************** USING proc/meminfo *******************************************/
		
	/**
	 * void getPIDMemory(String pid)
	 * 
	 * @param pid The process's ID which we want to access its proc/pid/statm
	 */
	static public void getPIDMemory(String pid){
// Read proc/[pid]/statm for the requested PID (size; resident; share; text; lib; data; dt)
//    	size       total program size
//        (same as VmSize in /proc/[pid]/status)
//    	resident   resident set size
//        (same as VmRSS in /proc/[pid]/status)
//    	share      shared pages (from shared mappings)
//    	text       text (code)
//    	lib        library (unused in Linux 2.6)
//    	data       data + stack
//    	dt         dirty pages (unused in Linux 2.6)

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
			for (String item : arrayOfString){
				Log.i(TAG, tmp + " : " + item + "\t");
		}
		localBufferedReader.close();
		}
		catch (IOException e){
			
		}
	}
	
	/**
	 * void getTotalMemory()
	 * 
	 * It outputs to the Log.i the result of /proc/meminfo
	 */
	static public void getTotalMemory() {  
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
	    	
	    	
//	    	arrayOfString = str2.split("\\s+"); 
//	    	for (String num : arrayOfString) {
//	    		Log.i(TAG, "str2 : num \t" + str2 + " : " + num + "\t");
//	    	}
	    	//total Memory
//	    	initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024; 
//	    	Log.i(TAG, "\nInitial_memory: "+ initial_memory + "\n");
	    	localBufferedReader.close();
	    } 
	    catch (IOException e) 
	    {       
	    }
	}
	    
	    /** 
	     * getMapPID
	     * 
	     * @return The pid of the map process
	     * */
	  static public String getMapPID(){
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
		    	
		    	pid = readStream(in);
		    	
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
	private static String readStream(InputStream in) {
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
					if (item.compareTo("com.google.android.apps.maps") == 0){
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
	
}
