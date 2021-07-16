package com.reSipWebRTC.util;

import android.util.Log;

public class Debug {

	 public static int MY_LOG_LEVEL = 6;
	 public static int ERROR = 1;
	 public static int WARN = 2;
	 public static int INFO = 3;
	 public static int DEBUG = 4;
	 public static int VERBOS = 5;
	
	public static void i(String tag, String msg){
		if(MY_LOG_LEVEL >= INFO)
		  Log.i(tag, msg);
	}
	
	public static void e(String tag, String msg){
		if(MY_LOG_LEVEL >= ERROR)
		Log.e(tag, msg);
	}
	
	public static void d(String tag, String msg){
		if(MY_LOG_LEVEL >= DEBUG)
		 Log.d(tag, msg);
	}

	public static void w(String tag, String msg){
		if(MY_LOG_LEVEL >= WARN)
		 Log.d(tag, msg);
	}
}
