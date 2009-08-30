package com.androidMashup.Organizer.Ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.androidMashup.Organizer.IMashupActivity;
import com.androidMashup.Organizer.MyApplication;
import com.androidMashup.Organizer.R;
import com.androidMashup.Organizer.DataTypes.MashupIntent;
import com.androidMashup.provider.MashupProvider;

public class MashupTabApplications extends Activity implements OnClickListener, OnItemSelectedListener,
		OnItemClickListener, android.content.DialogInterface.OnClickListener, OnItemLongClickListener, IMashupActivity {
	
	private class InstalledApplicationContextMenuListener implements OnCreateContextMenuListener {
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			menu.add(Menu.NONE, CONTEXT_MENU_DEINSTALL, 1, "deinstall");
		}
	}
	
	private ListView					installedApplicationsListView;
	private ListView					availableApplicationsListView;
	private Button						updateListButton;
	
	// private Spinner intentchooser;
	private String						applicationInformation;
	private SimpleCursorAdapter			installedAppsAdapter;
	private SimpleCursorAdapter			availableAppsAdapter;
	private AlertDialog					installDialog;
	private String						applicationPackage;
	private Dialog						showInfoDialog;
	private String						applicationName;
	private String						applicationApkUrl;
	private Dialog						installChoiceDialog;
	// private ArrayAdapter<MashupIntent> spinnerAdapter;
	// private SimpleCursorAdapter spinnerAdapter2;
	private MyApplication				myApp;
	private String						applicationActivityClass;
	private int							selectedPos;
	
	private static final MashupIntent	allIntentsDummy					= new MashupIntent("All Intents");
	private static final int			DIALOG_APPLICATION_INFORMATION	= 0;
	private static final int			INSTALL_DIALOG					= 1;
	public static final int				CONTEXT_MENU_DEINSTALL			= 0;
	
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
		if (dialog == installDialog && which == DialogInterface.BUTTON_POSITIVE && applicationPackage != null) {
			if (applicationPackage.length() > 0) {
				if (applicationApkUrl != null && applicationApkUrl.length() > 0) {
					installChoiceDialog = new AlertDialog.Builder(this)
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
				i.setComponent(new ComponentName(applicationPackage, applicationActivityClass));
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
					// using a URL in case to catch MalformedURLException
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
		switch (v.getId()) {
			case R.id.ApplicationsTab_updateList_Button:
				myApp.findInstalledApps();
				refreshDrawableStates();
				break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_organizer_tabs_applications);
		
		myApp = (MyApplication) getApplication();
		myApp.registeredActivities.add(this);
		myApp.updateDataBase();
		
		updateListButton = (Button) findViewById(R.id.ApplicationsTab_updateList_Button);
		updateListButton.setOnClickListener(this);
		
		// String[] from3 = new String[] { MashupProvider.INTENT_TITLE };
		// int[] to3 = { android.R.id.text1 };
		// spinnerAdapter2 = new SimpleCursorAdapter(this,
		// android.R.layout.simple_spinner_dropdown_item,
		// myApp.enabledIntentsCursor, from3, to3);
		//		
		// intentchooser = (Spinner)
		// findViewById(R.id.ApplicationsTab_intentsSpinner);
		// intentchooser.setAdapter(spinnerAdapter2);
		// intentchooser.setOnItemSelectedListener(this);
		
		installedApplicationsListView = (ListView) findViewById(R.id.ApplicationsTab_installedApplications_ListView);
		
		availableApplicationsListView = (ListView) findViewById(R.id.ApplicationsTab_newApplications_ListView);
		
		InstalledApplicationContextMenuListener installedListener = new InstalledApplicationContextMenuListener();
		
		String[] from1 = new String[] { MashupProvider.APPLICATION_NAME };
		int[] to1 = { R.id.installedApplication_Name };
		installedAppsAdapter = new SimpleCursorAdapter(this, R.layout.installed_apps_view, myApp.installedAppsCursor, from1, to1);
		installedApplicationsListView.setAdapter(installedAppsAdapter);
		installedApplicationsListView.setSelector(R.drawable.selection_overlay);
		installedApplicationsListView.setOnItemClickListener(this);
		installedApplicationsListView.setOnCreateContextMenuListener(installedListener);
		installedApplicationsListView.setOnItemLongClickListener(this);
		
		String[] from2 = new String[] { MashupProvider.APPLICATION_NAME };
		int[] to2 = { R.id.availableApplication_Name };
		availableAppsAdapter = new SimpleCursorAdapter(this, R.layout.available_apps_view, myApp.availableAppsCursor, from2, to2);
		availableApplicationsListView.setAdapter(availableAppsAdapter);
		availableApplicationsListView.setSelector(R.drawable.selection_overlay);
		availableApplicationsListView.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> adapter, View v, int pos, long arg3) {
		if (adapter == installedApplicationsListView) {
			myApp.installedAppsCursor.moveToPosition(pos);
			
			String name = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_NAME));
			String description = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			String developerEmail = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_EMAIL));
			String url = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_URL));
			String developerUrl = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
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
			
			TextView descriptionView = new TextView(this);
			descriptionView.setText(applicationInformation);
			Linkify.addLinks(descriptionView, Linkify.ALL);
			
			// save the package for the intent
			applicationPackage = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE));
			applicationName = name;
			applicationActivityClass = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_ACTIVITY_CLASS));
			
			showInfoDialog = new AlertDialog.Builder(this)
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle("Information about:" + name)
					.setPositiveButton("open Application", this).setView(descriptionView).setCancelable(true).create();
			showInfoDialog.show();
		}
		else if (adapter == availableApplicationsListView) {
			
			String name = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_NAME));
			String description = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			String developerEmail = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_EMAIL));
			String url = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_URL));
			String developerUrl = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_URL));
			applicationApkUrl = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
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
			applicationPackage = myApp.availableAppsCursor.getString(myApp.installedAppsCursor
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE));
			
			TextView descriptionView = new TextView(this);
			descriptionView.setText(applicationInformation);
			Linkify.addLinks(descriptionView, Linkify.ALL);
			
			installDialog = new AlertDialog.Builder(this)
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setView(descriptionView).setPositiveButton("install", this)
					.setTitle("Information about:" + name).setCancelable(true).create();
			installDialog.show();
		}
	}
	
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long arg3) {
		if (adapter == installedApplicationsListView) {
			selectedPos = pos;
		}
		return false;
	}
	
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		myApp.installedAppsCursor.moveToPosition(selectedPos);
		switch (item.getItemId()) {
			case CONTEXT_MENU_DEINSTALL:
				String packageString = myApp.installedAppsCursor.getString(myApp.installedAppsCursor
						.getColumnIndex(MashupProvider.APPLICATION_PACKAGE));
				Uri packageURI = Uri.parse("package:" + packageString);
				Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
				startActivity(uninstallIntent);
				
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	private void refreshDrawableStates() {
		// intentchooser.refreshDrawableState();
		availableAppsAdapter.notifyDataSetChanged();
		installedAppsAdapter.notifyDataSetChanged();
		
		installedApplicationsListView.refreshDrawableState();
		availableApplicationsListView.refreshDrawableState();
	}
	
	public void refreshState() {
		refreshDrawableStates();
	}
}
