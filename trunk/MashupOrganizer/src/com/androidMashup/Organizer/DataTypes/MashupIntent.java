package com.androidMashup.Organizer.DataTypes;

public class MashupIntent {
	public long		id;
	public long		webId;
	public String	action;
	public String	description;
	public String	title;
	public String	icon;
	public boolean	enabled;
	
	public MashupIntent() {
	}
	
	public MashupIntent(String action, String description) {
		this.action = action;
		this.description = description;
	}
}
