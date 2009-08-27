package com.androidMashup.Organizer;

import java.net.MalformedURLException;
import java.net.URL;


public class Config {
	static public URL MASHUP_DATA_URL;
	static{
		try {
			MASHUP_DATA_URL = new URL("http://androidmashup.org/xml");
		}
		catch (MalformedURLException e) {		}
	}
	
}
