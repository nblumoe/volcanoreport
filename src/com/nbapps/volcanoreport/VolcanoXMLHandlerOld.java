package com.nbapps.volcanoreport;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class VolcanoXMLHandlerOld extends DefaultHandler {

	private String currentValue = "";
	public static VolcanoList volcanoList = null;
	
	public static VolcanoList getVolcanoList() {
		return volcanoList;
	}

	public static void setVolcanoList(VolcanoList volcanoList) {
		VolcanoXMLHandlerOld.volcanoList = volcanoList;
	}

	@Override
	public void startDocument() {
		volcanoList = new VolcanoList();
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
		if(localName.equalsIgnoreCase("name")) {
			volcanoList.addName(currentValue);
		}
		else if (localName.equalsIgnoreCase("description")) {
			volcanoList.addDescription(currentValue);
		}
		else if (localName.equalsIgnoreCase("longitude")) {
			volcanoList.addLongitude(currentValue);
		}
		else if (localName.equalsIgnoreCase("latitude")) {
			volcanoList.addLatitude(currentValue);
		}
		/** create new volcano list after style map definitions
		 * to remove the first description, which does not belong to a volcano
		 */
		else if (localName.equalsIgnoreCase("stylemap")) {
			volcanoList = new VolcanoList();
		}
		currentValue = "";
	}
	
	@Override
	public void characters(char[] ch, int start, int length){
		
		currentValue = currentValue + new String(ch, start, length);
		
	}
}
