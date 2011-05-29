/*
 * TODO: load volcano overlay from database, download new file if necessary
 * 
 * TODO: re-implement holocene volcanoes
 * 
 * TODO: new icon for volcanoes with new unrest
 * 
 * TODO: change back to kml and use WebView.loadData() for description
 *        -> link
 *  
 * TODO: implement changeable volcano icons
 * 
 * TODO: implement list view
 * 
 * TODO: implement archive
 * 
 * TODO: implement links to volcano details (smithsonian site)
 */
package com.nbapps.volcanoreport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class VolcanoReport extends MapActivity {
	
	private MapView mapView;
	private Drawable volcanoIcon;
	
	private List<Overlay> mapOverlays;
    private VolcanoOverlay weeklyOverlay;
	
	private ArrayList<VolcanicActivity> weeklyVolcanoList = null;
	
	private Boolean firstStart = true;
	
	private PreferencesManager prefsManager;
	private VolcanoDB volcanoDB;
	
	    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefsManager = new PreferencesManager(this.getApplicationContext());
        volcanoDB = new VolcanoDB(this);
        setContentView(R.layout.main);
        
		/*
		 * load preferences
		 */
        try {
			Date localWeeklyReportDate = new SimpleDateFormat().parse(prefsManager.getLocalWeeklyReportDate());
			firstStart = prefsManager.isFirstStart();
			Log.d("VOLCANO_DEBUG", localWeeklyReportDate.toString());
		} catch (ParseException e) {
			Log.d("VOLCANO_DEBUG","Error while reading preferences:" + e);
		}
		
        /*
         * initialize map view
         */
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setSatellite(prefsManager.isSatelliteMapMode());
        mapView.setBuiltInZoomControls(true);
        mapView.invalidate();
        volcanoIcon = this.getResources().getDrawable(R.drawable.volcano_eruption2_red);
        mapOverlays = mapView.getOverlays();
        weeklyOverlay = new VolcanoOverlay(volcanoIcon, this);

        /*
         * get new data if necessary
         */
        if (volcanoDB.isOutdated()) {
        	downloadVolcanoLists();
        }        
		updateVolcanoLists();
		updateOverlay();
		
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
    	switch(menuItem.getItemId()) {
    	case R.id.mapmode:
            if (mapView.isSatellite()) {
            	mapView.setSatellite(false);
            	prefsManager.setSatelliteMapMode(false);
            }
            else {
            	mapView.setSatellite(true);
            	prefsManager.setSatelliteMapMode(true);
            }
    		return true;
		default:
			return super.onOptionsItemSelected(menuItem);
    	}    	    	
    }
    private void downloadVolcanoLists() {
    	downloadWeeklyVolcanoList();
    }
    
    private void downloadWeeklyVolcanoList() {
		// start new thread to download weekly report
		if (firstStart == true) {
			try {
				new ListDownloader().DownloadFromUrl(
						getResources().getString(R.string.urlWeeklyReport),
						getResources().getString(R.string.localWeeklyReport));
			} catch (Exception e) {
				Log.d("VOLCANO_DEBUG", "Exception: " + e);
			}
			prefsManager.setLocalWeeklyReportDate(new SimpleDateFormat().format(new Date()));
			prefsManager.setFirstStart(false);
		} else {
			new Thread() {
				public void run() {
					new ListDownloader().DownloadFromUrl(getResources()
							.getString(R.string.urlWeeklyReport),
							getResources()
									.getString(R.string.localWeeklyReport));
					prefsManager.setLocalWeeklyReportDate(new SimpleDateFormat().format(new Date()));
					prefsManager.setFirstStart(false);
				}
			}.start();
		}
    }    
    
	private void updateVolcanoLists() {
		updateWeeklyVolcanoList();
	}
	
	private void updateWeeklyVolcanoList(){
		// parse weekly report
		//WeeklyReport weeklyReport = new WeeklyReportXMLParser().parse(getResources().getString(R.string.localWeeklyReport));
		//weeklyVolcanoList = weeklyReport.getVolcanicActivities();
		weeklyVolcanoList = volcanoDB.getMostRecentReport().getVolcanicActivities();
	}
	

	
	private void updateOverlay() {
		for (int i = 0; i < weeklyVolcanoList.size(); i++) {
			GeoPoint point = new GeoPoint(weeklyVolcanoList.get(i).getLatitude(),
					weeklyVolcanoList.get(i).getLongitude());
			OverlayItem overlayItem = new OverlayItem(point, weeklyVolcanoList
					.get(i).getTitle(), weeklyVolcanoList.get(i).getDescription());
			weeklyOverlay.addOverlay(overlayItem);

		}
		mapOverlays.add(weeklyOverlay);
	}
	
	public void showVolcanoDetails(int index) {
		Intent intent = new Intent(this, VolcanoDetails.class);
		intent.putExtra("TITLE", weeklyVolcanoList.get(index).getTitle());
		intent.putExtra("REPORTDATE", weeklyVolcanoList.get(index).getReportDate());
		intent.putExtra("NEWUNREST", weeklyVolcanoList.get(index).getNewUnrest());
		intent.putExtra("LATITUDE", weeklyVolcanoList.get(index).getLatitude());
		intent.putExtra("LONGITUDE", weeklyVolcanoList.get(index).getLongitude());
		intent.putExtra("DESCRIPTION", weeklyVolcanoList.get(index).getDescription());
		intent.putExtra("LINK", weeklyVolcanoList.get(index).getLink());
		this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		volcanoDB.close();
		super.onDestroy();
	}
}