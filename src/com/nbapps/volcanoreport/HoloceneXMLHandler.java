package com.nbapps.volcanoreport;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HoloceneXMLHandler extends VolcanoXMLHandler {
	
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		if (localName.equalsIgnoreCase("placemark")) {
			currentVolcanoInfo = new VolcanoInfo();
		}

	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (currentVolcanoInfo==null) {
			currentValue = "";
			return;
		}
		
		if (localName.equalsIgnoreCase("name")) {
			currentVolcanoInfo.setTitle(currentValue);
		}
		else if (localName.equalsIgnoreCase("description")) {
			currentVolcanoInfo.setDescription(currentValue);
		}
		else if (localName.equalsIgnoreCase("coordinates")) {
			currentVolcanoInfo.setLongitude(currentValue.split(",")[1]);
			currentVolcanoInfo.setLatitude(currentValue.split(",")[0]);
		}
		else if (localName.equalsIgnoreCase("Placemark")) {
			volcanoList.add(currentVolcanoInfo);
			currentVolcanoInfo=null;
		}
		currentValue = "";
	}
}
