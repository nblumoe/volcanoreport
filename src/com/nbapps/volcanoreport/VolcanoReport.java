package com.nbapps.volcanoreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

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
	
	private ArrayList<VolcanoInfo> weeklyVolcanoList = null;
	private ArrayList<VolcanoInfo> holoceneVolcanoList = null;
	
	private Boolean firstStart = true;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        /*
         * load preferences
         */
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        try {
			Date localWeeklyReportDate = new SimpleDateFormat().parse(preferences.getString("localWeeklyReportDate", "0"));
			firstStart = preferences.getBoolean("firstStart", true);
			Log.d("VOLCANO_DEBUG", localWeeklyReportDate.toString());
		} catch (ParseException e) {
			Log.d("VOLCANO_DEBUG","Error while reading preferences:" + e);
		}
		
        
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setSatellite(true);
        mapView.setBuiltInZoomControls(true);
        mapView.invalidate();
        
        volcanoIcon = this.getResources().getDrawable(R.drawable.volcano_eruption);
        mapOverlays = mapView.getOverlays();
        weeklyOverlay = new VolcanoOverlay(volcanoIcon, this);
        
         /*TODO: re-implement holocene volcanoes
         * 
         * TODO: new icon for volcanoes with new unrest
         * 
         * TODO: change back to kml and use WebView.loadData() for description
         *        -> link
         *        
         * TODO: check for valid local kml file, start update if new file is available
         * 
         * TODO: implement menu with map settings, about dialog, copyright
         * 
         * TODO: implement list view
         * 
         * TODO: implement donation
         * 
         * TODO: create logos and screenshots
         * 
         * TODO: implement earthquakes?
         *  
         */



        downloadVolcanoLists();
		updateVolcanoLists();
		updateOverlay();

	}
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }
    
    private void downloadVolcanoLists() {
    	downloadWeeklyVolcanoList();
    	//downloadHoloceneVolcanoList();
    }
    
    private void downloadWeeklyVolcanoList() {
		// start new thread to download weekly report
		if (firstStart == true) {
			new ListDownloader().DownloadFromUrl(
					getResources().getString(R.string.urlWeeklyReport),
					getResources().getString(R.string.localWeeklyReport));
			SharedPreferences preferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("localWeeklyReportDate",
					new SimpleDateFormat().format(new Date()));
			editor.putBoolean("firstStart", false);
			editor.commit();			
		} else {
			new Thread() {
				public void run() {
					new ListDownloader().DownloadFromUrl(getResources()
							.getString(R.string.urlWeeklyReport),
							getResources()
									.getString(R.string.localWeeklyReport));
					SharedPreferences preferences = getPreferences(MODE_PRIVATE);
					SharedPreferences.Editor editor = preferences.edit();
					editor.putString("localWeeklyReportDate",
							new SimpleDateFormat().format(new Date()));
					editor.commit();
				}
			}.start();
		}
    }
    
    private void downloadHoloceneVolcanoList() {
		// start new thread to download holocene volcanoes
		new Thread() {
			public void run() {
				new ListDownloader()
						.DownloadFromUrl(
								getResources().getString(
										R.string.urlHoloceneVolcanoes),
								getResources().getString(
										R.string.localHoloceneVolcanoesZipped));


			}
		}.start();
    }
    
	private void updateVolcanoLists() {
		updateWeeklyVolcanoList();
		//updateHoloceneVolcanoList();
	}
	
	private void updateWeeklyVolcanoList(){
		// parse weekly report
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			File file = new File(getResources().getString(R.string.localWeeklyReport));
			FileInputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
			InputSource is = new InputSource(reader);
			is.setEncoding("ISO-8859-1");
			VolcanoXMLHandler xmlHandler = new VolcanoXMLHandler();
			parser.parse(is, xmlHandler);
		} catch (Exception e) {
			Log.d("VOLCANO_DEBUG","XML Parser Exception: " +e);
		}
		weeklyVolcanoList = VolcanoXMLHandler.volcanoList;		
	}
	
	private void updateHoloceneVolcanoList() {
		// parse holocene volcanoes
		ZipFile zippedFile = null;
		InputStream inputStream = null;
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			zippedFile = new ZipFile(getResources().getString(
					R.string.localHoloceneVolcanoesZipped));
			inputStream = zippedFile.getInputStream(zippedFile.entries()
					.nextElement());
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			HoloceneXMLHandler xmlHandler = new HoloceneXMLHandler();
			parser.parse(is, xmlHandler);
		} catch (Exception e) {
			Log.d("VOLCANO_DEBUG","XML Parser Exception: " +e);
		}
		holoceneVolcanoList = HoloceneXMLHandler.volcanoList;		
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
}