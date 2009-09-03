// Created by plusminus on 22:59:38 - 12.09.2008
package org.andnav.osm.views.overlay;

import org.mashup.OpenFONMaps.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class OpenStreetMapViewItemizedOverlayControlView extends LinearLayout {
	// ===========================================================
	// Constants
	// ===========================================================
	
	// ===========================================================
	// Fields
	// ===========================================================
	
	public interface ItemizedOverlayControlViewListener {
		public void onCenter();
		
		public void onNavTo();
		
		public void onNext();
		
		public void onPrevious();
	}
	
	protected ImageButton							mCenterToButton;
	protected ItemizedOverlayControlViewListener	mLis;
	protected ImageButton							mNavToButton;
	
	protected ImageButton							mNextButton;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	protected ImageButton							mPreviousButton;
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	public OpenStreetMapViewItemizedOverlayControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.mPreviousButton = new ImageButton(context);
		this.mPreviousButton.setImageDrawable(context.getResources().getDrawable(R.drawable.previous));
		
		this.mNextButton = new ImageButton(context);
		this.mNextButton.setImageDrawable(context.getResources().getDrawable(R.drawable.next));
		
		this.mCenterToButton = new ImageButton(context);
		this.mCenterToButton.setImageDrawable(context.getResources().getDrawable(R.drawable.center));
		
		this.mNavToButton = new ImageButton(context);
		this.mNavToButton.setImageDrawable(context.getResources().getDrawable(R.drawable.navto_small));
		
		this.addView(mPreviousButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mCenterToButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mNavToButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(mNextButton, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		initViewListeners();
	}
	
	private void initViewListeners() {
		this.mNextButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (OpenStreetMapViewItemizedOverlayControlView.this.mLis != null)
					OpenStreetMapViewItemizedOverlayControlView.this.mLis.onNext();
			}
		});
		
		this.mPreviousButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (OpenStreetMapViewItemizedOverlayControlView.this.mLis != null)
					OpenStreetMapViewItemizedOverlayControlView.this.mLis.onPrevious();
			}
		});
		
		this.mCenterToButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (OpenStreetMapViewItemizedOverlayControlView.this.mLis != null)
					OpenStreetMapViewItemizedOverlayControlView.this.mLis.onCenter();
			}
		});
		
		this.mNavToButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if (OpenStreetMapViewItemizedOverlayControlView.this.mLis != null)
					OpenStreetMapViewItemizedOverlayControlView.this.mLis.onNavTo();
			}
		});
	}
	
	public void setItemizedOverlayControlViewListener(final ItemizedOverlayControlViewListener lis) {
		this.mLis = lis;
	}
	
	public void setNavToVisible(final int pVisibility) {
		this.mNavToButton.setVisibility(pVisibility);
	}
	
	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	public void setNextEnabled(final boolean pEnabled) {
		this.mNextButton.setEnabled(pEnabled);
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
	
	public void setPreviousEnabled(final boolean pEnabled) {
		this.mPreviousButton.setEnabled(pEnabled);
	}
}
