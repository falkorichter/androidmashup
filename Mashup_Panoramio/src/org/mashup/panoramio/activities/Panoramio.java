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

import java.util.ArrayList;
import java.util.List;

import org.mashup.panoramio.ImageManager;
import org.mashup.panoramio.R;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
	
	private void mashup(String intentIdentifier, String dialogTitle) {
		PackageManager mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(intentIdentifier);
		List<ResolveInfo> mashupList = mPackageManager.queryIntentActivities(queryIntent, 0);
		ArrayList<String> apps = new ArrayList<String>();
		for ( int i = 0 ; i < mashupList.size() ; i++ ) {
			ResolveInfo item = mashupList.get(i);
			if (!item.activityInfo.packageName.equals(getApplication().getPackageName())) {
				apps.add(item.loadLabel(mPackageManager).toString());
			}
			else {
				mashupList.remove(i);
				i--;
			}
		}
		final List<ResolveInfo> finalList = mashupList;
		
		String[] items = apps.toArray(new String[apps.size()]);
		if (finalList.size() == 0) {
			try {
				Intent i = new Intent("org.mashupOrganizer.SHOW_ORGANIZER");
				i.putExtra("mashup", intentIdentifier);
				startActivity(i);
				Toast.makeText(this, "no apps installed, starting the Organizer", Toast.LENGTH_LONG).show();
			}
			catch (Exception e) {
				try {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse("market://search?q=mashup"));
					startActivity(i);
					Toast.makeText(this, "let´s search in the market for mashup apps", Toast.LENGTH_LONG).show();
				}
				catch (Exception ex) {
					Toast
							.makeText(this, "no other app installed and you don´t have a market installed", Toast.LENGTH_LONG)
							.show();
				}
			}
			
		}
		else {
			AlertDialog alert = new AlertDialog.Builder(this).setTitle(dialogTitle)
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ResolveInfo clickedApp = finalList.get(which);
							mMyLocationOverlay.disableMyLocation();
							Intent intent = new Intent();
							intent
									.setComponent(new ComponentName(clickedApp.activityInfo.applicationInfo.packageName, clickedApp.activityInfo.name));
							intent.putExtra("latitude", mMapView.getMapCenter().getLatitudeE6());
							intent.putExtra("longitude", mMapView.getMapCenter().getLongitudeE6());
							intent.putExtra("zoomLevel", mMapView.getZoomLevel());
							String mapMode = mMapView.isSatellite() ? "satelite" : mMapView.isTraffic() ? "traffic" : "street_view";
							intent.putExtra("mapMode", mapMode);
							startActivity(intent);
						}
					}).setIcon(R.drawable.diagona069).create();
			alert.show();
		}
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
		}
		else if (view == mashupButton) {
			mashup("com.androidMashup.MAP_VIEW", "Mashup this Map!");
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
		frame.addView(mMapView, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		// Create an overlay to show current location
		mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMyLocationOverlay.enableCompass();
		
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.setClickable(true);
		mMapView.setEnabled(true);
		
		controller = mMapView.getController();
		
		SharedPreferences settings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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
					mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
					Log.w("fonMaps", "myLocation");
					return true;
				}
				catch (Exception e) {
					Toast
							.makeText(this, "Something went wrong while location you: " + e.getMessage(), Toast.LENGTH_LONG)
							.show();
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
					mMapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
				}
			});
		}
		else {
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
				.setNegativeButton("done", new android.content.DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences settings = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(PREF_FIRST_STARTUP, false);
						editor.commit();
					}
				}).setMessage(R.string.infoText).setTitle("Help").setCancelable(true).create();
		installDialog.show();
		
	}
	
}
