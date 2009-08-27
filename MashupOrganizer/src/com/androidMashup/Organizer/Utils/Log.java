package com.androidMashup.Organizer.Utils;

public class Log {
	public final static String LOGTAG = "mashup";

	public static final boolean DEBUG = true;

	public static void d(String msg) {
		android.util.Log.d(LOGTAG, msg);
	}

	public static void e(String msg) {
		android.util.Log.e(LOGTAG, msg);
	}

	public static void i(String msg) {
		android.util.Log.i(LOGTAG, msg);
	}

	public static void v(String msg) {
		android.util.Log.v(LOGTAG, msg);
	}

	public static void w(String msg) {
		android.util.Log.w(LOGTAG, msg);
	}
}
