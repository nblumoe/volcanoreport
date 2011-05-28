package com.nbapps.volcanoreport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;

import android.util.Log;

public class WeeklyReportXMLParser {
	
	private static final String XML_ENCODING = "ISO-8859-1";
	
	private WeeklyReport parse(String filename){
		// parse weekly report
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			File file = new File(filename);
			FileInputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, XML_ENCODING);
			InputSource is = new InputSource(reader);
			is.setEncoding(XML_ENCODING);
			VolcanoXMLHandler xmlHandler = new VolcanoXMLHandler();
			parser.parse(is, xmlHandler);
		} catch (Exception e) {
			Log.d("VOLCANO_DEBUG","XML Parser Exception: " +e);
		}
		weeklyVolcanoList = VolcanoXMLHandler.volcanoList;		
	}
}
