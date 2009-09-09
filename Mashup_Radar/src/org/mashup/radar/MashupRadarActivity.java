package org.mashup.radar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Simple Activity wrapper that hosts a {@link RadarView}
 */
public class MashupRadarActivity extends Activity {

    private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 1000;

    private static final int MENU_MASHUP = Menu.FIRST + 3;
    private static final int MENU_METRIC = Menu.FIRST + 2;
    private static final int MENU_STANDARD = Menu.FIRST + 1;
    private static final String PREF_METRIC = "metric";
    private static final String RADAR = "radar";
    private int latE6;
    private int lonE6;
    private LocationManager mLocationManager;
    private SharedPreferences mPrefs;

    private RadarView mRadar;

    private SensorManager mSensorManager;

    private Cursor applicationCursor;

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

    private void mashup(String intentIdentifier, String dialogTitle,
	    final Intent data) {

	String urlString = "content://com.mashup.mashupdataprovider/application/"
		+ intentIdentifier + "/" + getApplication().getPackageName();

	Uri applications = Uri.parse(urlString);

	applicationCursor = getContentResolver().query(applications, null,
		null, null, null);

	Intent i;
	// if there are no apps available
	if (applicationCursor == null || applicationCursor.getCount() == 0) {
	    i = new Intent("org.mashupOrganizer.SHOW_ORGANIZER");
	    i.putExtra("mashup", intentIdentifier);
	    try {
		startActivity(i);
		Toast.makeText(this,
			"no apps installed, starting the Organizer",
			Toast.LENGTH_LONG).show();
		return;
	    } catch (Exception e) {
		// if the Organizer is not installed
		i = new Intent(Intent.ACTION_VIEW);
		i
			.setData(Uri
				.parse("market://search?q=pname:com.androidMashup.Organizer"));
		try {
		    startActivity(i);
		    Toast
			    .makeText(
				    this,
				    "You don«t have the Organizer installed, opening the Market",
				    Toast.LENGTH_LONG).show();
		    return;
		} catch (Exception ex) {
		    // if no market is installed
		    i = new Intent(Intent.ACTION_VIEW);
		    i
			    .setData(Uri
				    .parse("http://androidmashup.googlecode.com/files/Mashup-Organizer.apk"));
		    startActivity(i);
		    Toast
			    .makeText(
				    this,
				    "You don«t have the Organizer installed and no Market access, it will be downloaded directly",
				    Toast.LENGTH_LONG).show();
		    return;
		}
	    }
	}

	AlertDialog test = new AlertDialog.Builder(this)
		.setTitle(dialogTitle)
		.setCursor(applicationCursor,
			new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				applicationCursor.moveToPosition(which);
				data
					.setComponent(new ComponentName(
						applicationCursor
							.getString(applicationCursor
								.getColumnIndex("applicationPackage")),
						applicationCursor
							.getString(applicationCursor
								.getColumnIndex("activityClass"))));
				applicationCursor.close();

				try {
				    startActivity(data);
				} catch (ActivityNotFoundException e) {
				    Toast.makeText(getApplicationContext(),
					    "something went wrong",
					    Toast.LENGTH_LONG).show();
				}
			    }
			}, "name").setIcon(R.drawable.diagona069)
		.setPositiveButton("preferences",
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {
				Intent i = new Intent(
					"org.mashupOrganizer.SHOW_ORGANIZER");
				startActivity(i);

			    }
			}).setNegativeButton("done",
			new DialogInterface.OnClickListener() {

			    @Override
			    public void onClick(DialogInterface dialog,
				    int which) {

			    }
			}).create();
	test.show();
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
	    Intent intent = new Intent();
	    intent.putExtra("latitude", latE6);
	    intent.putExtra("longitude", lonE6);
	    mashup("com.androidMashup.MAP_VIEW",
		    "Mashup with this coordinate!", intent);
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