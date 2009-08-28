package com.androidMashup.Organizer.Logic;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.database.Cursor;
import android.database.SQLException;

import com.androidMashup.Organizer.DataTypes.MashupApplication;
import com.androidMashup.Organizer.DataTypes.MashupIntent;
import com.androidMashup.Organizer.Utils.Log;
import com.androidMashup.provider.MashupProvider;

public class MashupXmlHandler extends DefaultHandler {
	
	private final MashupDbAdapter	db;
	private MashupApplication		newApplication		= null;
	private MashupIntent			newIntent			= null;
	private boolean					mashupData			= false;
	private boolean					mashupIntents		= false;
	private boolean					mashupApplications	= false;
	private boolean					description			= false;
	private final ArrayList<Long>	applicationIntentRelationIds;
	private final ArrayList<Long>	applicationWebIds;
	private final ArrayList<Long>	intentWebIds;
	private int						mode;
	public int						insertCount			= 0;
	private static MashupXmlHandler	instance;
	
	public static MashupXmlHandler getInstance(MashupDbAdapter db, int mode) {
		if (instance == null) {
			instance = new MashupXmlHandler(db, mode);
		}
		else {
			instance.mode = mode;
		}
		return instance;
	}
	
	private MashupXmlHandler(MashupDbAdapter db, int mode) {
		this.db = db;
		this.mode = mode;
		applicationIntentRelationIds = new ArrayList<Long>();
		applicationWebIds = new ArrayList<Long>();
		intentWebIds = new ArrayList<Long>();
	}
	
	/**
	 * Gets be called on the following structure:
	 * <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (description && newIntent != null) {
			newIntent.description = new String(ch).substring(start, start + length);
			description = false;
		}
		if (description && newApplication != null) {
			newApplication.description = new String(ch).substring(start, start + length);
			description = false;
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		if (mode == DatabaseHandler.MODE_UPDATE) {
			Cursor databaseIntents = db.fetchAllIntents();
			Cursor databaseApplications = db.fetchAllApplications();
			
			while (databaseApplications.moveToNext()) {
				long webId = databaseApplications.getLong(databaseApplications
						.getColumnIndexOrThrow(MashupProvider.APPLICATION_WEB_ID));
				if (!applicationWebIds.contains(webId)) {
					db.deleteApplicationByWebId(webId);
					Log.i("deleting application with webId" + webId);
				}
			}
			while (databaseIntents.moveToNext()) {
				long webId = databaseIntents.getLong(databaseIntents
						.getColumnIndexOrThrow(MashupProvider.INTENT_WEB_ID));
				if (!intentWebIds.contains(webId)) {
					db.deleteIntentByWebId(webId);
					Log.i("deleting intent with webId" + webId);
				}
			}
		}
		Log.i("end of the document closing the db connection");
		try {
			db.close();
		}
		catch (Exception e) {
		}
	}
	
	/**
	 * Gets be called on closing tags like:
	 * </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		
		if (mashupData && mashupIntents && localName.equals("intent")) {
			insertCount += db.updateOrInsertMashupIntent(newIntent);
			newIntent = null;
		}
		else if (mashupData && mashupApplications && localName.equals("application")) {
			
			insertCount += db.updateOrInsertMashupApplication(newApplication);
			
			for ( int i = 0 ; i < applicationIntentRelationIds.size() ; i++ ) {
				insertCount += db.updateOrInsertRelation(applicationIntentRelationIds.get(i), newApplication.webId);
			}
			
			newApplication = null;
			applicationIntentRelationIds.clear();
		}
		
		else if (localName.equals("mashupData")) {
			mashupData = false;
		}
		else if (localName.equals("mashupIntents")) {
			mashupIntents = false;
		}
	}
	
	@Override
	public void startDocument() throws SAXException, SQLException {
		Log.i("beginning of the document opening the db connection");
		insertCount = 0;
		db.open();
	}
	
	/**
	 * Gets be called on opening tags like:
	 * <tag>
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("mashupData")) {
			// TODO react to a changed databaseversion
			// Long.parseLong(atts.getValue("dataBaseId"));
			// Long.parseLong(atts.getValue("mashupIntentCount"));
			// Long.parseLong(atts.getValue("applicationCount"));
			mashupData = true;
		}
		else if (localName.equals("mashupIntents")) {
			mashupIntents = true;
		}
		else if (localName.equals("mashupApplications")) {
			mashupApplications = true;
		}
		else if (mashupData && mashupIntents) {
			if (localName.equals("intent")) {
				newIntent = new MashupIntent();
				newIntent.action = atts.getValue("action");
				newIntent.title = atts.getValue("title");
				newIntent.icon = atts.getValue("icon");
				newIntent.webId = Long.parseLong(atts.getValue("id"));
				intentWebIds.add(Long.parseLong(atts.getValue("id")));
				
			}
			else if (localName.equals("description")) {
				description = true;
			}
		}
		else if (mashupData && mashupApplications) {
			if (localName.equals("application")) {
				newApplication = new MashupApplication();
				newApplication.name = atts.getValue("name");
				newApplication.applicationPackage = atts.getValue("package");
				newApplication.url = atts.getValue("url");
				newApplication.icon = atts.getValue("icon");
				newApplication.apkUrl = atts.getValue("apkUrl");
				newApplication.developerEmail = atts.getValue("developerEmail");
				newApplication.developerUrl = atts.getValue("developerUrl");
				newApplication.webId = Long.parseLong(atts.getValue("id"));
				applicationWebIds.add(Long.parseLong(atts.getValue("id")));
			}
			else if (localName.equals("intent") && atts.getValue("mashup").equals("1")) {
				applicationIntentRelationIds.add(Long.parseLong(atts.getValue("id")));
			}
			else if (localName.equals("description")) {
				description = true;
			}
		}
	}
}
