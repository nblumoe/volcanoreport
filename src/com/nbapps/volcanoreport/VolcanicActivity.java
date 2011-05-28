package com.nbapps.volcanoreport;

import java.net.MalformedURLException;
import java.net.URL;

public class VolcanicActivity {
	
	private String title = null;
	private String reportDate = null;
	private Boolean newUnrest = false;
	private int latitude = 0;
	private int longitude = 0;
	private String description = null;
	private URL link = null;

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public int getLatitude() {
		return latitude;
	}


	public void setLatitude(String latitude) {
		Float recalcValue = Float.parseFloat(latitude)*1000000;
		this.latitude = Math.round(recalcValue);
	}


	public int getLongitude() {
		return longitude;
	}


	public void setLongitude(String longitude) {
		Float recalcValue = Float.parseFloat(longitude)*1000000;
		this.longitude = Math.round(recalcValue);
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public URL getLink() {
		return link;
	}


	public void setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public String getReportDate() {
		return reportDate;
	}


	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}


	public Boolean getNewUnrest() {
		return newUnrest;
	}


	public void setNewUnrest(Boolean newUnrest) {
		this.newUnrest = newUnrest;
	}


}
