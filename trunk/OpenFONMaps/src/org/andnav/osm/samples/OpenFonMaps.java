// Created by plusminus on 00:23:14 - 03.10.2008
package org.andnav.osm.samples;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.controller.OpenStreetMapViewController;
import org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlay;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlayItem;
import org.andnav.osm.views.overlay.OpenStreetMapViewItemizedOverlay.OnItemTapListener;
import org.andnav.osm.views.util.OpenStreetMapRendererInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.mashup.OpenFONMaps.R;

import android.app.Activity;
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
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @author Nicolas Gramlich
 * @author Falko Richter
 */
public class OpenFonMaps extends Activity implements
		OnItemTapListener<OpenStreetMapViewOverlayItem>, OnClickListener,
		Callback {

	private static final String												API_URL				= "http://maps.fon.com/ajax/getNodes";
	private static final String												FONSPOT_FILE		= "fonspots.spots";
	private static final int												MENU_MASHUP			= Menu.FIRST;
	private static final double												MILLION				= 1000000;
	private static final String												PREF_FIRST_STARTUP	= "FIRST_STARTUP";
	private static final String												PREFERENCES			= "PREFERENCES";

	private final Runnable													beforeRequestRunner	= new Runnable() {
																									public void run() {
																										beforeRequest();
																									}

																								};
	private OpenStreetMapViewController										controller;
	private JSONException													exception;
	private Button															goButton;

	private Button															mashupButton;
	private OpenStreetMapView												mMapView;
	private OpenStreetMapViewItemizedOverlay<OpenStreetMapViewOverlayItem>	mMyLocationOverlay;

	private final Runnable													refreshRunner		= new Runnable() {
																									public void run() {
																										refreshView();
																									};
																								};
	private Thread															runner;
	private HashMap<Integer, double[]>										spots;

	private Handler															threadHandler;

	protected void beforeRequest() {
		getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
				Window.PROGRESS_VISIBILITY_ON);
	}

	private void displayFonSpots(OpenStreetMapView mOsmv2) {
		if (spots != null) {
			Iterator<double[]> iterator = spots.values().iterator();
			while (iterator.hasNext()) {
				double[] next = iterator.next();

				OpenStreetMapViewOverlayItem add = new OpenStreetMapViewOverlayItem(
						"fonspot " + next[2], "a FONspot that is online",
						new GeoPoint((int) (next[0] * MILLION),
								(int) (next[1] * MILLION)));
				mMyLocationOverlay.addItem(add);
			}
		}
	}

	private void findFonSpots(final OpenStreetMapView mOsmv2) {
		int oldSpotSize = spots.size();

		double latitudeSpan = mOsmv2.getLatitudeSpanE6();
		double longitudeSpan = mOsmv2.getLongitudeSpanE6();

		GeoPoint center = mOsmv2.getMapCenter();

		double swLat = (center.getLatitudeE6() - (latitudeSpan)) / MILLION;
		double swLng = (center.getLongitudeE6() - (longitudeSpan)) / MILLION;

		double neLat = (center.getLatitudeE6() + (latitudeSpan)) / MILLION;
		double neLng = (center.getLongitudeE6() + (longitudeSpan)) / MILLION;

		// Log.i("fonMaps","center: "+center.toString(),null);
		// Log.i("fonMaps","latspan: "+center.toString(),null);

		try {
			URL nodesURL = new URL(API_URL + "?swLat=" + swLat + "&swLng="
					+ swLng + "&neLat=" + neLat + "&neLng=" + neLng + "&zoom="
					+ 14 +
					// "&zoom=" + mMapView.getZoomLevel() +
					"&forceRefresh=0" + "&filters=2047" + "&status=1");
			// nodesURL = new
			// URL("http://maps.fon.com/ajax/getNodes?swLat=52.5116515&swLng=13.441727&neLat=52.5313925&neLng=13.469021&zoom=14&forceRefresh=0&filters=2047&status=1");

			HttpURLConnection con = (HttpURLConnection) nodesURL
					.openConnection();
			con.setRequestMethod("GET");
			con.connect();
			String jsonString = con.getHeaderField("X-JSON");

			// remove the wrong braces at the end and beginning
			jsonString = jsonString.substring(1, jsonString.length() - 1);

			JSONArray nodesArray = new JSONArray(jsonString);

			// should be "add" nodesArray.getJSONArray(1).getString(0)
			if ((!(nodesArray.getJSONArray(1).getString(0)).equals("add"))) {
				Log.w("fonMaps", "wrong format of the response it is:'"
						+ nodesArray.getJSONArray(1).getString(0) + "'", null);

				throw new IOException("wrong format of the response it is:'"
						+ nodesArray.getJSONArray(1).getString(0) + "'");
			}

			JSONArray add = nodesArray.getJSONArray(1).getJSONArray(1);

			for (int i = 0; i < add.length(); i++) {
				// add.getJSONArray(i).getJSONArray(5)

				JSONArray arrayOfNodes = null;
				try {
					if (add.getJSONArray(i).optInt(2) == 1
							|| add.getJSONArray(i).optInt(2) == 0) {
						arrayOfNodes = add.getJSONArray(i).optJSONArray(5);
					} else {
						arrayOfNodes = add.getJSONArray(i).optJSONArray(3);
						if (arrayOfNodes == null) {
							arrayOfNodes = add.getJSONArray(i).optJSONArray(2);
						}
					}
				} catch (JSONException e) {
					exception = e;
				}
				if (arrayOfNodes == null) {
					Log.w("fonMaps", "problem with this Array", exception);
					// popup.showAtCenter("No Spots at this zoom Level",mMapView);
					Toast.makeText(this, "No Spots at this zoom Level",
							Toast.LENGTH_SHORT).show();
					return;
				}

				for (int j = 0; j < arrayOfNodes.length(); j++) {
					JSONArray node = arrayOfNodes.getJSONArray(j);
					try {
						double coord[] = { node.getDouble(1),
								node.getDouble(2), node.getInt(0) };
						spots.put(node.getInt(0), coord);
					} catch (Exception e) {
						Log.w("fonMaps", "Problem with node" + node.getInt(0),
								e);
					}
				}
			}
			Log.i("fonMaps", (spots.size() - oldSpotSize)
					+ " FON Spots fetched", null);
			Toast.makeText(this,
					(spots.size() - oldSpotSize) + "FON Spots fetched",
					Toast.LENGTH_SHORT).show();
			return;

		} catch (Exception e) {
			Log
					.w(
							"fonMaps",
							"An Excpetion occured while fetching the FonSpot locations",
							e);

		}
		Log.i("fonMaps", "no Spots", null);
	}

	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
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

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ZoomIn:
			mMapView.zoomIn();
			break;
		case R.id.ZoomOut:
			mMapView.zoomOut();
			break;
		case R.id.go:
			Log.i("fonMaps", "button klicked", null);
			runner = new Thread() {
				@Override
				public void run() {
					try {
						threadHandler.post(beforeRequestRunner);
						findFonSpots(mMapView);
						displayFonSpots(mMapView);
					} catch (Exception e) {
					} finally {
						threadHandler.post(refreshRunner);

					}
				}
			};
			runner.start();
			break;
		case R.id.mashupButton:
			Intent intent = new Intent();
			intent.putExtra("latitude", mMapView.getMapCenterLatitudeE6());
			intent.putExtra("longitude", mMapView.getMapCenterLongitudeE6());
			intent.putExtra("zoomLevel", mMapView.getZoomLevel());
			mashup("com.androidMashup.MAP_VIEW", "Mashup! this map", intent);
		}
	}

	// ===========================================================
	// Constructors
	// ===========================================================
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		threadHandler = new Handler(this);

		setContentView(R.layout.main);

		FrameLayout frame = (FrameLayout) findViewById(R.id.frame);

		mMapView = new OpenStreetMapView(this, OpenStreetMapRendererInfo.MAPNIK);
		frame.addView(mMapView, new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		final ImageView ivZoomIn = (ImageView) findViewById(R.id.ZoomIn);
		ivZoomIn.setOnClickListener(this);

		final ImageView ivZoomOut = (ImageView) findViewById(R.id.ZoomOut);
		ivZoomOut.setOnClickListener(this);

		goButton = (Button) findViewById(R.id.go);
		goButton.setOnClickListener(this);

		mashupButton = (Button) findViewById(R.id.mashupButton);
		mashupButton.setOnClickListener(this);

		controller = mMapView.getController();

		try {
			ObjectInputStream ois = new ObjectInputStream(
					getApplicationContext().openFileInput(FONSPOT_FILE));
			spots = (HashMap<Integer, double[]>) ois.readObject();
			Toast
					.makeText(
							this,
							"loaded "
									+ spots.size()
									+ " fonspots, you can deltele them for better performance from the Menu",
							Toast.LENGTH_LONG);
		} catch (Exception e) {
			spots = new HashMap<Integer, double[]>();
		}

		SharedPreferences settings = getSharedPreferences(PREFERENCES,
				Context.MODE_PRIVATE);
		boolean firstStartup = settings.getBoolean(PREF_FIRST_STARTUP, true);

		if (firstStartup) {
			showInfo();
		}

		onNewIntent(getIntent());

		/* Itemized Overlay */
		{

			final ArrayList<OpenStreetMapViewOverlayItem> items = new ArrayList<OpenStreetMapViewOverlayItem>();

			/* OnTapListener for the Markers, shows a simpel Toast. */
			this.mMyLocationOverlay = new OpenStreetMapViewItemizedOverlay<OpenStreetMapViewOverlayItem>(
					this, items, getResources().getDrawable(
							R.drawable.fonspot_active), null, this);

			mMapView.getOverlays().add(this.mMyLocationOverlay);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// menu 1
		SubMenu sub1 = menu.addSubMenu(3, 0, 3, R.string.possibleViews);

		sub1.add(3, 30, 1, R.string.OSMARENDER);
		sub1.add(3, 31, 1, R.string.MAPNIK);
		sub1.add(3, 32, 1, R.string.CYCLEMAP);
		sub1.add(3, 33, 1, R.string.OPENARIELMAP);
		sub1.add(3, 34, 1, R.string.CLOUDMADESMALLTILES);
		sub1.add(3, 35, 1, R.string.CLOUDMADESTANDARDTILES);

		// menu 2
		SubMenu sub = menu.addSubMenu(2, 0, 1, R.string.zoom);
		sub.add(2, 5, 1, R.string.zoomIn);
		sub.add(2, 6, 2, R.string.zoomOut);

		menu.add(0, 8, 2, R.string.deleteDatabase);
		menu.add(0, 9, 2, R.string.info);

		return true;
	}

	public boolean onItemTap(int index, OpenStreetMapViewOverlayItem item) {
		Log.w("fonMaps", "startRadarIntent");
		OpenStreetMapViewOverlayItem selectedItem = mMyLocationOverlay
				.getItem(index);
		// Launch the radar activity (if it is installed)
		Intent i = new Intent("com.google.android.radar.SHOW_RADAR");
		i.putExtra("latitude", selectedItem.mGeoPoint.getLatitudeE6());
		i.putExtra("longitude", selectedItem.mGeoPoint.getLongitudeE6());
		try {
			startActivity(i);
		} catch (ActivityNotFoundException ex) {
			Toast.makeText(this, R.string.noRadarInstalled, Toast.LENGTH_SHORT)
					.show();
		}
		return true;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_I:
			mMapView.zoomIn();
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_O:
			mMapView.zoomOut();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		Log.e("fonMaps", item.getItemId() + " clicked");
		Intent i;
		GeoPoint location;
		switch (item.getItemId()) {
		case 30:
			mMapView.setRenderer(OpenStreetMapRendererInfo.OSMARENDER);
			return true;
		case 31:
			mMapView.setRenderer(OpenStreetMapRendererInfo.MAPNIK);
			return true;
		case 32:
			mMapView.setRenderer(OpenStreetMapRendererInfo.CYCLEMAP);
			return true;
		case 33:
			mMapView.setRenderer(OpenStreetMapRendererInfo.OPENARIELMAP);
			return true;
		case 34:
			mMapView.setRenderer(OpenStreetMapRendererInfo.CLOUDMADESMALLTILES);
			return true;
		case 35:
			mMapView
					.setRenderer(OpenStreetMapRendererInfo.CLOUDMADESTANDARDTILES);
			return true;
		case 5:
			mMapView.zoomIn();
			return true;
		case 6:
			mMapView.zoomOut();
			return true;
		case 7:
			i = new Intent(Intent.ACTION_VIEW);
			Uri u = Uri.parse("http://www.falkorichter.de/androidfeedback");
			i.setData(u);
			startActivity(i);
			return true;
		case 8:
			spots.clear();
			mMyLocationOverlay.clearItems();
			mMapView.invalidate();
			return true;
		case 9:
			showInfo();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onNewIntent(Intent recieve) {
		// TODO Auto-generated method stub
		super.onNewIntent(recieve);

		int latE6 = recieve.getIntExtra("latitude", -1);
		int lonE6 = recieve.getIntExtra("longitude", -1);
		mMapView.setZoomLevel(recieve.getIntExtra("zoomLevel", 15));

		String mapMode = recieve.getStringExtra("mapMode");
		mapMode = mapMode == null ? "traffic" : mapMode;
		if (mapMode.equals("traffic") || mapMode.equals("MAPNIK")) {
			mMapView.setRenderer(OpenStreetMapRendererInfo.MAPNIK);
		} else if (mapMode.equals("OSMARENDER")) {
			mMapView.setRenderer(OpenStreetMapRendererInfo.OSMARENDER);
		} else if (mapMode.equals("CYCLEMAP")) {
			mMapView.setRenderer(OpenStreetMapRendererInfo.CYCLEMAP);
		} else if (mapMode.equals("OPENARIELMAP") || mapMode.equals("satelite")) {
			mMapView.setRenderer(OpenStreetMapRendererInfo.OPENARIELMAP);
		} else if (mapMode.equals("CLOUDMADESMALLTILES")) {
			mMapView.setRenderer(OpenStreetMapRendererInfo.CLOUDMADESMALLTILES);
		} else if (mapMode.equals("CLOUDMADESTANDARDTILES")) {
			mMapView
					.setRenderer(OpenStreetMapRendererInfo.CLOUDMADESTANDARDTILES);
		}

		if ((latE6 == -1) || (lonE6 == -1)) {
			mMapView.setMapCenter(40417188, -3699302);
		} else {
			mMapView.setMapCenter(latE6, lonE6);
		}
	}

	@Override
	protected void onPause() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					getApplicationContext().openFileOutput(FONSPOT_FILE,
							Context.MODE_PRIVATE));
			oos.writeObject(spots);
		} catch (IOException e) {
		}
		super.onPause();
	}

	protected void refreshView() {
		mMapView.invalidate();
		getWindow().setFeatureInt(Window.FEATURE_INDETERMINATE_PROGRESS,
				Window.PROGRESS_VISIBILITY_OFF);
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
