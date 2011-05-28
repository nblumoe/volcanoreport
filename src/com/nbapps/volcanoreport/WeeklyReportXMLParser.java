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
	
	public WeeklyReport parse(String filename){
		// parse weekly report
		try {
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			SAXParser parser = parserFactory.newSAXParser();
			File file = new File(filename);
			FileInputStream inputStream = new FileInputStream(file);
			Reader reader = new InputStreamReader(inputStream, XML_ENCODING);
			InputSource is = new InputSource(reader);
			is.setEncoding(XML_ENCODING);
			
			WeeklyReportXMLHandler xmlHandler = new WeeklyReportXMLHandler();
			parser.parse(is, xmlHandler);
		} catch (Exception e) {
			Log.d("VOLCANO_DEBUG","XML Parser Exception: " +e);
		}
		return WeeklyReportXMLHandler.weeklyReport;		
	}
}
