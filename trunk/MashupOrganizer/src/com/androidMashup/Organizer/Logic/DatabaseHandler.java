package com.androidMashup.Organizer.Logic;

import java.io.InvalidClassException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.SQLException;

import com.androidMashup.Organizer.Config;
import com.androidMashup.Organizer.DataTypes.MashupApplication;
import com.androidMashup.Organizer.DataTypes.MashupIntent;

public class DatabaseHandler {
	
	public final static int			MODE_UPDATE	= 1;
	public final static int			MODE_INIT	= 2;
	
	private final MashupDbAdapter	db;
	private URL						url			= null;
	private SAXParser				sp			= null;
	private final XMLReader			xr;
	private MashupXmlHandler		xmlHandler;
	private static DatabaseHandler	instance	= null;
	
	public static DatabaseHandler getInstance(Context ctx) throws InvalidClassException {
		if (instance == null) {
			instance = new DatabaseHandler(ctx);
		}
		
		return instance;
	}
	
	private DatabaseHandler(Context ctx) throws InvalidClassException {
		db = MashupDbAdapter.getInstance(ctx);
		url = Config.MASHUP_DATA_URL;
		/* Get a SAXParser from the SAXPArserFactory. */
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			sp = spf.newSAXParser();
			/* Get the XMLReader of the SAXParser we created. */
			try {
				xr = sp.getXMLReader();
			}
			catch (SAXException e) {
				throw new InvalidClassException("The SAXParser could not return its XMLReader ");
			}
		}
		catch (Exception e) {
			throw new InvalidClassException("The SAXParserFactory could not return an Instance of SAXParser ");
		}
	}
	
	public void clearDataBase() throws Exception {
		db.open();
		db.clearDataBase();
		db.close();
	}
	
	public int findInstalledApps(PackageManager packageManager) {
		ArrayList<MashupIntent> intents = getIntents();
		if (intents.size() == 0) return 0;
		db.open();
		int installedAppsCount = 0;
		for ( MashupIntent mashupIntent : intents ) {
			Intent queryIntent = new Intent();
			queryIntent.setAction(mashupIntent.action);
			
			List<ResolveInfo> list = packageManager.queryIntentActivities(queryIntent, 0);
			for ( ResolveInfo resolveInfo : list ) {
				installedAppsCount += db.markAsInstalled(resolveInfo.activityInfo.packageName, resolveInfo
						.loadLabel(packageManager).toString());
			}
		}
		db.close();
		return installedAppsCount;
		
	}
	
	public ArrayList<MashupApplication> getApplications() {
		ArrayList<MashupApplication> applications = null;
		try {
			db.open();
			applications = db.getAllApplications();
		}
		finally {
			db.close();
		}
		db.close();
		return applications;
	}
	
	public ArrayList<MashupIntent> getIntents() {
		ArrayList<MashupIntent> intents = null;
		try {
			db.open();
			intents = db.getAllIntents();
		}
		finally {
			db.close();
		}
		
		return intents;
	}
	
	public int initDataBase() throws Exception {
		// catch SQLException on the first init
		try {
			clearDataBase();
		}
		catch (SQLException e) {
		}
		
		/* Parse the xml-data from our URL. */
		xmlHandler = MashupXmlHandler.getInstance(db, MODE_INIT);
		xr.setContentHandler(xmlHandler);
		xr.parse(new InputSource(url.openStream()));
		return xmlHandler.insertCount;
	}
	
	public void updateApplicationStatus() {
		ArrayList<MashupApplication> applications = getApplications();
		for ( MashupApplication mashupApplication : applications ) {
			
		}
	}
	
	public int updateDataBase() throws Exception {
		/* Parse the xml-data from our URL. */
		xmlHandler = MashupXmlHandler.getInstance(db, MODE_UPDATE);
		xr.setContentHandler(xmlHandler);
		xr.parse(new InputSource(url.openStream()));
		return xmlHandler.insertCount;
	}
}
