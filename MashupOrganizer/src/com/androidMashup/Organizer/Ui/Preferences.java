package com.androidMashup.Organizer.Ui;

import android.app.Activity;
import android.os.Bundle;

import com.androidMashup.Organizer.MyApplication;
import com.androidMashup.Organizer.R;

public class Preferences extends Activity {
	private MyApplication	mainApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_preferences);
		
		mainApp = (MyApplication) getApplication();
		
	}
	
}
