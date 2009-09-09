package com.androidMashup.provider;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.androidMashup.Organizer.Logic.MashupDbAdapter;

public class MashupProvider extends ContentProvider {

	public static final String		APPLICATION_APK_URL			= "apk_url";
	public static final String		APPLICATION_DEVELOPER_EMAIL	= "developerEmail";
	public static final String		APPLICATION_DEVELOPER_URL	= "developerUrl";
	public static final String		APPLICATION_ICON			= "icon";
	public static final String		APPLICATION_INSTALLED		= "installed";
	public static final String		APPLICATION_KEY_ROWID		= "_id";
	public static final String		APPLICATION_NAME			= "name";
	public static final String		APPLICATION_PACKAGE			= "applicationPackage";
	public static final String		APPLICATION_URL				= "url";
	public static final String		APPLICATION_WEB_ID			= "_webId";
	public static final String		APPLICATION_DESCRIPTION		= "description";
	public static final String		APPLICATION_ACTIVITY_CLASS	= "activityClass";

	public static final String		INTENT_ACTION				= "action";
	public static final String		INTENT_DESCRIPTION			= "description";
	public static final String		INTENT_ENABLED				= "enabled";
	public static final String		INTENT_ICON					= "icon";
	public static final String		INTENT_KEY_ROWID			= "_id";
	public static final String		INTENT_TITLE				= "title";
	public static final String		INTENT_WEB_ID				= "_webId";
	public static final String		INTENT_APPLICATIONCOUNT		= "applicationCount";

	public static final String		RELATION_APPLICATION_WEB_ID	= "application_webId";
	public static final String		RELATION_INTENT_WEB_ID		= "intent_webId";
	public static final String		RELATION_KEY_ROWID			= "_id";
	public static final String		RELATION_MASHUP_ENABLED		= "mashupEnabled";

	// FIXME make private again
	public static final String		DATABASE_APPLICATIONS_TABLE	= "applications";
	public static final String		DATABASE_INTENTS_TABLE		= "intents";
	public static final String		DATABASE_RELATION_TABLE		= "intents_applications";

	public static final Uri			CONTENT_URI					= Uri
																		.parse("content://com.mashup.mashupdataprovider");
	public static final Uri			CONTENT_INTENT				= Uri
																		.parse("content://com.mashup.mashupdataprovider/intent");
	public static final Uri			CONTENT_APPLICATION			= Uri
																		.parse("content://com.mashup.mashupdataprovider/application");
	private MashupDbAdapter			mDbAdapter;

	private static final int		SINGLE_INTENT				= 1;
	private static final int		ALL_INTENTS					= 2;
	private static final int		SINGLE_APPLICATION			= 3;
	private static final int		ALL_APPLICATIONS			= 4;

	private static final UriMatcher	uriMatcher;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.mashup.mashupdataprovider", "intent",
				ALL_INTENTS);
		uriMatcher.addURI("com.mashup.mashupdataprovider", "intent/#",
				SINGLE_INTENT);
		uriMatcher.addURI("com.mashup.mashupdataprovider", "application",
				ALL_APPLICATIONS);
		uriMatcher.addURI("com.mashup.mashupdataprovider", "application/#",
				SINGLE_APPLICATION);
	}

	public MashupProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ALL_APPLICATIONS:
			return "vnd.androidMashup.cursor.dir/vnd.androidMashup.mashupApplication";
		case SINGLE_APPLICATION:
			return "vnd.androidMashup.cursor.item/vnd.androidMashup.mashupApplication";
		case ALL_INTENTS:
			return "vnd.androidMashup.cursor.dir/vnd.androidMashup.mashupIntent";
		case SINGLE_INTENT:
			return "vnd.androidMashup.cursor.item/vnd.androidMashup.mashupIntent";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	public void notifyChange(Uri uri, ContentObserver observer) {
		// TODO implement on update
	}

	@Override
	public boolean onCreate() {
		mDbAdapter = MashupDbAdapter.getInstance(getContext());
		mDbAdapter.open();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();

		Cursor c = null;

		List<String> arguments = uri.getPathSegments();
		if (arguments.size() < 2) {
			throw new IllegalArgumentException(
					"you must specify the intent action and optional the application package that should be exluded");
		}

		if (arguments.get(0).equals("application")) {
			String intentAction = arguments.get(1);

			String query = "SELECT "

			+ DATABASE_APPLICATIONS_TABLE + "." + APPLICATION_KEY_ROWID + ", "
					+ DATABASE_APPLICATIONS_TABLE + "." + APPLICATION_PACKAGE
					+ ", " + DATABASE_APPLICATIONS_TABLE + "."
					+ APPLICATION_ACTIVITY_CLASS + ", "
					+ DATABASE_APPLICATIONS_TABLE + "." + APPLICATION_NAME
					+ " "

					+ "FROM " + DATABASE_APPLICATIONS_TABLE

					+ " INNER JOIN " + DATABASE_RELATION_TABLE

					+ " ON " + DATABASE_RELATION_TABLE + "."
					+ RELATION_APPLICATION_WEB_ID

					+ " = " + DATABASE_APPLICATIONS_TABLE + "."
					+ APPLICATION_WEB_ID + " " +

					"INNER JOIN " + DATABASE_INTENTS_TABLE

					+ " ON " + DATABASE_RELATION_TABLE + "."
					+ RELATION_INTENT_WEB_ID

					+ " = " + DATABASE_INTENTS_TABLE + "." + INTENT_WEB_ID

					+ " WHERE " + DATABASE_INTENTS_TABLE + "." + INTENT_ACTION
					+ "='" + intentAction + "' "

					+ "AND " + DATABASE_APPLICATIONS_TABLE + "."
					+ APPLICATION_INSTALLED + "=1 "

					+ "AND " + DATABASE_RELATION_TABLE + "."
					+ RELATION_MASHUP_ENABLED + "=1 ";

			if (arguments.size() > 2) {
				query += "AND " + DATABASE_APPLICATIONS_TABLE + "."
						+ APPLICATION_PACKAGE + "!='" + arguments.get(2) + "'";
			}

			c = mDbAdapter.rawQuery(query, null);
		}

		// switch (uriMatcher.match(uri)) {
		// case ALL_APPLICATIONS:
		// qBuilder.setTables(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE);
		// qBuilder.appendWhere(APPLICATION_INSTALLED + "='1'");
		// break;
		// case SINGLE_APPLICATION:
		// qBuilder.setTables(MashupDbAdapter.DATABASE_APPLICATIONS_TABLE);
		// qBuilder.appendWhere(APPLICATION_INSTALLED + "='1'");
		// break;
		// case ALL_INTENTS:
		// qBuilder.setTables(MashupDbAdapter.DATABASE_INTENTS_TABLE);
		// break;
		// case SINGLE_INTENT:
		// qBuilder.setTables(MashupDbAdapter.DATABASE_INTENTS_TABLE);
		// break;
		// }
		// c = qBuilder.query(mDbAdapter.getReadableDatabase(), projection,
		// selection, selectionArgs, null, null, sortOrder);

		return c;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
