package com.androidMashup.Organizer.Ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidMashup.Organizer.IMashupActivity;
import com.androidMashup.Organizer.MyApplication;
import com.androidMashup.Organizer.R;

public class Preferences extends Activity implements
		android.view.View.OnClickListener, IMashupActivity {
	private MyApplication	myApp;

	private Button			clearButton;
	private Button			findAppsButton;
	private Button			updateDataBaseButton;
	private Button			aboutButton;

	public void beforeRequest() {
		clearButton.setEnabled(false);
		updateDataBaseButton.setEnabled(false);
	}

	public void onClick(View view) {
		int viewId = view.getId();
		switch (viewId) {
		case R.id.Preferences_findInstalledApps_Button:
			int appsCount = myApp.findInstalledApps();

			break;
		case R.id.Preferences_Update_Button:
			myApp.updateDataBase();
			break;
		case R.id.Preferences_clearDatabase_Button:
			new AlertDialog.Builder(this)
					.setTitle("clear database?")
					.setMessage(
							"if you continue all data from the database will be deleted!")
					.setPositiveButton("yes delete", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							myApp.clearDatabase();
						}
					}).setNegativeButton("cancel", new OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {

						}
					}).setCancelable(true).create().show();

			break;
		case R.id.Preferences_about_Button:

			TextView descriptionView = new TextView(this);
			String applicationInformation = getResources().getString(
					R.string.preferences_about_application);
			descriptionView.setText(applicationInformation);
			Linkify.addLinks(descriptionView, Linkify.ALL);
			ScrollView scrollView = new ScrollView(this);
			scrollView.addView(descriptionView);
			// save the package for the intent

			new AlertDialog.Builder(this).setTitle("about androidmashup.org:")
					.setView(scrollView).setCancelable(true).create().show();
		default:
			break;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_preferences);

		myApp = (MyApplication) getApplication();
		myApp.registerActivity(this);

		clearButton = (Button) findViewById(R.id.Preferences_clearDatabase_Button);
		clearButton.setOnClickListener(this);

		findAppsButton = (Button) findViewById(R.id.Preferences_findInstalledApps_Button);
		findAppsButton.setOnClickListener(this);

		updateDataBaseButton = (Button) findViewById(R.id.Preferences_Update_Button);
		updateDataBaseButton.setOnClickListener(this);

		aboutButton = (Button) findViewById(R.id.Preferences_about_Button);
		aboutButton.setOnClickListener(this);

	}

	public void refreshState() {
		clearButton.setEnabled(true);
		updateDataBaseButton.setEnabled(true);
	}

}
