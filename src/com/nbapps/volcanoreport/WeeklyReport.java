package com.nbapps.volcanoreport;

import java.util.ArrayList;

public class WeeklyReport {
	private ArrayList<VolcanicActivity> volcanicActivities;
	private String pubDate;
	
	public ArrayList<VolcanicActivity> getVolcanicActivities() {
		return volcanicActivities;
	}
	public void setVolcanicActivities(ArrayList<VolcanicActivity> volcanicActivities) {
		this.volcanicActivities = volcanicActivities;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

}
