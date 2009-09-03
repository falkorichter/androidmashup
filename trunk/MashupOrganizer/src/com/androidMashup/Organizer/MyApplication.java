package com.androidMashup.Organizer;

import java.util.ArrayList;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.androidMashup.Organizer.Logic.DatabaseHandler;
import com.androidMashup.Organizer.Logic.MashupDbAdapter;
import com.androidMashup.provider.MashupProvider;

public class MyApplication extends Application implements Handler.Callback {
	
	private static final int			MESSAGE_REFRESH_CHILDREN	= 0;
	public Cursor						installedAppsCursor			= null;
	public Cursor						availableAppsCursor			= null;
	public Cursor						enabledIntentsCursor		= null;
	private MashupDbAdapter				dbAdapter;
	public Cursor						availableIntentsCursor;
	
	private final Runnable				refreshRunner				= new Runnable() {
																		public void run() {
																			refreshViews();
																			Toast
																					.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT)
																					.show();
																		}
																	};
	private final Runnable				beforeRequestRunner			= new Runnable() {
																		public void run() {
																			beforeRequest();
																		}
																		
																	};
	
	private DatabaseHandler				handler;
	private Thread						runner;
	
	public ArrayList<IMashupActivity>	registeredActivities;
	
	private Handler						threadHandler;
	public boolean						firstStartup;
	
	private void beforeRequest() {
		for ( IMashupActivity activity : registeredActivities ) {
			activity.beforeRequest();
		}
	}
	
	public void clearDatabase() {
		runner = new Thread() {
			@Override
			public void run() {
				try {
					threadHandler.post(beforeRequestRunner);
					handler.clearDataBase();
					enabledIntentsCursor.requery();
					installedAppsCursor.requery();
					availableAppsCursor.requery();
					availableIntentsCursor.requery();
					
				}
				catch (Exception e) {
				}
				finally {
					threadHandler.post(refreshRunner);
				}
			}
		};
		runner.start();
	}
	
	public void disableIntent(final long intentId) {
		// runner = new Thread() {
		// @Override
		// public void run() {
		// try {
		dbAdapter.open();
		ContentValues values = new ContentValues();
		values.put(MashupProvider.INTENT_ENABLED, 0);
		dbAdapter.update(MashupDbAdapter.DATABASE_INTENTS_TABLE, values, MashupProvider.INTENT_KEY_ROWID + "="
																			+ intentId, null);
		availableIntentsCursor.requery();
		enabledIntentsCursor.requery();
		//					
		// }
		// catch (Exception e) {
		// }
		// finally {
		threadHandler.post(refreshRunner);
		// }
		// }
		// };
		// runner.start();
	}
	
	public void enableIntent(final long intentId) {
		// runner = new Thread() {
		// @Override
		// public void run() {
		// try {
		dbAdapter.open();
		ContentValues values = new ContentValues();
		values.put(MashupProvider.INTENT_ENABLED, 1);
		dbAdapter.update(MashupDbAdapter.DATABASE_INTENTS_TABLE, values, MashupProvider.INTENT_KEY_ROWID + "="
																			+ intentId, null);
		availableIntentsCursor.requery();
		enabledIntentsCursor.requery();
		//					
		// }
		// catch (Exception e) {
		// }
		// finally {
		threadHandler.post(refreshRunner);
		// }
		// }
		// };
		// runner.start();
	}
	
	public int findInstalledApps() {
		// runner = new Thread() {
		// @Override
		// public void run() {
		// try {
		// threadHandler.post(beforeRequestRunner);
		handler.findInstalledApps(getPackageManager());
		installedAppsCursor.requery();
		availableAppsCursor.requery();
		refreshViews();
		//					
		// }
		// catch (Exception e) {
		// }
		// finally {
		// threadHandler.post(refreshRunner);
		// }
		// }
		// };
		// runner.start();
		return 0;
	}
	
	public void firstStartupDismissed() {
		SharedPreferences settings = getSharedPreferences(Config.PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(Config.PREF_FIRST_STARTUP, false);
		editor.commit();
	}
	
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case MESSAGE_REFRESH_CHILDREN:
				refreshViews();
				return true;
		}
		return false;
	}
	
	public void initAvailableAppsCursor() {
		String query = MashupProvider.APPLICATION_INSTALLED + "='0'";
		availableAppsCursor = dbAdapter
				.query(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE, null, query, null, null, null, null, null);
	}
	
	public void initAvailableIntentsCursor() {
		String query = MashupProvider.INTENT_ENABLED + "='0'";
		availableIntentsCursor = dbAdapter
				.query(MashupDbAdapter.DATABASE_INTENTS_TABLE, null, query, null, null, null, null, null);
	}
	
	public int initDatabase() {
		runner = new Thread() {
			@Override
			public void run() {
				try {
					threadHandler.post(beforeRequestRunner);
					handler.initDataBase();
					handler.findInstalledApps(getPackageManager());
					enabledIntentsCursor.requery();
					installedAppsCursor.requery();
					availableAppsCursor.requery();
					availableIntentsCursor.requery();
				}
				catch (Exception e) {
				}
				finally {
					threadHandler.post(refreshRunner);
				}
			}
		};
		runner.start();
		return 0;
	}
	
	public void initEnabledIntentsCursor() {
		String query = MashupProvider.INTENT_ENABLED + "='1'";
		enabledIntentsCursor = dbAdapter
				.query(MashupDbAdapter.DATABASE_INTENTS_TABLE, null, query, null, null, null, null, null);
		
	}
	
	public void initInstalledAppsCursor() {
		String query = MashupProvider.APPLICATION_INSTALLED + "='1'";
		installedAppsCursor = dbAdapter
				.query(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE, null, query, null, null, null, null, null);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dbAdapter = MashupDbAdapter.getInstance(getApplicationContext());
		
		handler = DatabaseHandler.getInstance(this);
		
		registeredActivities = new ArrayList<IMashupActivity>();
		
		threadHandler = new Handler(this);
		
		dbAdapter.open();
		
		updateDataBase();
		
		SharedPreferences settings = getSharedPreferences(Config.PREFERENCES, Context.MODE_PRIVATE);
		firstStartup = settings.getBoolean(Config.PREF_FIRST_STARTUP, true);
		
		initAvailableAppsCursor();
		initAvailableIntentsCursor();
		initEnabledIntentsCursor();
		initInstalledAppsCursor();
		
	}
	
	@Override
	public void onTerminate() {
		dbAdapter.close();
		super.onTerminate();
	}
	
	private void refreshViews() {
		for ( IMashupActivity registeredActivitiy : registeredActivities ) {
			registeredActivitiy.refreshState();
		}
	}
	
	public int updateDataBase() {
		
		runner = new Thread() {
			@Override
			public void run() {
				try {
					threadHandler.post(beforeRequestRunner);
					handler.updateDataBase();
					handler.findInstalledApps(getPackageManager());
					enabledIntentsCursor.requery();
					installedAppsCursor.requery();
					availableAppsCursor.requery();
					availableIntentsCursor.requery();
				}
				catch (Exception e) {
				}
				finally {
					threadHandler.post(refreshRunner);
					
				}
			}
		};
		runner.start();
		return 0;
	}
}