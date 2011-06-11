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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

	private ArrayList<VolcanoInfo> weeklyVolcanoList = null;

	private Boolean firstStart = true;
	private Boolean refreshVolcanoList = true;

	private PreferencesManager prefsManager;

	static final int DIALOG_NONETWORKWARNING_ID = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		prefsManager = new PreferencesManager(this.getApplicationContext());

		setContentView(R.layout.main);

		/*
		 * load preferences
		 */

		try {
			Date localWeeklyReportDate = new SimpleDateFormat()
					.parse(prefsManager.getLocalWeeklyReportDate());
			firstStart = prefsManager.isFirstStart();
			Log.d("VOLCANO_DEBUG", localWeeklyReportDate.toString());
		} catch (ParseException e) {
			Log.d("VOLCANO_DEBUG", "Error while reading preferences:" + e);
		}

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setSatellite(prefsManager.isSatelliteMapMode());
		mapView.setBuiltInZoomControls(true);
		mapView.invalidate();

		volcanoIcon = this.getResources().getDrawable(
				R.drawable.volcano_eruption);
		mapOverlays = mapView.getOverlays();
		weeklyOverlay = new VolcanoOverlay(volcanoIcon, this);

		/*
		 * TODO: check for valid local kml file, start update if new file is
		 * available, this should replace firstStart checke TODO: store map mode
		 * in preferences
		 * 
		 * TODO: re-implement holocene volcanoes
		 * 
		 * TODO: new icon for volcanoes with new unrest
		 * 
		 * TODO: change back to kml and use WebView.loadData() for description
		 * -> link
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
		 */

		tryGettingVolcanoLists();
	}

	protected void tryGettingVolcanoLists() {
		downloadVolcanoLists();
		if (refreshVolcanoList) {
			updateVolcanoLists();
			if (weeklyVolcanoList != null) {
				updateOverlay();
			} else {
				showDialog(DIALOG_NONETWORKWARNING_ID);
			}
			refreshVolcanoList = false;
			prefsManager.setCurrentListDate(VolcanoXMLHandler.parsedVolcanoListDate);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_NONETWORKWARNING_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Sorry, no valid file with volcano information is available. Please ensure a working internet connection and click 'Retry'.")
					.setCancelable(false)
					.setPositiveButton("Retry",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			dialog = builder.create();
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					tryGettingVolcanoLists();
				}
			});
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		// case R.id.about:
		// return true;
		case R.id.mapmode:
			if (mapView.isSatellite()) {
				mapView.setSatellite(false);
				prefsManager.setSatelliteMapMode(false);
			} else {
				mapView.setSatellite(true);
				prefsManager.setSatelliteMapMode(true);
			}

			return true;
			// case R.id.support_it:
			// return true;
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
			prefsManager.setLocalWeeklyReportDate(new SimpleDateFormat()
					.format(new Date()));
			prefsManager.setFirstStart(false);
		} else {
			new Thread() {
				public void run() {
					new ListDownloader().DownloadFromUrl(getResources()
							.getString(R.string.urlWeeklyReport),
							getResources()
									.getString(R.string.localWeeklyReport));
					prefsManager
							.setLocalWeeklyReportDate(new SimpleDateFormat()
									.format(new Date()));
					prefsManager.setFirstStart(false);
				}
			}.start();
		}
	}


	private void updateVolcanoLists() {
		updateWeeklyVolcanoList();
	}

	private void updateWeeklyVolcanoList() {
		// parse weekly report
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			File file = new File(getResources().getString(
					R.string.localWeeklyReport));
			FileInputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, "ISO-8859-1");
			InputSource is = new InputSource(reader);
			is.setEncoding("ISO-8859-1");
			VolcanoXMLHandler xmlHandler = new VolcanoXMLHandler();
			parser.parse(is, xmlHandler);
		} catch (Exception e) {
			Log.d("VOLCANO_DEBUG", "XML Parser Exception: " + e);
		}
		weeklyVolcanoList = VolcanoXMLHandler.volcanoList;
	}

	private void updateOverlay() {
		for (int i = 0; i < weeklyVolcanoList.size(); i++) {
			GeoPoint point = new GeoPoint(weeklyVolcanoList.get(i)
					.getLatitude(), weeklyVolcanoList.get(i).getLongitude());
			OverlayItem overlayItem = new OverlayItem(point, weeklyVolcanoList
					.get(i).getTitle(), weeklyVolcanoList.get(i)
					.getDescription());
			weeklyOverlay.addOverlay(overlayItem);

		}
		mapOverlays.add(weeklyOverlay);
	}

	public void showVolcanoDetails(int index) {
		Intent intent = new Intent(this, VolcanoDetails.class);
		intent.putExtra("TITLE", weeklyVolcanoList.get(index).getTitle());
		intent.putExtra("REPORTDATE", weeklyVolcanoList.get(index)
				.getReportDate());
		intent.putExtra("NEWUNREST", weeklyVolcanoList.get(index)
				.getNewUnrest());
		intent.putExtra("LATITUDE", weeklyVolcanoList.get(index).getLatitude());
		intent.putExtra("LONGITUDE", weeklyVolcanoList.get(index)
				.getLongitude());
		intent.putExtra("DESCRIPTION", weeklyVolcanoList.get(index)
				.getDescription());
		intent.putExtra("LINK", weeklyVolcanoList.get(index).getLink());
		this.startActivity(intent);
	}
}