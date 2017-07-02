package com.example.newdemo.util;

import android.util.Log;

public class MyLog {

	private static final String TAG = "dukun";
	//是否打印输出，true：打印输出，false：不打印输出
	private static final boolean PRINT_LOG = true;
	
	public static void d(String msg) {
		if(PRINT_LOG) {
			Log.d(TAG, msg);
		}
	}
	//后台输出
	public static void d(String tag, String msg) {
		if(PRINT_LOG) {
			Log.d(tag, msg);
		}
	}
	
	public static void i(String msg) {
		if(PRINT_LOG) {
			Log.i(TAG, msg);
		}
	}
	
	public static void e(String msg) {
		if(PRINT_LOG) {
			Log.e(TAG, msg);
		}
	}

	//这里最好设一个标志位，用来设置日志的打印与不打印
	//网上代码（第一行代码
//	public static final int VERBOSE = 1;
//	public static final int DEBUG = 2;
//	public static final int INFO = 3;
//	public static final int WARN = 4;
//	public static final int ERROR = 5;
//	public static final int NOTHING = 6;
//	public static final int LEVEL = VERBOSE;
//	
//	public static void v(String tag, String msg) {
//		if (LEVEL <= VERBOSE) {
//			Log.v(tag, msg);
//		}
//	}
//	public static void d(String tag, String msg) {
//		if (LEVEL <= DEBUG) {
//			Log.d(tag, msg);
//		}
//	}
//	public static void i(String tag, String msg) {
//		if (LEVEL <= INFO) {
//			Log.i(tag, msg);
//		}
//	}
//	public static void w(String tag, String msg) {
//		if (LEVEL <= WARN) {
//			Log.w(tag, msg);
//		}
//	}
//	public static void e(String tag, String msg) {
//		if (LEVEL <= ERROR) {
//			Log.e(tag, msg);
//		}
//	}
}