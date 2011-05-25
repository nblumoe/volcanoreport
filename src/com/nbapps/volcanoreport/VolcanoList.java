package com.nbapps.volcanoreport;

import java.util.ArrayList;

public class VolcanoList {

	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<String> descriptions = new ArrayList<String>();
	private ArrayList<Integer> longitudes = new ArrayList<Integer>();
	private ArrayList<Integer> latitudes = new ArrayList<Integer>();
	
	public ArrayList<String> getNames() {
		return names;
	}
	public ArrayList<String> getDescriptions() {
		return descriptions;
	}

	public ArrayList<Integer> getLongitudes() {
		return longitudes;
	}

	public ArrayList<Integer> getLatitudes() {
		return latitudes;
	}
	
	public void addName(String name) {
		this.names.add(name);
	}
	
	public void addDescription(String description) {
		this.descriptions.add(description);
	}
	
	public void addLatitude(String latitude) {
		Float recalcValue = Float.parseFloat(latitude)*1000000;
		this.latitudes.add(Math.round(recalcValue));
	}
	
	public void addLongitude(String longitude) {
		Float recalcValue = Float.parseFloat(longitude)*1000000;
		this.longitudes.add(Math.round(recalcValue));
	}

}
