package com.nbapps.volcanoreport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;



public class VolcanoXMLHandler extends DefaultHandler {

	protected String currentValue = "";
	protected VolcanoInfo currentVolcanoInfo = null;
	public static ArrayList<VolcanoInfo> volcanoList = null;
	public static Date volcanoListDate = new Date(0);
	
	@Override
	public void startDocument() {
		volcanoList = new ArrayList<VolcanoInfo>();
	}
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		if (localName.equalsIgnoreCase("item")) {
			currentVolcanoInfo = new VolcanoInfo();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentVolcanoInfo==null) {
			currentValue = "";
			return;
		}
		
		if (localName.equalsIgnoreCase("title")) {
			String[] splitTitle = currentValue.split(" - ");
			currentVolcanoInfo.setTitle(splitTitle[0]);
			currentVolcanoInfo.setReportDate(splitTitle[1]);
			if (splitTitle.length >= 3) {
				currentVolcanoInfo
						.setNewUnrest((splitTitle[2].contains("NEW")));
			}
		}
		else if (localName.equalsIgnoreCase("pubDate")) {
			SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
			try {
				volcanoListDate = df.parse(currentValue);
			} catch (ParseException e) {
				Log.d("DEBUG", currentValue+"!");
				Log.d("DEBUG", e.toString());
			}
		}
		else if (localName.equalsIgnoreCase("description")) {
			currentVolcanoInfo.setDescription(currentValue);
		}
		else if (localName.equalsIgnoreCase("point")) {
			currentVolcanoInfo.setLongitude(currentValue.split(" ")[1]);
			currentVolcanoInfo.setLatitude(currentValue.split(" ")[0]);
		}
		else if (localName.equalsIgnoreCase("link")) {
			currentVolcanoInfo.setLink(currentValue);
		}
		else if (localName.equalsIgnoreCase("item")) {
			volcanoList.add(currentVolcanoInfo);
			currentVolcanoInfo=null;
		}
		currentValue = "";
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		currentValue = currentValue + new String(ch, start, length);
		
	}
}
