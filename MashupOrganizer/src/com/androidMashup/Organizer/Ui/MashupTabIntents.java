package com.androidMashup.Organizer.Ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.androidMashup.Organizer.IMashupActivity;
import com.androidMashup.Organizer.MyApplication;
import com.androidMashup.Organizer.R;
import com.androidMashup.provider.MashupProvider;

public class MashupTabIntents extends Activity implements OnClickListener, OnItemClickListener,
		android.content.DialogInterface.OnClickListener, OnItemLongClickListener, IMashupActivity {
	
	private class EnabledContextMenuListener implements OnCreateContextMenuListener {
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			menu.add(Menu.NONE, CONTEXT_MENU_DISABLE, 1, "disable");
			menu.add(Menu.NONE, CONTEXT_MENU_MASHUP, 2, "initiate mashup");
			menu.add(Menu.NONE, CONTEXT_MENU_OPEN, 3, "open native mashup");
		}
	}
	
	private ListView			enabledIntentsListView;
	private ListView			availableIntentsListView;
	private Button				updateListButton;
	private SimpleCursorAdapter	enabledIntentsAdapter;
	private SimpleCursorAdapter	availableIntentsAdapter;
	private Dialog				descriptionIntentDialog;
	private Builder				alertBuilder;
	private String				intentAction;
	private String				intentTitle;
	private AlertDialog			enableDialog;
	private int					selectedPos;
	private MyApplication		myApp;
	private static final int	CONTEXT_MENU_DISABLE	= 0;
	private static final int	CONTEXT_MENU_MASHUP		= 1;
	private static final int	CONTEXT_MENU_OPEN		= 2;
	
	private void mashup(Cursor cursor) {
		intentTitle = cursor.getString(cursor.getColumnIndex(MashupProvider.INTENT_TITLE));
		intentAction = cursor.getString(cursor.getColumnIndex(MashupProvider.INTENT_ACTION));
		mashup(intentTitle, intentAction);
	}
	
	private void mashup(String title, String action) {
		PackageManager mPackageManager = getPackageManager();
		Intent queryIntent = new Intent(action);
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
	
	private void nativeMashup(Cursor cursor) {
		intentAction = cursor.getString(cursor.getColumnIndex(MashupProvider.INTENT_ACTION));
		Intent i = new Intent(intentAction);
		try {
			startActivity(i);
		}
		catch (ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(), "no applications for this mashup installed", Toast.LENGTH_LONG)
					.show();
		}
		
	}
	
	public void onClick(DialogInterface dialog, int which) {
		if (dialog == descriptionIntentDialog && which == DialogInterface.BUTTON_POSITIVE) {
			mashup(myApp.enabledIntentsCursor);
		}
		if (dialog == enableDialog && which == DialogInterface.BUTTON_POSITIVE) {
			long intentId = myApp.availableIntentsCursor.getLong(myApp.availableIntentsCursor
					.getColumnIndex(MashupProvider.APPLICATION_KEY_ROWID));
			myApp.enableIntent(intentId);
		}
		
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.IntentsTab_updateList_Button:
				myApp.updateDataBase();
				break;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_organizer_tabs_intent);
		
		myApp = (MyApplication) getApplication();
		myApp.registeredActivities.add(this);
		
		updateListButton = (Button) findViewById(R.id.IntentsTab_updateList_Button);
		updateListButton.setOnClickListener(this);
		
		enabledIntentsListView = (ListView) findViewById(R.id.IntentsTab_installedIntents_ListView);
		
		availableIntentsListView = (ListView) findViewById(R.id.IntentsTab_newIntents_ListView);
		
		alertBuilder = new AlertDialog.Builder(this);
		
		EnabledContextMenuListener enabledListener = new EnabledContextMenuListener();
		
		String[] from1 = new String[] { MashupProvider.INTENT_TITLE };
		int[] to1 = { R.id.installedIntent_Name };
		enabledIntentsAdapter = new SimpleCursorAdapter(this, R.layout.installed_intents_view, myApp.enabledIntentsCursor, from1, to1);
		enabledIntentsListView.setAdapter(enabledIntentsAdapter);
		enabledIntentsListView.setSelector(R.drawable.selection_overlay);
		enabledIntentsListView.setOnItemClickListener(this);
		enabledIntentsListView.setOnCreateContextMenuListener(enabledListener);
		enabledIntentsListView.setOnItemLongClickListener(this);
		
		String[] from2 = new String[] { MashupProvider.INTENT_TITLE };
		int[] to2 = { R.id.availableIntent_Name };
		availableIntentsAdapter = new SimpleCursorAdapter(this, R.layout.available_intents_view, myApp.availableIntentsCursor, from2, to2);
		availableIntentsListView.setAdapter(availableIntentsAdapter);
		availableIntentsListView.setSelector(R.drawable.selection_overlay);
		availableIntentsListView.setOnItemClickListener(this);
		
		refreshDrawableStates();
	}
	
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long arg3) {
		if (adapter == enabledIntentsListView) {
			myApp.enabledIntentsCursor.moveToPosition(pos);
			
			String intentDescription = myApp.enabledIntentsCursor.getString(myApp.enabledIntentsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			intentTitle = myApp.enabledIntentsCursor.getString(myApp.enabledIntentsCursor
					.getColumnIndex(MashupProvider.INTENT_TITLE));
			
			descriptionIntentDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle(intentTitle)
					.setPositiveButton("fire up this mashup!", this).setMessage(intentDescription).setCancelable(true)
					.create();
			descriptionIntentDialog.show();
		}
		else if (adapter == availableIntentsListView) {
			myApp.availableIntentsCursor.moveToPosition(pos);
			CharSequence intentDescripttion = myApp.availableIntentsCursor.getString(myApp.availableIntentsCursor
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION));
			intentTitle = myApp.availableIntentsCursor.getString(myApp.availableIntentsCursor
					.getColumnIndex(MashupProvider.INTENT_TITLE));
			enableDialog = alertBuilder
					// build.setIcon(R.drawable.alert_dialog_icon);
					.setNegativeButton("done", this).setTitle(intentTitle).setPositiveButton("enable", this)
					.setMessage(intentDescripttion).setCancelable(true).create();
			enableDialog.show();
		}
		
	}
	
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long arg3) {
		if (adapter == enabledIntentsListView) {
			selectedPos = pos;
		}
		return false;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		myApp.enabledIntentsCursor.moveToPosition(selectedPos);
		switch (item.getItemId()) {
			case CONTEXT_MENU_DISABLE:
				long intentId = myApp.enabledIntentsCursor.getLong(myApp.enabledIntentsCursor
						.getColumnIndex(MashupProvider.APPLICATION_KEY_ROWID));
				myApp.disableIntent(intentId);
				break;
			case CONTEXT_MENU_MASHUP:
				mashup(myApp.enabledIntentsCursor);
				break;
			case CONTEXT_MENU_OPEN:
				nativeMashup(myApp.enabledIntentsCursor);
				break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshDrawableStates();
	}
	
	private void refreshDrawableStates() {
		enabledIntentsAdapter.notifyDataSetChanged();
		availableIntentsAdapter.notifyDataSetChanged();
		
		availableIntentsListView.refreshDrawableState();
		enabledIntentsListView.refreshDrawableState();
		
	}
	
	public void refreshState() {
		refreshDrawableStates();
	}
	
}
