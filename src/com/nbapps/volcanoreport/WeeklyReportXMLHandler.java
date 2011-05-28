package com.nbapps.volcanoreport;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeeklyReportXMLHandler extends DefaultHandler {
	protected String currentValue = "";
	protected VolcanicActivity currentVolcanoActivity = null;
	public static WeeklyReport weeklyReport;
	
	@Override
	public void startDocument() {
		weeklyReport = new WeeklyReport();
	}
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		if (localName.equalsIgnoreCase("item")) {
			currentVolcanoActivity = new VolcanicActivity();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentVolcanoActivity==null) {
			currentValue = "";
			return;
		}
		
		if (localName.equalsIgnoreCase("pubDate")) {
			weeklyReport.setPubDate(currentValue);
		}
		else if (localName.equalsIgnoreCase("title")) {
			String[] splitTitle = currentValue.split(" - ");
			currentVolcanoActivity.setTitle(splitTitle[0]);
			currentVolcanoActivity.setReportDate(splitTitle[1]);
			if (splitTitle.length >= 3) {
				currentVolcanoActivity
						.setNewUnrest((splitTitle[2].contains("NEW")));
			}
		}
		else if (localName.equalsIgnoreCase("description")) {
			currentVolcanoActivity.setDescription(currentValue);
		}
		else if (localName.equalsIgnoreCase("point")) {
			currentVolcanoActivity.setLongitude(currentValue.split(" ")[1]);
			currentVolcanoActivity.setLatitude(currentValue.split(" ")[0]);
		}
		else if (localName.equalsIgnoreCase("link")) {
			currentVolcanoActivity.setLink(currentValue);
		}
		else if (localName.equalsIgnoreCase("item")) {
			weeklyReport.addVolcanicActivity(currentVolcanoActivity);
			currentVolcanoActivity=null;
		}
		currentValue = "";
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		currentValue = currentValue + new String(ch, start, length);
		
	}
}
