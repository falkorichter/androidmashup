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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * @author Nicolas Gramlich
 */
public class OpenStreetMapViewDirectedLocationOverlay extends OpenStreetMapViewOverlay {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	protected final Bitmap	DIRECTION_ARROW;
	
	private final float		DIRECTION_ARROW_CENTER_X;
	
	private final float		DIRECTION_ARROW_CENTER_Y;
	private final int		DIRECTION_ARROW_HEIGHT;
	
	private final int		DIRECTION_ARROW_WIDTH;
	
	private final Matrix	directionRotater	= new Matrix();
	protected float			mBearing;
	protected GeoPoint		mLocation;
	protected final Paint	mPaint				= new Paint();
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	public OpenStreetMapViewDirectedLocationOverlay(final Context ctx) {
		this.DIRECTION_ARROW = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.direction_arrow);
		
		this.DIRECTION_ARROW_CENTER_X = this.DIRECTION_ARROW.getWidth() / 2 - 0.5f;
		this.DIRECTION_ARROW_CENTER_Y = this.DIRECTION_ARROW.getHeight() / 2 - 0.5f;
		this.DIRECTION_ARROW_HEIGHT = this.DIRECTION_ARROW.getHeight();
		this.DIRECTION_ARROW_WIDTH = this.DIRECTION_ARROW.getWidth();
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
			
			/*
			 * Rotate the direction-Arrow according to the bearing we are
			 * driving. And draw it to the canvas.
			 */
			this.directionRotater.setRotate(this.mBearing, DIRECTION_ARROW_CENTER_X, DIRECTION_ARROW_CENTER_Y);
			Bitmap rotatedDirection = Bitmap
					.createBitmap(DIRECTION_ARROW, 0, 0, DIRECTION_ARROW_WIDTH, DIRECTION_ARROW_HEIGHT, this.directionRotater, false);
			c.drawBitmap(rotatedDirection, screenCoords.x - rotatedDirection.getWidth() / 2, screenCoords.y
																								- rotatedDirection
																										.getHeight()
																								/ 2, this.mPaint);
		}
	}
	
	@Override
	protected void onDrawFinished(Canvas c, OpenStreetMapView osmv) {
		return;
	}
	
	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	public void setBearing(final float aHeading) {
		this.mBearing = aHeading;
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
