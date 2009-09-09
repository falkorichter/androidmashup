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

package org.mashup.panoramio.activities;

import org.mashup.panoramio.ImageManager;
import org.mashup.panoramio.R;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * Activity which lets the user select a search area
 */
public class Panoramio extends MapActivity implements OnClickListener {
	public static final int		MILLION				= 1000000;
	private static final String	PREF_FIRST_STARTUP	= "FIRST_STARTUP";
	private static final String	PREFERENCES			= "PREFERENCES";
	private MapController		controller;

	private Button				goButton;
	private Button				mashupButton;

	private ImageManager		mImageManager;
	private MapView				mMapView;
	private MyLocationOverlay	mMyLocationOverlay;

	/**
	 * Add zoom controls to our frame layout
	 */

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void mashup(String intentIdentifier, String dialogTitle,
			final Intent intent) {

		String urlString = "content://com.mashup.mashupdataprovider/application/"
				+ intentIdentifier + "/" + getApplication().getPackageName();

		Uri applications = Uri.parse(urlString);

		final Cursor applicationCursor = getContentResolver().query(
				applications, null, null, null, null);

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

								intent
										.setComponent(new ComponentName(
												applicationCursor
														.getString(applicationCursor
																.getColumnIndex("applicationPackage")),
												applicationCursor
														.getString(applicationCursor
																.getColumnIndex("activityClass"))));
								applicationCursor.close();

								try {
									startActivity(intent);
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

	public void onClick(View view) {
		if (view == goButton) {
			// Get the search area
			int latHalfSpan = mMapView.getLatitudeSpan() >> 1;
			int longHalfSpan = mMapView.getLongitudeSpan() >> 1;

			// Remember how the map was displayed so we can show it the same way
			// later
			GeoPoint center = mMapView.getMapCenter();
			int zoom = mMapView.getZoomLevel();
			int latitudeE6 = center.getLatitudeE6();
			int longitudeE6 = center.getLongitudeE6();

			Intent i = new Intent(this, ImageList.class);
			i.putExtra(ImageManager.ZOOM_EXTRA, zoom);
			i.putExtra(ImageManager.LATITUDE_E6_EXTRA, latitudeE6);
			i.putExtra(ImageManager.LONGITUDE_E6_EXTRA, longitudeE6);

			float minLong = ((float) (longitudeE6 - longHalfSpan)) / MILLION;
			float maxLong = ((float) (longitudeE6 + longHalfSpan)) / MILLION;

			float minLat = ((float) (latitudeE6 - latHalfSpan)) / MILLION;
			float maxLat = ((float) (latitudeE6 + latHalfSpan)) / MILLION;

			mImageManager.clear();

			// Start downloading
			mImageManager.load(minLong, maxLong, minLat, maxLat);

			// Show results
			startActivity(i);
		} else if (view == mashupButton) {
			Intent intent = new Intent();
			intent
					.putExtra("latitude", mMapView.getMapCenter()
							.getLatitudeE6());
			intent.putExtra("longitude", mMapView.getMapCenter()
					.getLongitudeE6());
			intent.putExtra("zoomLevel", mMapView.getZoomLevel());
			String mapMode = mMapView.isSatellite() ? "satelite" : mMapView
					.isTraffic() ? "traffic" : "street_view";
			intent.putExtra("mapMode", mapMode);
			mashup("com.androidMashup.MAP_VIEW", "Mashup this map!", intent);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		mImageManager = ImageManager.getInstance(this);

		FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
		goButton = (Button) findViewById(R.id.go);
		goButton.setOnClickListener(this);

		mashupButton = (Button) findViewById(R.id.mashupButton);
		mashupButton.setOnClickListener(this);

		// Add the map view to the frame
		mMapView = new MapView(this, ViewMap.MAPS_API_KEY);
		frame.addView(mMapView, new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		// Create an overlay to show current location
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableCompass();

		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.setClickable(true);
		mMapView.setEnabled(true);

		controller = mMapView.getController();

		SharedPreferences settings = getSharedPreferences(PREFERENCES,
				Context.MODE_PRIVATE);
		boolean firstStartup = settings.getBoolean(PREF_FIRST_STARTUP, true);

		if (firstStartup) {
			showInfo();
		}

		// pass the intent to the method
		onNewIntent(getIntent());

		mMapView.setBuiltInZoomControls(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 1, 2, R.string.myLocation);
		// menu 1
		SubMenu sub1 = menu.addSubMenu(3, 0, 3, R.string.possibleViews);
		sub1.add(3, 3, 1, R.string.satelite);
		sub1.add(3, 4, 2, R.string.traffic);
		// menu 2
		SubMenu sub = menu.addSubMenu(2, 0, 1, R.string.zoom);
		sub.add(2, 5, 1, R.string.zoomIn);
		sub.add(2, 6, 2, R.string.zoomOut);
		// menu 3
		menu.add(0, 9, 2, R.string.disableMyLocation);
		menu.add(0, 10, 2, R.string.info);

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case 1:
			try {
				mMyLocationOverlay.enableMyLocation();
				mMapView.getController().animateTo(
						mMyLocationOverlay.getMyLocation());
				Log.w("fonMaps", "myLocation");
				return true;
			} catch (Exception e) {
				Toast.makeText(
						this,
						"Something went wrong while location you: "
								+ e.getMessage(), Toast.LENGTH_LONG).show();
			}
			return false;
		case 3:
			mMapView.setSatellite(true);
			return true;
		case 4:
			mMapView.setSatellite(false);
			return true;
		case 5:
			controller.zoomIn();
			return true;
		case 6:
			controller.zoomOut();
			return true;
		case 9:
			mMyLocationOverlay.disableMyLocation();
			return true;
		case 10:
			showInfo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNewIntent(Intent recieve) {
		super.onNewIntent(recieve);
		int latE6 = recieve.getIntExtra("latitude", -1);
		int lonE6 = recieve.getIntExtra("longitude", -1);
		controller.setZoom(recieve.getIntExtra("zoomLevel", 15));
		controller.setCenter(new GeoPoint(latE6, lonE6));

		String mapMode = recieve.getStringExtra("mapMode");
		mapMode = mapMode == null ? "traffic" : mapMode;
		mMapView.setSatellite(mapMode.equals("satelite"));
		mMapView.setStreetView(mapMode.equals("street_view"));
		mMapView.setTraffic(mapMode.equals("traffic"));

		if ((latE6 == -1) || (lonE6 == -1)) {
			mMyLocationOverlay.enableMyLocation();
			mMyLocationOverlay.runOnFirstFix(new Runnable() {
				public void run() {
					mMapView.getController().animateTo(
							mMyLocationOverlay.getMyLocation());
				}
			});
		} else {
			mMyLocationOverlay.disableMyLocation();
			controller.setCenter(new GeoPoint(latE6, lonE6));
		}
	}

	@Override
	protected void onPause() {
		mMyLocationOverlay.disableMyLocation();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void showInfo() {

		AlertDialog installDialog = new AlertDialog.Builder(this)
				.setNegativeButton("done",
						new android.content.DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								SharedPreferences settings = getSharedPreferences(
										PREFERENCES, Context.MODE_PRIVATE);
								SharedPreferences.Editor editor = settings
										.edit();
								editor.putBoolean(PREF_FIRST_STARTUP, false);
								editor.commit();
							}
						}).setMessage(R.string.infoText).setTitle("Help")
				.setCancelable(true).create();
		installDialog.show();

	}

}
