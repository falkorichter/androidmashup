package com.androidMashup.Organizer.Ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.androidMashup.Organizer.R;

public class MashupOrganizer extends TabActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
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
}
