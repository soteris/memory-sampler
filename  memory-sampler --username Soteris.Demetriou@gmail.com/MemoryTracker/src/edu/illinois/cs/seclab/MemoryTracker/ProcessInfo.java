package edu.illinois.cs.seclab.MemoryTracker;

import java.util.Collection;
import java.util.Map;

import android.os.Debug.MemoryInfo;
import android.util.Log;

public class ProcessInfo {
	private static final String TAG = "Process Info";
	public android.os.Debug.MemoryInfo[] memoryInfoArray;
	public Map<Integer, String> pidMap;
	
	public ProcessInfo(android.os.Debug.MemoryInfo[] memoryInfoArray, Map<Integer, String> pidMap){
		this.memoryInfoArray = memoryInfoArray;
		this.pidMap = pidMap;
	}
	
	/**
	 * getMemoryInfoArray
	 * 
	 * @return Returns an android.os.Debug.MemoryInfo[] array
	 */
	public MemoryInfo[] getMemoryInfoArray(){
		return memoryInfoArray;
	}
	
	/**
	 * getMemoryInfoArray
	 * 
	 * @return Returns a Map<Integer, String> object
	 */
	public Map<Integer, String> getPidMap(){
		return pidMap;
	}
	
	public Long getMyPSS(){
		Long myPss = new Long(null);
		for(MemoryInfo pI : memoryInfoArray){
			myPss = (long) pI.getTotalPss();
		}
		return myPss;
		
	}
	
	/**
	 * getMyPid
	 * 
	 * @return Returns an Integer representation of this process's ID or 0 for error
	 */
	public Integer getMyPid(){
		Integer myId = new Integer(0);
		Collection<Integer> keys = this.pidMap.keySet();
		for(Integer key : keys){
			myId = key;
			Log.i(TAG, "ProcessInfo: getMyPid: return: " + myId + "\n");
		}
		return myId;
	}
	
	/**
	 * getMyProcName
	 * 
	 * @return Returns a String representation of the process's name or empty String if error
	 */
	public String getMyProcName(){
		String name = new String();
		Collection<Integer> keys = this.pidMap.keySet();
		for(Integer key : keys){
			Log.i(TAG, "ProcessInfo: getMyProcName: return: " + key + "\n");
			name = pidMap.get(key);
		}
		return name;
	}

}
