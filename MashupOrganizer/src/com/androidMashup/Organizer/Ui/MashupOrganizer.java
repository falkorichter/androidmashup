package com.androidMashup.Organizer.Ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;

import com.androidMashup.Organizer.IMashupActivity;
import com.androidMashup.Organizer.MyApplication;
import com.androidMashup.Organizer.R;

public class MashupOrganizer extends TabActivity implements IMashupActivity {
	private MyApplication	myApp;
	
	public void beforeRequest() {
		getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		
		myApp = (MyApplication) getApplication();
		myApp.registeredActivities.add(this);
		final TabHost tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("apps").setIndicator("apps", getResources()
				.getDrawable(R.drawable.diagona069)).setContent(new Intent(this, MashupTabApplications.class)));
		
		tabHost.addTab(tabHost.newTabSpec("intents").setIndicator("intents", getResources()
				.getDrawable(R.drawable.diagona069)).setContent(new Intent(this, MashupTabIntents.class)));
		
		tabHost.addTab(tabHost.newTabSpec("preferences").setIndicator("preferences", getResources()
				.getDrawable(R.drawable.preferences)).setContent(new Intent(this, Preferences.class)));
		
		// tabHost.addTab(tabHost.newTabSpec("test").setIndicator("tests")
		// .setContent(new Intent(this, MashupOrganizerTestApp.class)));
	}
	
	public void refreshState() {
		getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS, Window.PROGRESS_VISIBILITY_OFF);
	}
}
