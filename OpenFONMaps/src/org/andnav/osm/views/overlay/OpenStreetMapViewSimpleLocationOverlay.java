// Created by plusminus on 22:01:11 - 29.09.2008
package org.andnav.osm.views.overlay;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.mashup.OpenFONMaps.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @author Nicolas Gramlich
 */
public class OpenStreetMapViewSimpleLocationOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	protected GeoPoint						mLocation;
	
	protected final Paint					mPaint			= new Paint();
	/** Coordinates the feet of the person are located. */
	protected final android.graphics.Point	PERSON_HOTSPOT	= new android.graphics.Point(24, 39);
	
	protected final Bitmap					PERSON_ICON;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OpenStreetMapViewSimpleLocationOverlay(final Context ctx) {
		this.PERSON_ICON = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.person);
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	@Override
	public void onDraw(final Canvas c, final OpenStreetMapView osmv) {
		if (this.mLocation != null) {
			final OpenStreetMapViewProjection pj = osmv.getProjection();
			final Point screenCoords = new Point();
			pj.toPixels(this.mLocation, screenCoords);
			
			c
					.drawBitmap(PERSON_ICON, screenCoords.x - PERSON_HOTSPOT.x, screenCoords.y - PERSON_HOTSPOT.y, this.mPaint);
		}
	}
	
	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	@Override
	protected void onDrawFinished(Canvas c, OpenStreetMapView osmv) {
		return;
	}
	
	public void setLocation(final GeoPoint mp) {
		this.mLocation = mp;
	}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
