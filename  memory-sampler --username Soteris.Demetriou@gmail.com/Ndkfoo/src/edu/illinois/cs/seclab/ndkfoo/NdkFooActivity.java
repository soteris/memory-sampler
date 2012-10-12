package edu.illinois.cs.seclab.ndkfoo;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Menu;

public class NdkFooActivity extends Activity {

	// load the c library - name matches jni/Android.mk
	static {
		System.loadLibrary("ndkfoo");
	}
	
	// declare the native code function - must match ndkfoo.c
	private native String invokeNativeFunction();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndk_foo);
        
        // this is where we call the native code
        String hello = invokeNativeFunction();
        
        new AlertDialog.Builder(this).setMessage(hello).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ndk_foo, menu);
        return true;
    }
}
