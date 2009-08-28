package com.androidMashup.Organizer.Ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.androidMashup.Organizer.R;
import com.androidMashup.Organizer.DataTypes.MashupIntent;
import com.androidMashup.Organizer.Logic.MashupDbAdapter;
import com.androidMashup.provider.MashupProvider;

public class MashupTabApplications extends Activity implements OnClickListener, OnItemSelectedListener,
		OnItemClickListener, android.content.DialogInterface.OnClickListener {
	
	private ListView					installedApplicationsListView;
	private ListView					availableApplicationsListView;
	private Button						updateListButton;
	private Cursor						installedApps;
	private Cursor						availableApps;
	private Spinner						intentchooser;
	private MashupDbAdapter				db;
	private ArrayList<MashupIntent>		availableIntents;
	private String						applicationInformation;
	private SimpleCursorAdapter			installedAppsAdapter;
	private SimpleCursorAdapter			availableAppsAdapter;
	private AlertDialog					installDialog;
	private String						applicationPackage;
	private Dialog						showInfoDialog;
	private String						applicationName;
	private String						applicationApkUrl;
	private Dialog						installChoiceDialog;
	private Builder						alertBuilder;
	private MashupIntent				selectedIntentId;
	private static final MashupIntent	allIntentsDummy					= new MashupIntent("All Intents");
	private static final int			DIALOG_APPLICATION_INFORMATION	= 0;
	
	private void getAvailableApps() {
		// FIXME react to the selectedIntentId
		String query = " " + MashupProvider.APPLICATION_INSTALLED + "='0' ";
		query = "";
		availableApps = db
				.query(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE, null, query, null, null, null, null, null);
	}
	
	private void getInstalledApps() {
		String query = " " + MashupProvider.APPLICATION_INSTALLED + "='1' ";
		query = "";
		installedApps = db
				.query(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE, null, query, null, null, null, null, null);
	}
	
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == installDialog && which == DialogInterface.BUTTON_POSITIVE && applicationPackage != null) {
			if (applicationPackage.length() > 0) {
				if (applicationApkUrl != null && applicationApkUrl.length() > 0) {
					installChoiceDialog = alertBuilder
							.setTitle("installation method")
							// build.setIcon(R.drawable.alert_dialog_icon);
							.setNegativeButton("market", this)
							.setPositiveButton("direct", this)
							.setMessage("The developer has provided a direkt download url. You can either install directly from the url or search and install the application from the Android Market")
							.setCancelable(true).create();
					installChoiceDialog.show();
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://search?q=pname:" + applicationPackage));
				try {
					startActivity(i);
				}
				catch (ActivityNotFoundException ex) {
					Toast
							.makeText(getApplicationContext(), "there is not Market installed on your phone", Toast.LENGTH_LONG)
							.show();
				}
			}
		}
		else if (dialog == showInfoDialog && which == DialogInterface.BUTTON_POSITIVE && applicationPackage != null) {
			if (applicationPackage.length() > 0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setComponent(new ComponentName(applicationPackage, applicationName));
				try {
					startActivity(i);
				}
				catch (ActivityNotFoundException ex) {
					Toast.makeText(getApplicationContext(), "something went wrong " + applicationName
															+ " could not be started ", Toast.LENGTH_LONG).show();
				}
				
			}
		}
		else if (dialog == installChoiceDialog) {
			if (which == DialogInterface.BUTTON_NEGATIVE) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("market://search?q=pname:" + applicationPackage));
				try {
					startActivity(i);
				}
				catch (ActivityNotFoundException ex) {
					Toast
							.makeText(getApplicationContext(), "there is not Market installed on your phone", Toast.LENGTH_LONG)
							.show();
				}
			}
			else if (which == DialogInterface.BUTTON_POSITIVE) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				
				try {
					// using a URL in case to cath MalformedURLException
					i.setData(Uri.parse(new URL(applicationApkUrl).toString()));
					startActivity(i);
				}
				catch (MalformedURLException ex) {
					Toast
							.makeText(getApplicationContext(), "the url from the package was not correct", Toast.LENGTH_LONG)
							.show();
				}
				catch (ActivityNotFoundException ex) {
					Toast
							.makeText(getApplicationContext(), "cannot open the url with any application", Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	}
	
	public void onClick(View v) {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_organizer_tabs_applications);
		
		MashupDbAdapter dbAdapter = MashupDbAdapter.getInstance(getApplicationContext());
		
		updateListButton = (Button) findViewById(R.id.ApplicationsTab_updateList_Button);
		updateListButton.setOnClickListener(this);
		
		intentchooser = (Spinner) findViewById(R.id.ApplicationsTab_intentsSpinner);
		availableIntents = dbAdapter.getAllIntents();
		
		availableIntents.add(0, allIntentsDummy);
		ArrayAdapter<MashupIntent> spinnerAdapter = new ArrayAdapter<MashupIntent>(this, android.R.layout.simple_spinner_item, availableIntents);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		intentchooser.setAdapter(spinnerAdapter);
		intentchooser.setOnItemSelectedListener(this);
		
		installedApplicationsListView = (ListView) findViewById(R.id.ApplicationsTab_installedApplications_ListView);
		
		availableApplicationsListView = (ListView) findViewById(R.id.ApplicationsTab_newApplications_ListView);
		
		db = MashupDbAdapter.getInstance(getApplicationContext());
		db.open();
		
		alertBuilder = new AlertDialog.Builder(this);
		
		getInstalledApps();
		getAvailableApps();
		
		String[] from1 = new String[] { MashupProvider.APPLICATION_NAME };
		int[] to1 = { R.id.installedApplication_Name };
		installedAppsAdapter = new SimpleCursorAdapter(this, R.layout.installed_apps_view, installedApps, from1, to1);
		installedApplicationsListView.setAdapter(installedAppsAdapter);
		installedApplicationsListView.setSelector(R.drawable.selection_overlay);
		installedApplicationsListView.setOnItemClickListener(this);
		
		String[] from2 = new String[] { MashupProvider.APPLICATION_NAME };
		int[] to2 = { R.id.availableApplication_Name };
		availableAppsAdapter = new SimpleCursorAdapter(this, R.layout.available_apps_view, availableApps, from2, to2);
		availableApplicationsListView.setAdapter(availableAppsAdapter);
		availableApplicationsListView.setSelector(R.drawable.selection_overlay);
		availableApplicationsListView.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long arg3) {
		if (adapter == installedApplicationsListView) {
			installedApps.moveToPosition(pos);
			
			String name = installedApps.getString(installedApps.getColumnIndex(MashupProvider.APPLICATION_NAME));
			String description = installedApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			String developerEmail = installedApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_EMAIL));
			String url = installedApps.getString(installedApps.getColumnIndex(MashupProvider.APPLICATION_URL));
			String developerUrl = installedApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_URL));
			
			applicationInformation = "";
			applicationInformation += "Name:" + name + "\n";
			if (url.length() > 1) {
				applicationInformation += url;
			}
			applicationInformation += "\n----------------";
			applicationInformation += "\n\n" + description + "\n";
			if (developerEmail.length() > 1) {
				applicationInformation += "Developers email: " + developerEmail + "\n";
			}
			if (developerUrl.length() > 1) {
				applicationInformation += "Developers website: " + developerUrl + "\n";
			}
			
			// save the package for the intent
			applicationPackage = installedApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE));
			applicationName = name;
			
			showInfoDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle("Information about:" + name)
					.setPositiveButton("open Application", this).setMessage(applicationInformation).setCancelable(true)
					.create();
			showInfoDialog.show();
		}
		else if (adapter == availableApplicationsListView) {
			
			String name = availableApps.getString(installedApps.getColumnIndex(MashupProvider.APPLICATION_NAME));
			String description = availableApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			String developerEmail = availableApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_EMAIL));
			String url = availableApps.getString(installedApps.getColumnIndex(MashupProvider.APPLICATION_URL));
			String developerUrl = availableApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_URL));
			applicationApkUrl = availableApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_APK_URL));
			
			applicationInformation = "";
			applicationInformation += "Name:" + name + "\n";
			if (url.length() > 1) {
				applicationInformation += url + "\n";
			}
			if (applicationApkUrl.length() > 1) {
				applicationInformation += applicationApkUrl + "\n";
			}
			
			applicationInformation += "\n----------------";
			applicationInformation += "\n\n" + description + "\n";
			if (developerEmail.length() > 1) {
				applicationInformation += "Developers email: " + developerEmail + "\n";
			}
			if (developerUrl.length() > 1) {
				applicationInformation += "Developers website: " + developerUrl + "\n";
			}
			// save the package for the intent
			applicationPackage = availableApps.getString(installedApps
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE));
			
			installDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setPositiveButton("install", this).setTitle("Information about:"
																									+ name)
					.setMessage(applicationInformation).setCancelable(true).create();
			installDialog.show();
		}
	}
	
	public void onItemSelected(AdapterView<?> arg0, View v, int location, long arg3) {
		if (v.getId() == R.id.ApplicationsTab_intentsSpinner) {
			if (location > 0) {
				selectedIntentId = availableIntents.get(location);
				
			}
			else {
				selectedIntentId = null;
			}
		}
	}
	
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		db.open();
	}
}
