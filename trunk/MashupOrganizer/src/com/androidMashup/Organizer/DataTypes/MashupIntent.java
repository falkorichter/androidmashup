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
	
	/**
	 * a simple constructor only accepting the title so it can be used with
	 * _toString()
	 * 
	 * @param title
	 *            the title for this intent
	 */
	public MashupIntent(String title) {
		this.title = title;
	}
	
	public MashupIntent(String action, String description) {
		this.action = action;
		this.description = description;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
