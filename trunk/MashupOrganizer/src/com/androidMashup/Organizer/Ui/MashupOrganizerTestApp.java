package com.androidMashup.Organizer.Ui;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidMashup.Organizer.Config;
import com.androidMashup.Organizer.R;
import com.androidMashup.Organizer.Logic.DatabaseHandler;
import com.androidMashup.Organizer.Logic.MashupDbAdapter;
import com.androidMashup.provider.MashupProvider;

public class MashupOrganizerTestApp extends Activity implements OnClickListener {
	
	private Button				connectButton;
	private Button				clearButton;
	private Button				initButton;
	private Button				loadButton;
	private Button				mashupButton;
	private Button				clearConsoleButton;
	private Button				findApps;
	private Button				getFromContentProvider;
	private Button				getDirectly;
	private EditText			intent_edit;
	private TextView			console;
	private DatabaseHandler		handler;
	private PackageManager		mPackageManager;
	private Intent				queryIntent;
	private SharedPreferences	prefs;
	
	public void onClick(View view) {
		try {
			int viewId = view.getId();
			switch (viewId) {
				case R.id.ClearButton:
					handler.clearDataBase();
					break;
				case R.id.InitButton:
					console.setText(console.getText() + "\n" + handler.initDataBase() + " items inserted");
					break;
				case R.id.UpdateButton:
					console.setText(console.getText() + "\n" + handler.updateDataBase() + " items inserted");
					break;
				
				case R.id.FindInstalledApps:
					int appsCount = handler.findInstalledApps(getPackageManager());
					console.setText(console.getText() + "\n found " + appsCount + " apps that are installed");
					break;
				case R.id.GetFromContentProvider:

					// Form an array specifying which columns to return.
					String[] projection = new String[] { MashupProvider.APPLICATION_NAME, MashupProvider.APPLICATION_PACKAGE };
					
					// Get the base URI for the People table in the Contacts
					// content provider.
					Uri aaplications = MashupProvider.CONTENT_APPLICATION;
					
					// Make the query.
					Cursor managedCursor = managedQuery(aaplications, projection, null, null, null);
					startManagingCursor(managedCursor);
					
					// managedCursor.close();
					
					// managedCursor = getContentResolver().query(intents,
					// projection, null, null, null);
					
					console.setText(console.getText() + "\n" + managedCursor.getCount() + "items found");
					
					if (managedCursor.getCount() == 0) break;
					
					while (!managedCursor.isLast()) {
						managedCursor.moveToNext();
						
						for ( int i = 0 ; i < managedCursor.getColumnCount() ; i++ ) {
							console.setText(console.getText() + managedCursor.getString(i) + " ");
						}
						console.setText(console.getText() + "\n");
					}
					
					break;
				case R.id.GetFromDirect:

					MashupDbAdapter db = MashupDbAdapter.getInstance(getApplicationContext());
					db.open();
					managedCursor = db
							.query(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE, null, null, null, null, null, null, null);
					
					console.setText(console.getText() + "\n" + managedCursor.getCount() + "items found");
					
					if (managedCursor.getCount() == 0) break;
					
					while (!managedCursor.isLast()) {
						managedCursor.moveToNext();
						
						for ( int i = 0 ; i < managedCursor.getColumnCount() ; i++ ) {
							console.setText(console.getText() + managedCursor.getString(i) + " ");
						}
						console.setText(console.getText() + "\n");
					}
					db.close();
					
					break;
				case R.id.LoadApps:

					mPackageManager = getPackageManager();
					queryIntent = new Intent();
					queryIntent.setAction(intent_edit.getText().toString());
					// queryIntent.setClassName("com.fon.fonMaps", "FONMaps");
					
					List<ResolveInfo> list = mPackageManager.queryIntentActivities(queryIntent, 0);
					
					if (list.size() == 0) {
						Toast.makeText(this, "no apps for this intent", Toast.LENGTH_LONG).show();
					}
					else {
						for ( ResolveInfo resolveInfo : list ) {
							console.setText(console.getText() + "\n" + resolveInfo.activityInfo.packageName);
							console.setText(console.getText() + "\n" + resolveInfo.loadLabel(mPackageManager));
							console.setText(console.getText() + "\n" + resolveInfo.activityInfo.name);
							console.setText(console.getText() + "\n"
											+ resolveInfo.activityInfo.applicationInfo.packageName + "\n");
						}
					}
					break;
				case R.id.MashupButton:
					mPackageManager = getPackageManager();
					queryIntent = new Intent(intent_edit.getText().toString());
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
					if (finalList.size() == 0) {
						Toast.makeText(this, "no other app installed that supports this Mashup", Toast.LENGTH_LONG)
								.show();
					}
					else {
						String[] items = apps.toArray(new String[apps.size()]);
						
						AlertDialog alert = new AlertDialog.Builder(this).setTitle("Mashup this map!")
								.setItems(items, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										ResolveInfo clickedApp = finalList.get(which);
										Intent intent = new Intent();
										intent
												.setComponent(new ComponentName(clickedApp.activityInfo.applicationInfo.packageName, clickedApp.activityInfo.name));
										startActivity(intent);
									}
								}).setIcon(R.drawable.diagona069).create();
						alert.show();
					}
					break;
				case R.id.ClearConsole:
					console.setText("test \n");
					break;
				default:
					break;
			}
		}
		catch (IOException e) {
			Toast.makeText(this, "no network connection", Toast.LENGTH_LONG).show();
		}
		catch (Exception e) {
			/* Display any Error to the GUI. */
			console.setText(console.getText() + "\n" + "Error: " + e.getMessage() + "\n\n");
			// Log.e( e.getMessage());
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_test_app);
		
		prefs = getSharedPreferences(Config.USER_PREFERENCE, Activity.MODE_PRIVATE);
		String consoleText = prefs.getString(Config.PREF_CONSOLE_TEXT, "Console");
		
		connectButton = (Button) findViewById(R.id.ClearButton);
		connectButton.setOnClickListener(this);
		
		mashupButton = (Button) findViewById(R.id.MashupButton);
		mashupButton.setOnClickListener(this);
		
		clearButton = (Button) findViewById(R.id.InitButton);
		clearButton.setOnClickListener(this);
		
		initButton = (Button) findViewById(R.id.UpdateButton);
		initButton.setOnClickListener(this);
		
		findApps = (Button) findViewById(R.id.FindInstalledApps);
		findApps.setOnClickListener(this);
		
		loadButton = (Button) findViewById(R.id.LoadApps);
		loadButton.setOnClickListener(this);
		
		getDirectly = (Button) findViewById(R.id.GetFromDirect);
		getDirectly.setOnClickListener(this);
		
		clearConsoleButton = (Button) findViewById(R.id.ClearConsole);
		clearConsoleButton.setOnClickListener(this);
		
		getFromContentProvider = (Button) findViewById(R.id.GetFromContentProvider);
		getFromContentProvider.setOnClickListener(this);
		
		console = (TextView) findViewById(R.id.TextView);
		console.setText(consoleText);
		
		intent_edit = (EditText) findViewById(R.id.Intent_EditText);
		
		try {
			handler = DatabaseHandler.getInstance(this);
		}
		catch (InvalidClassException e) {
			Toast
					.makeText(getApplicationContext(), getText(R.string.toast_somethingWentTerriblyWrong), Toast.LENGTH_LONG);
			finish();
		}
	}
	
	@Override
	protected void onPause() {
		Editor editor = prefs.edit();
		editor.putString(Config.PREF_CONSOLE_TEXT, console.getText().toString());
		editor.commit();
		super.onPause();
		
	}
	
}
