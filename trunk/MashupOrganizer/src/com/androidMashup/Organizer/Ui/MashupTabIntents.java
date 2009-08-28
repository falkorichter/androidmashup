package com.androidMashup.Organizer.Ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.androidMashup.Organizer.R;
import com.androidMashup.Organizer.Logic.DatabaseHandler;
import com.androidMashup.Organizer.Logic.MashupDbAdapter;
import com.androidMashup.provider.MashupProvider;

public class MashupTabIntents extends Activity implements OnClickListener, OnItemClickListener,
		android.content.DialogInterface.OnClickListener {
	
	private ListView			enabledIntentsListView;
	private ListView			availableIntentsListView;
	private Button				updateListButton;
	private Button				installButton;
	private DatabaseHandler		handler;
	private Cursor				installedIntents;
	private Cursor				availableIntents;
	private MashupDbAdapter		db;
	private SimpleCursorAdapter	installedIntentsAdapter;
	private SimpleCursorAdapter	availableIntentsAdapter;
	private Dialog				descriptionIntentDialog;
	private Builder				alertBuilder;
	private String				intentAction;
	private String				intentTitle;
	private AlertDialog			enableDialog;
	
	private void getAvailableIntents() {
		String query = " " + MashupProvider.INTENT_ENABLED + "='0' ";
		query = "";
		availableIntents = db.query(MashupDbAdapter.DATABASE_INTENTS_TABLE, null, query, null, null, null, null, null);
	}
	
	private void getEnabledIntents() {
		String query = " " + MashupProvider.INTENT_ENABLED + "='1' ";
		query = "";
		installedIntents = db.query(MashupDbAdapter.DATABASE_INTENTS_TABLE, null, query, null, null, null, null, null);
	}
	
	private void mashup(String intentAction2, String title) {
		PackageManager mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(intentAction2);
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
			Toast.makeText(this, "no other app installed that supports this Mashup", Toast.LENGTH_LONG).show();
		}
		else {
			String[] items = apps.toArray(new String[apps.size()]);
			
			AlertDialog alert = new AlertDialog.Builder(this).setTitle(title).setPositiveButton("cancel", this)
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
		
	}
	
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == descriptionIntentDialog && which == DialogInterface.BUTTON_POSITIVE && intentAction != null) {
			mashup(intentAction, intentTitle);
		}
	}
	
	public void onClick(View v) {
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_organizer_tabs_intent);
		
		updateListButton = (Button) findViewById(R.id.IntentsTab_updateList_Button);
		updateListButton.setOnClickListener(this);
		
		enabledIntentsListView = (ListView) findViewById(R.id.IntentsTab_installedIntents_ListView);
		
		availableIntentsListView = (ListView) findViewById(R.id.IntentsTab_newIntents_ListView);
		
		db = MashupDbAdapter.getInstance(getApplicationContext());
		db.open();
		alertBuilder = new AlertDialog.Builder(this);
		
		getEnabledIntents();
		getAvailableIntents();
		
		String[] from1 = new String[] { MashupProvider.INTENT_TITLE };
		int[] to1 = { R.id.installedIntent_Name };
		installedIntentsAdapter = new SimpleCursorAdapter(this, R.layout.installed_intents_view, installedIntents, from1, to1);
		enabledIntentsListView.setAdapter(installedIntentsAdapter);
		enabledIntentsListView.setSelector(R.drawable.selection_overlay);
		enabledIntentsListView.setOnItemClickListener(this);
		
		String[] from2 = new String[] { MashupProvider.INTENT_TITLE };
		int[] to2 = { R.id.availableIntent_Name };
		availableIntentsAdapter = new SimpleCursorAdapter(this, R.layout.available_intents_view, availableIntents, from2, to2);
		availableIntentsListView.setAdapter(availableIntentsAdapter);
		availableIntentsListView.setSelector(R.drawable.selection_overlay);
		availableIntentsListView.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long arg3) {
		if (adapter == enabledIntentsListView) {
			availableIntents.moveToPosition(pos);
			
			CharSequence intentDescripttion = availableIntents.getString(availableIntents
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			intentTitle = availableIntents.getString(availableIntents.getColumnIndex(MashupProvider.INTENT_TITLE));
			intentAction = availableIntents.getString(availableIntents.getColumnIndex(MashupProvider.INTENT_ACTION));
			descriptionIntentDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle(intentTitle)
					.setPositiveButton("fire up this mashup!", this).setMessage(intentDescripttion).setCancelable(true)
					.create();
			descriptionIntentDialog.show();
		}
		else if (adapter == availableIntentsListView) {
			CharSequence intentDescripttion = availableIntents.getString(availableIntents
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			intentTitle = availableIntents.getString(availableIntents.getColumnIndex(MashupProvider.INTENT_TITLE));
			intentAction = availableIntents.getString(availableIntents.getColumnIndex(MashupProvider.INTENT_ACTION));
			enableDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle(intentTitle).setPositiveButton("enable", this)
					.setMessage(intentDescripttion).setCancelable(true).create();
			enableDialog.show();
		}
		
	}
}
