package com.androidMashup.Organizer;

import java.net.MalformedURLException;
import java.net.URL;

public class Config {
	static public URL			MASHUP_DATA_URL;
	static {
		try {
			MASHUP_DATA_URL = new URL("http://androidmashup.org/xml");
		}
		catch (MalformedURLException e) {
		}
	}
	
	public static final String	USER_PREFERENCE		= "USER_PREFERENCES";
	
	public static final String	PREF_CONSOLE_TEXT	= "PREF_CONSOLE";
	
	public static final String	PREF_FIRST_STARTUP	= "PREF_FIRST_STARTUP";
	
	public static final String	PREFERENCES			= "MASHUP_PREFERENCES";
	
}
