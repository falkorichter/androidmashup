package com.androidMashup.Organizer.Logic;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androidMashup.Organizer.DataTypes.MashupApplication;
import com.androidMashup.Organizer.DataTypes.MashupIntent;
import com.androidMashup.Organizer.Utils.Log;
import com.androidMashup.provider.MashupProvider;

/**
 * TODO javadoc
 */
public class MashupDbAdapter {

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void clearAllTables(SQLiteDatabase db) {
			db.execSQL("DELETE FROM " + DATABASE_APPLICATIONS_TABLE);
			db.execSQL("DELETE FROM " + DATABASE_INTENTS_TABLE);
			db.execSQL("DELETE FROM " + DATABASE_RELATION_TABLE);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE_TABLE_APPLICATIONS);
				db.execSQL(DATABASE_CREATE_TABLE_INTENTS);
				db.execSQL(DATABASE_CREATE_TABLE_RELATION);
				db.execSQL(DATABASE_CREATE_TRIGGER_APPLICATION);
				db.execSQL(DATABASE_CREATE_TRIGGER_INTENT);
			} catch (SQLException ex) {
				Log.w(ex.getMessage() + ex.getCause());
				ex.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_APPLICATIONS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_INTENTS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_RELATION_TABLE);
			onCreate(db);
		}
	}

	public static final String		RELATION_APPLICATION_WEB_ID			= "application_webId";
	public static final String		RELATION_INTENT_WEB_ID				= "intent_webId";
	public static final String		RELATION_KEY_ROWID					= "_id";

	// FIXME make private again
	public static final String		DATABASE_APPLICATIONS_TABLE			= "applications";
	public static final String		DATABASE_INTENTS_TABLE				= "intents";
	public static final String		DATABASE_RELATION_TABLE				= "intents_applications";
	private static final String		DATABASE_NAME						= "mashup.db";

	private static final int		DATABASE_VERSION					= 10;
	/**
	 * Database creation sql statement
	 */
	private static final String		DATABASE_CREATE_TABLE_APPLICATIONS	= "create table "
																				+ DATABASE_APPLICATIONS_TABLE
																				+ " ("
																				+ MashupProvider.APPLICATION_KEY_ROWID
																				+ " integer primary key autoincrement,"
																				+ MashupProvider.APPLICATION_WEB_ID
																				+ " integer unique on conflict rollback,"
																				+ MashupProvider.APPLICATION_NAME
																				+ " text not null,"
																				+ MashupProvider.APPLICATION_ICON
																				+ " text not null,"
																				+ MashupProvider.APPLICATION_APK_URL
																				+ " text null,"
																				+ MashupProvider.APPLICATION_URL
																				+ " text null,"
																				+ MashupProvider.APPLICATION_PACKAGE
																				+ " text not null,"
																				+ MashupProvider.APPLICATION_DEVELOPER_EMAIL
																				+ " text null,"
																				+ MashupProvider.APPLICATION_DESCRIPTION
																				+ " text null,"
																				+ MashupProvider.APPLICATION_ACTIVITY_CLASS
																				+ " text null,"
																				+ MashupProvider.APPLICATION_DEVELOPER_URL
																				+ " text null,"
																				+ MashupProvider.APPLICATION_INSTALLED
																				+ " integer not null default 0"
																				+ ");";
	private static final String		DATABASE_CREATE_TABLE_INTENTS		= "create table "
																				+ DATABASE_INTENTS_TABLE
																				+ " ("
																				+ MashupProvider.INTENT_KEY_ROWID
																				+ " integer primary key autoincrement,"
																				+ MashupProvider.INTENT_WEB_ID
																				+ " integer unique on conflict rollback,"
																				+ MashupProvider.INTENT_DESCRIPTION
																				+ " text null,"
																				+ MashupProvider.INTENT_ICON
																				+ " text null,"
																				+ MashupProvider.INTENT_TITLE
																				+ " text not null,"
																				+ MashupProvider.INTENT_ACTION
																				+ " text not null,"
																				+ MashupProvider.INTENT_ENABLED
																				+ " integer not null default 0"
																				+ ");";
	private static final String		DATABASE_CREATE_TABLE_RELATION		= "create table "
																				+ DATABASE_RELATION_TABLE
																				+ " ("
																				+ RELATION_KEY_ROWID
																				+ " integer primary key autoincrement,"
																				+ RELATION_APPLICATION_WEB_ID
																				+ " integer not null,"
																				+ RELATION_INTENT_WEB_ID
																				+ " integer not null);";
	private static final String		DATABASE_CREATE_TRIGGER_APPLICATION	= "CREATE TRIGGER delete_relation_application AFTER  DELETE ON "
																				+ DATABASE_APPLICATIONS_TABLE
																				+ " BEGIN "
																				+ "DELETE from "
																				+ DATABASE_RELATION_TABLE
																				+ "  WHERE "
																				+ RELATION_APPLICATION_WEB_ID
																				+ " = OLD."
																				+ MashupProvider.APPLICATION_WEB_ID
																				+ ";"
																				+ "END;"
																				+ "";

	// triggers that represent constraint ON DELETE CASACADE
	private static final String		DATABASE_CREATE_TRIGGER_INTENT		= "CREATE TRIGGER delete_relation_intent AFTER  DELETE ON "
																				+ DATABASE_INTENTS_TABLE
																				+ " BEGIN "
																				+ " DELETE from "
																				+ DATABASE_RELATION_TABLE
																				+ "  WHERE "
																				+ RELATION_INTENT_WEB_ID
																				+ " = OLD."
																				+ MashupProvider.INTENT_WEB_ID
																				+ ";"
																				+ " END;";

	private final Context			mCtx;
	private SQLiteDatabase			mDb;

	private DatabaseHelper			mDbHelper;
	private static MashupDbAdapter	instance							= null;

	public static MashupDbAdapter getInstance(Context ctx) {
		if (instance == null) {
			instance = new MashupDbAdapter(ctx);
		} else {
		}
		return instance;
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	private MashupDbAdapter(Context ctx) {
		Log.i("creating a mashupprovider");
		this.mCtx = ctx;
	}

	public void clearDataBase() {
		mDbHelper.clearAllTables(mDb);
	}

	public void close() {
		mDbHelper.close();
	}

	public boolean deleteApplication(long rowId) {
		return mDb.delete(DATABASE_INTENTS_TABLE,
				MashupProvider.APPLICATION_KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteApplicationByWebId(long webId) {
		return mDb.delete(DATABASE_INTENTS_TABLE,
				MashupProvider.APPLICATION_WEB_ID + "=" + webId, null) > 0;
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId
	 *            id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteIntent(long rowId) {
		return mDb.delete(DATABASE_INTENTS_TABLE,
				MashupProvider.INTENT_KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteIntentByWebId(long webId) {
		return mDb.delete(DATABASE_INTENTS_TABLE, MashupProvider.INTENT_WEB_ID
				+ "=" + webId, null) > 0;
	}

	public Cursor fetchAllApplications() {
		return mDb.query(DATABASE_APPLICATIONS_TABLE, null, null, null, null,
				null, null);
	}

	public Cursor fetchAllIntents() {
		return mDb.query(DATABASE_INTENTS_TABLE, null, null, null, null, null,
				null);

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId
	 *            id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException
	 *             if note could not be found/retrieved
	 */
	public Cursor fetchIntent(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_INTENTS_TABLE, null,
				MashupProvider.INTENT_KEY_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public ArrayList<MashupApplication> getAllApplications() {
		ArrayList<MashupApplication> applications = new ArrayList<MashupApplication>();
		Cursor all = fetchAllApplications();

		if (all.moveToFirst()) {

			int apkUrlColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_APK_URL);
			int developerEmailColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_DEVELOPER_EMAIL);
			int iconColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_ICON);
			int installedColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_INSTALLED);
			int idColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_KEY_ROWID);
			int nameColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_NAME);
			int packageColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE);
			int applicationUrlColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_URL);
			int applicationPackageColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_PACKAGE);
			int webIdColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_WEB_ID);
			int descriptionColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_DESCRIPTION);
			int activityClassColumn = all
					.getColumnIndex(MashupProvider.APPLICATION_ACTIVITY_CLASS);

			do {
				MashupApplication application = new MashupApplication();

				application.apkUrl = all.getString(apkUrlColumn);
				application.developerEmail = all
						.getString(developerEmailColumn);
				application.developerUrl = all.getString(iconColumn);
				application.icon = all.getString(installedColumn);
				application.installed = (all.getInt(idColumn) == 1);
				application.id = all.getLong(nameColumn);
				application.name = all.getString(packageColumn);
				application.applicationPackage = all
						.getString(applicationPackageColumn);
				application.url = all.getString(applicationUrlColumn);
				application.webId = all.getLong(webIdColumn);
				application.description = all.getString(descriptionColumn);
				application.activityClass = all.getString(activityClassColumn);

				applications.add(application);

			} while (all.moveToNext());
		}
		all.close();
		return applications;
	}

	public ArrayList<MashupIntent> getAllIntents() {
		ArrayList<MashupIntent> intents = new ArrayList<MashupIntent>();
		Cursor all = fetchAllIntents();

		if (all.moveToFirst()) {

			int actionColumn = all.getColumnIndex(MashupProvider.INTENT_ACTION);
			int descriptionColumn = all
					.getColumnIndex(MashupProvider.INTENT_DESCRIPTION);
			int enabledColumn = all
					.getColumnIndex(MashupProvider.INTENT_ENABLED);
			int idColumn = all.getColumnIndex(MashupProvider.INTENT_KEY_ROWID);
			int titleColumn = all.getColumnIndex(MashupProvider.INTENT_TITLE);
			int webIdColumn = all.getColumnIndex(MashupProvider.INTENT_WEB_ID);

			do {
				MashupIntent intent = new MashupIntent();
				intent.action = all.getString(actionColumn);
				intent.description = all.getString(descriptionColumn);
				intent.enabled = (all.getInt(enabledColumn) == 1);
				intent.id = all.getLong(idColumn);
				intent.title = all.getString(titleColumn);
				intent.webId = all.getLong(webIdColumn);
				intents.add(intent);
			} while (all.moveToNext());
		}
		all.close();
		return intents;
	}

	public SQLiteDatabase getReadableDatabase() {
		return mDbHelper.getReadableDatabase();
	}

	public long insertMashupApplication(MashupApplication app) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(MashupProvider.APPLICATION_WEB_ID, app.webId);
		initialValues.put(MashupProvider.APPLICATION_NAME, app.name);
		initialValues.put(MashupProvider.APPLICATION_ICON, app.icon);
		initialValues.put(MashupProvider.APPLICATION_APK_URL, app.apkUrl);
		initialValues.put(MashupProvider.APPLICATION_URL, app.url);
		initialValues.put(MashupProvider.APPLICATION_PACKAGE,
				app.applicationPackage);
		initialValues.put(MashupProvider.APPLICATION_DEVELOPER_EMAIL,
				app.developerEmail);
		initialValues.put(MashupProvider.APPLICATION_DEVELOPER_URL,
				app.developerUrl);
		initialValues.put(MashupProvider.APPLICATION_DESCRIPTION,
				app.description);
		initialValues.put(MashupProvider.APPLICATION_ACTIVITY_CLASS,
				app.activityClass);
		return mDb.insert(DATABASE_APPLICATIONS_TABLE, null, initialValues);
	}

	public long insertMashupIntent(MashupIntent intent) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(MashupProvider.INTENT_WEB_ID, intent.webId);
		initialValues.put(MashupProvider.INTENT_ACTION, intent.action);
		initialValues
				.put(MashupProvider.INTENT_DESCRIPTION, intent.description);
		initialValues.put(MashupProvider.INTENT_ICON, intent.icon);
		initialValues.put(MashupProvider.INTENT_TITLE, intent.title);
		return mDb.insert(DATABASE_INTENTS_TABLE, null, initialValues);
	}

	public Cursor intentsWithApplicationCount(int enabled) {

		/**
		 * SELECT intents.description , intents.icon , intents.title ,
		 * intents.action, intents.enabled, count(intents_applications._id) as
		 * applicationCount FROM intents INNER JOIN intents_applications ON
		 * intents._id = intents_applications.intent_id where intents.enabled =
		 * 1
		 */
		String sql = "SELECT "

		+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_ACTION + ","

		+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_DESCRIPTION
				+ ","

				+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_ENABLED
				+ ","

				+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_ICON
				+ ","

				+ DATABASE_INTENTS_TABLE + "."
				+ MashupProvider.INTENT_KEY_ROWID + ","

				+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_TITLE
				+ ","

				+ DATABASE_INTENTS_TABLE + "." + MashupProvider.INTENT_WEB_ID
				+ ","

				+ "count(intents_applications._id) as "
				+ MashupProvider.INTENT_APPLICATIONCOUNT

				+ "FROM " + DATABASE_INTENTS_TABLE + " INNER JOIN "
				+ DATABASE_RELATION_TABLE

				+ "ON " + DATABASE_INTENTS_TABLE + "."
				+ MashupProvider.APPLICATION_KEY_ROWID + " = "
				+ DATABASE_RELATION_TABLE + "." + RELATION_INTENT_WEB_ID;
		return mDb.rawQuery(sql, null);
	}

	public int markAsInstalled(String packageName, String name,
			String activityClass) {
		ContentValues args = new ContentValues();

		args.put(MashupProvider.APPLICATION_INSTALLED, "1");
		args.put(MashupProvider.APPLICATION_ACTIVITY_CLASS, activityClass);

		String query = MashupProvider.APPLICATION_PACKAGE + "='" + packageName
				+ "' AND " + MashupProvider.APPLICATION_NAME + "='" + name
				+ "'";
		int updates = mDb
				.update(DATABASE_APPLICATIONS_TABLE, args, query, null);
		if (updates != 0) {
			Log.i("mark as installed application with packageName: '"
					+ packageName + "' and name: " + name);
		}
		return updates;

	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw an exception to signal
	 * the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public MashupDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public Cursor provideApplications(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mDb.query(DATABASE_APPLICATIONS_TABLE, projection, selection,
				selectionArgs, null, null, null, sortOrder);
	}

	public Cursor provideIntents(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return mDb.query(DATABASE_INTENTS_TABLE, projection, selection,
				selectionArgs, null, null, null, sortOrder);
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return mDb.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy, limit);
	}

	public int update(String table, ContentValues values, String whereClause,
			String[] whereArgs) {
		return mDb.update(table, values, whereClause, whereArgs);
	}

	public int updateOrInsertMashupApplication(MashupApplication app) {
		ContentValues args = new ContentValues();

		args.put(MashupProvider.APPLICATION_WEB_ID, app.webId);
		args.put(MashupProvider.APPLICATION_NAME, app.name);
		args.put(MashupProvider.APPLICATION_ICON, app.icon);
		args.put(MashupProvider.APPLICATION_APK_URL, app.apkUrl);
		args.put(MashupProvider.APPLICATION_URL, app.url);
		args.put(MashupProvider.APPLICATION_PACKAGE, app.applicationPackage);
		args
				.put(MashupProvider.APPLICATION_DEVELOPER_EMAIL,
						app.developerEmail);
		args.put(MashupProvider.APPLICATION_DEVELOPER_URL, app.developerUrl);
		args.put(MashupProvider.APPLICATION_DESCRIPTION, app.description);
		args.put(MashupProvider.APPLICATION_ACTIVITY_CLASS, app.activityClass);

		if (mDb.update(DATABASE_APPLICATIONS_TABLE, args,
				MashupProvider.APPLICATION_WEB_ID + "=" + app.webId, null) == 0) {
			mDb.insert(DATABASE_APPLICATIONS_TABLE, null, args);
			Log.i("inserting application '" + app.name + "' with webId "
					+ app.webId);
			return 1;
		}
		Log
				.i("updating application '" + app.name + "' with webId "
						+ app.webId);
		return 0;
	}

	public int updateOrInsertMashupIntent(MashupIntent intent) {
		ContentValues args = new ContentValues();
		args.put(MashupProvider.INTENT_WEB_ID, intent.webId);
		args.put(MashupProvider.INTENT_ACTION, intent.action);
		args.put(MashupProvider.INTENT_DESCRIPTION, intent.description);
		args.put(MashupProvider.INTENT_ICON, intent.icon);
		args.put(MashupProvider.INTENT_TITLE, intent.title);

		if (mDb.update(DATABASE_INTENTS_TABLE, args,
				MashupProvider.INTENT_WEB_ID + "=" + intent.webId, null) == 0) {
			mDb.insert(DATABASE_INTENTS_TABLE, null, args);
			Log.i("inserting intent '" + intent.title + "' with webId "
					+ intent.webId);
			return 1;
		}
		Log.i("updated intent '" + intent.title + "' with webId "
				+ intent.webId);
		return 0;
	}

	public int updateOrInsertRelation(long intentId, long applicationId) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(RELATION_INTENT_WEB_ID, intentId);
		initialValues.put(RELATION_APPLICATION_WEB_ID, applicationId);

		String query = RELATION_INTENT_WEB_ID + "=" + intentId + " AND "
				+ RELATION_APPLICATION_WEB_ID + "=" + applicationId;
		Cursor entryExists = mDb.query(true, DATABASE_RELATION_TABLE,
				new String[] { RELATION_INTENT_WEB_ID,
						RELATION_APPLICATION_WEB_ID }, query, null, null, null,
				null, null);
		if (entryExists.getCount() == 0) {
			mDb.insert(DATABASE_RELATION_TABLE, null, initialValues);
			Log.i("inserting relation beetween intent(webId): '" + intentId
					+ "' and application(webId) " + applicationId);
			entryExists.close();
			return 1;
		}
		Log.i("relation beetween intent(webId): '" + intentId
				+ "' and application(webId) " + applicationId
				+ " allready exists");
		entryExists.close();
		return 0;
	}
	// public Cursor fetchApplication(long rowId) throws SQLException {
	//		
	// Cursor mCursor =
	//			
	// mDb.query(true, DATABASE_INTENTS_TABLE, new String[]
	// {APPLICATION_KEY_ROWID, APPLICATION_KEY_CLASS_NAME,
	// APPLICATION_ICON, APPLICATION_NAME, APPLICATION_URL},
	// APPLICATION_KEY_ROWID + "=" + rowId, null,
	// null, null, null, null);
	// if (mCursor != null) {
	// mCursor.moveToFirst();
	// }
	// return mCursor;
	//		
	// }

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId
	 *            id of note to update
	 * @param title
	 *            value to set note title to
	 * @param body
	 *            value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	// public boolean updateIntent(MashupIntent intent) {
	// ContentValues args = new ContentValues();
	// args.put(INTENT_ACTION, intent.getAction());
	// args.put(INTENT_DESCRIPTION, intent.getDescription());
	//		
	// return mDb.update(DATABASE_INTENTS_TABLE, args, INTENT_KEY_ROWID + "=" +
	// intent.getId(), null) > 0;
	// }
	//	
	// public boolean updateApplication(MashupApplication app) {
	// ContentValues args = new ContentValues();
	// args.put(APPLICATION_KEY_CLASS_NAME, app.getClass_name());
	// args.put(APPLICATION_NAME, app.getName());
	// args.put(APPLICATION_ICON, app.getIcon().toString());
	// args.put(APPLICATION_APK_URL, app.getApk_url().toString());
	// args.put(APPLICATION_URL, app.getUrl());
	//		
	// return mDb.update(DATABASE_INTENTS_TABLE, args, APPLICATION_KEY_ROWID +
	// "=" + app.getId(), null) > 0;
	// }

}
