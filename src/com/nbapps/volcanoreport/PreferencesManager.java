package com.nbapps.volcanoreport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	private SharedPreferences preferences;
	private Editor editor;
	SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	
	public PreferencesManager(Context context) {
		preferences = context.getSharedPreferences("VolcanoReport",Context.MODE_PRIVATE);
		editor = preferences.edit();
	}
	
	public Date getCurrentListDate() {
		Date currentListDate;
		try {
			currentListDate = df.parse(preferences.getString("currentListDate", "Mon, 01 Jan 0000 00:00:00 +0000"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			currentListDate = new Date(0);
		}
		return currentListDate;
	}
	
	public void setCurrentListDate(Date newListDate) {
		editor.putString("currentListDate", df.format(newListDate));
	}
	
	public boolean isFirstStart() {
		return preferences.getBoolean("firstStart", true);
	}
	
	public String getLocalWeeklyReportDate() {
		return preferences.getString("localWeeklyReportDate", "");
	}
	
	public boolean isSatelliteMapMode() {
		return preferences.getBoolean("satelliteMapMode", true);
	}
	
	public void setLocalWeeklyReportDate(String newValue) {
		editor.putString("localWeeklyReportDate", newValue);
		editor.commit();
	}
	
	public void setFirstStart(boolean newValue) {
		editor.putBoolean("firstStart", newValue);
		editor.commit();
	}

	public void setSatelliteMapMode(boolean newValue) {
		editor.putBoolean("satelliteMapMode", newValue);
		editor.commit();
	}
}
