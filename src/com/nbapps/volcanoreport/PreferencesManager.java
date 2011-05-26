package com.nbapps.volcanoreport;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	
	private SharedPreferences preferences;
	private Editor editor;
	
	public PreferencesManager(Context context) {
		preferences = context.getSharedPreferences("VolcanoReport",Context.MODE_PRIVATE);
		editor = preferences.edit();
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
