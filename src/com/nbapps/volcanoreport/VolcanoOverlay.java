package com.nbapps.volcanoreport;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class VolcanoOverlay extends ItemizedOverlay {
	
	private VolcanoReport mContext;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public VolcanoOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public VolcanoOverlay(Drawable defaultMarker, VolcanoReport context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index){
		mContext.showVolcanoDetails(index);
		return true;
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
}
