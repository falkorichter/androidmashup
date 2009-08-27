/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.radar;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

/**
 * Simple Activity wrapper that hosts a {@link RadarView}
 */
public class RadarActivity extends Activity {

    private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;

    private static final int MENU_MASHUP = Menu.FIRST + 3;
    private static final int MENU_METRIC = Menu.FIRST + 2;
    private static final int MENU_STANDARD = Menu.FIRST + 1;
    private static final String PREF_METRIC = "metric";
    private static final String RADAR = "radar";;
    private int latE6;
    private int lonE6;
    private LocationManager mLocationManager;
    private SharedPreferences mPrefs;

    private RadarView mRadar;

    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.radar);
	mRadar = (RadarView) findViewById(R.id.radar);
	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

	// Metric or standard units?
	mPrefs = getSharedPreferences(RADAR, MODE_PRIVATE);
	boolean useMetric = mPrefs.getBoolean(PREF_METRIC, false);
	mRadar.setUseMetric(useMetric);

	// Read the target from our intent
	Intent recieve = getIntent();
	latE6 = recieve.getIntExtra("latitude", -1);
	lonE6 = recieve.getIntExtra("longitude", -1);
	mRadar.setTarget(latE6, lonE6);
	mRadar.setDistanceView((TextView) findViewById(R.id.distance));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	menu.add(0, MENU_STANDARD, 0, R.string.menu_standard).setIcon(
		R.drawable.ic_menu_standard).setAlphabeticShortcut('A');
	menu.add(0, MENU_METRIC, 0, R.string.menu_metric).setIcon(
		R.drawable.ic_menu_metric).setAlphabeticShortcut('C');
	menu.add(0, MENU_MASHUP, 0, R.string.mashup).setIcon(
		R.drawable.diagona069).setAlphabeticShortcut('M');
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem selectedItem) {
	switch (selectedItem.getItemId()) {
	case MENU_STANDARD: {
	    setUseMetric(false);
	    return true;
	}
	case MENU_METRIC: {
	    setUseMetric(true);
	    return true;
	}
	case MENU_MASHUP: {

	    PackageManager mPackageManager = getPackageManager();
	    Intent queryIntent = new Intent("com.androidMashup.MAP_VIEW");
	    List<ResolveInfo> mashupList = mPackageManager
		    .queryIntentActivities(queryIntent, 0);
	    ArrayList<String> apps = new ArrayList<String>();
	    for (int i = 0; i < mashupList.size(); i++) {
		ResolveInfo item = mashupList.get(i);
		if (!item.activityInfo.packageName.equals(getApplication()
			.getPackageName())) {
		    apps.add(item.loadLabel(mPackageManager).toString());
		} else {
		    mashupList.remove(i);
		    i--;
		}
	    }
	    final List<ResolveInfo> finalList = mashupList;

	    String[] items = apps.toArray(new String[apps.size()]);

	    AlertDialog alert = new AlertDialog.Builder(this).setTitle(
		    "Mashup with this coordinate!").setItems(items,
		    new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    ResolveInfo clickedApp = finalList.get(which);
			    Intent intent = new Intent();
			    intent
				    .setComponent(new ComponentName(
					    clickedApp.activityInfo.applicationInfo.packageName,
					    clickedApp.activityInfo.name));
			    intent.putExtra("latitude", latE6);
			    intent.putExtra("longitude", lonE6);
			    startActivity(intent);
			}
		    }).setIcon(R.drawable.diagona069).create();
	    alert.show();

	    return true;
	}
	}

	return super.onOptionsItemSelected(selectedItem);
    }

    @Override
    protected void onPause() {
	mSensorManager.unregisterListener(mRadar);
	mLocationManager.removeUpdates(mRadar);

	// Stop animating the radar screen
	mRadar.stopSweep();
	super.onStop();
    }

    @Override
    protected void onResume() {
	super.onResume();
	mSensorManager.registerListener(mRadar,
		SensorManager.SENSOR_ORIENTATION,
		SensorManager.SENSOR_DELAY_GAME);

	// Start animating the radar screen
	mRadar.startSweep();

	// Register for location updates
	mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
	mLocationManager.requestLocationUpdates(
		LocationManager.NETWORK_PROVIDER,
		LOCATION_UPDATE_INTERVAL_MILLIS, 1, mRadar);
    }

    private void setUseMetric(boolean useMetric) {
	SharedPreferences.Editor e = mPrefs.edit();
	e.putBoolean(PREF_METRIC, useMetric);
	e.commit();
	mRadar.setUseMetric(useMetric);
    }
}