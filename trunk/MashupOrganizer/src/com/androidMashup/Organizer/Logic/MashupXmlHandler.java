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
	private MashupApplication		app					= null;
	private MashupIntent			intent				= null;
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
		if (description && intent != null) {
			intent.description = new String(ch);
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
			insertCount += db.updateOrInsertMashupIntent(intent);
			intent = null;
		}
		else if (mashupData && mashupApplications && localName.equals("application")) {
			
			insertCount += db.updateOrInsertMashupApplication(app);
			
			for ( int i = 0 ; i < applicationIntentRelationIds.size() ; i++ ) {
				insertCount += db.updateOrInsertRelation(applicationIntentRelationIds.get(i), app.webId);
			}
			
			app = null;
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
				intent = new MashupIntent();
				intent.action = atts.getValue("action");
				intent.title = atts.getValue("title");
				intent.icon = atts.getValue("icon");
				intent.webId = Long.parseLong(atts.getValue("id"));
				intentWebIds.add(Long.parseLong(atts.getValue("id")));
				
			}
			else if (localName.equals("description")) {
				description = true;
			}
		}
		else if (mashupData && mashupApplications) {
			if (localName.equals("application")) {
				app = new MashupApplication();
				app.name = atts.getValue("name");
				app.applicationPackage = atts.getValue("package");
				app.url = atts.getValue("url");
				app.icon = atts.getValue("icon");
				app.apkUrl = atts.getValue("apkUrl");
				app.developerEmail = atts.getValue("developerEmail");
				app.developerUrl = atts.getValue("developerUrl");
				app.webId = Long.parseLong(atts.getValue("id"));
				applicationWebIds.add(Long.parseLong(atts.getValue("id")));
			}
			else if (localName.equals("intent") && atts.getValue("mashup").equals("1")) {
				applicationIntentRelationIds.add(Long.parseLong(atts.getValue("id")));
			}
		}
	}
}
