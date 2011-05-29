package com.nbapps.volcanoreport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class VolcanoDB extends SQLiteOpenHelper {
    
	private static final int DATABASE_VERSION = 1;
	private static final String DATE_FORMAT = "\nEEE, dd MMM yyyy hh:mm:ss ZZZZZ";
		
	private static final String DATABASE_NAME = "VolcanoReportDB";
	private static final String ID = "_id";
	private static final String REPORTS_TABLE_NAME = "reports";
	private static final String REPORTS_PUBDATE_COLUMN = "pubdate";
	
	private static final String ACTIVITIES_TABLE_NAME = "activitites";
	private static final String ACTIVITIES_TITLE_COLUMN = "title";
	private static final String ACTIVITIES_REPORTDATE_COLUMN = "reportdate";
	private static final String ACTIVITIES_NEWUNREST_COLUMN = "newunrest";
	private static final String ACTIVITIES_LATITUDE_COLUMN = "latitude";
	private static final String ACTIVITIES_LONGITUDE_COLUMN = "longitude";
	private static final String ACTIVITIES_DESCRIPTION_COLUMN = "description";
	private static final String ACTIVITIES_LINK_COLUMN = "link";
	private static final String ACTIVITIES_REPORTID_COLUMN = "reportid";
	
	

	public VolcanoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " +
				REPORTS_TABLE_NAME + " ("+
				ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
				REPORTS_PUBDATE_COLUMN + " TEXT NOT NULL)");
		
		db.execSQL("CREATE TABLE " +
				ACTIVITIES_TABLE_NAME + " (" + 
				ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
				ACTIVITIES_TITLE_COLUMN + " TEXT NOT NULL," +
				ACTIVITIES_REPORTDATE_COLUMN + " TEXT NOT NULL," +
				ACTIVITIES_NEWUNREST_COLUMN + " TEXT NOT NULL," +
				ACTIVITIES_LATITUDE_COLUMN + " INTEGER NOT NULL," +
				ACTIVITIES_LONGITUDE_COLUMN + " INTEGER NOT NULL," +
				ACTIVITIES_DESCRIPTION_COLUMN + " TEXT NOT NULL," +
				ACTIVITIES_LINK_COLUMN + " TEXT NOT NULL," +
				ACTIVITIES_REPORTID_COLUMN + " LONG NOT NULL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private long setVolcanicActivity(VolcanicActivity volcanicActivity, long reportid) {
		ContentValues values = new ContentValues();
		values.put(ACTIVITIES_TITLE_COLUMN, volcanicActivity.getTitle());
		values.put(ACTIVITIES_DESCRIPTION_COLUMN, volcanicActivity.getDescription());
		values.put(ACTIVITIES_NEWUNREST_COLUMN, volcanicActivity.getNewUnrest());
		values.put(ACTIVITIES_REPORTDATE_COLUMN, volcanicActivity.getReportDate());
		values.put(ACTIVITIES_LATITUDE_COLUMN, volcanicActivity.getLatitude());
		values.put(ACTIVITIES_LONGITUDE_COLUMN, volcanicActivity.getLongitude());
		values.put(ACTIVITIES_LINK_COLUMN, volcanicActivity.getLink().toString());
		values.put(ACTIVITIES_REPORTID_COLUMN, reportid);
		return getWritableDatabase().insert(ACTIVITIES_TABLE_NAME, null, values);	
	}
	
	public long setWeeklyReport(WeeklyReport weeklyReport){
		ContentValues values = new ContentValues();
		values.put(REPORTS_PUBDATE_COLUMN, weeklyReport.getPubDate());
		long reportid = getWritableDatabase().insert(REPORTS_TABLE_NAME, null, values);
		
		ArrayList<VolcanicActivity> activityList = weeklyReport.getVolcanicActivities();
		for (VolcanicActivity volcanicActivity : activityList) {
			this.setVolcanicActivity(volcanicActivity, reportid);
		}
		return reportid;
	}
	
	private WeeklyReport getWeeklyReport(long reportid) {
		Cursor cursor = getReadableDatabase().query(REPORTS_TABLE_NAME, new String[]{ID,REPORTS_PUBDATE_COLUMN}, ID+"=?", new String[]{String.valueOf(reportid)}, null, null, null);
		cursor.moveToFirst();
		String pubDate = cursor.getString(cursor.getColumnIndex(REPORTS_PUBDATE_COLUMN));
		WeeklyReport weeklyReport = new WeeklyReport();
		weeklyReport.setPubDate(pubDate);
		ArrayList<VolcanicActivity> volcanicActivities = this.getActivitiesOfReport(reportid);
		weeklyReport.setVolcanicActivities(volcanicActivities);
		cursor.close();
		return weeklyReport;
	}
	
	private ArrayList<VolcanicActivity> getActivitiesOfReport(long reportid) {
		ArrayList<VolcanicActivity> volcanicActivities = new ArrayList<VolcanicActivity>();
		Cursor cursor = getReadableDatabase().query(ACTIVITIES_TABLE_NAME, new String[]{ACTIVITIES_TITLE_COLUMN,ACTIVITIES_REPORTDATE_COLUMN,ACTIVITIES_DESCRIPTION_COLUMN,ACTIVITIES_LATITUDE_COLUMN,ACTIVITIES_LONGITUDE_COLUMN, ACTIVITIES_LINK_COLUMN, ACTIVITIES_NEWUNREST_COLUMN, ACTIVITIES_REPORTID_COLUMN}, ACTIVITIES_REPORTID_COLUMN+"=?", new String[]{String.valueOf(reportid)}, null, null, null);
		while (cursor.moveToNext()) {
			VolcanicActivity volcanicActivity = new VolcanicActivity();
			volcanicActivity.setTitle(cursor.getString(cursor.getColumnIndex(ACTIVITIES_TITLE_COLUMN)));
			volcanicActivity.setDescription(cursor.getString(cursor.getColumnIndex(ACTIVITIES_DESCRIPTION_COLUMN)));
			volcanicActivity.setLatitude(cursor.getInt(cursor.getColumnIndex(ACTIVITIES_LATITUDE_COLUMN)));
			volcanicActivity.setLongitude(cursor.getInt(cursor.getColumnIndex(ACTIVITIES_LONGITUDE_COLUMN)));
			volcanicActivity.setLink(cursor.getString(cursor.getColumnIndex(ACTIVITIES_LINK_COLUMN)));
			volcanicActivity.setNewUnrest("1"==cursor.getString(cursor.getColumnIndex(ACTIVITIES_NEWUNREST_COLUMN)));
			volcanicActivity.setReportDate(cursor.getString(cursor.getColumnIndex(ACTIVITIES_REPORTDATE_COLUMN)));
			volcanicActivities.add(volcanicActivity);
		}
		cursor.close();
		return volcanicActivities;
	}
	
	public Boolean isOutdated() {
		// get time difference in milliseconds
		long dateDiff = new Date().getTime() - getMostRecentPubdate().getTime();
		// check if time difference is larger than 7 days
		return (dateDiff>7*24*60*60*1000);				
	}
	
	public WeeklyReport getMostRecentReport() {
		Cursor cursor = getReadableDatabase().query(REPORTS_TABLE_NAME, new String[]{ID,REPORTS_PUBDATE_COLUMN}, null, null, null, null, null);
		long reportid = -1;
		Date mostRecentPubdate = new Date(0);
		Date newDate = null;
		while (cursor.moveToNext()) {
			String dateString = cursor.getString(cursor
					.getColumnIndex(REPORTS_PUBDATE_COLUMN));
			try {
				newDate = new SimpleDateFormat(DATE_FORMAT).parse(dateString);
				Log.d("VOLCANO_DEBUG","newDate " + newDate);
			} catch (ParseException e) {
				Log.d("VOLCANO_DEBUG", "DateFormat pasing error: " + e);
			}
			if (newDate.after(mostRecentPubdate)) {
				mostRecentPubdate = newDate;
				reportid = cursor.getLong(cursor.getColumnIndex(ID));
			}
		}
		cursor.close();
		return getWeeklyReport(reportid);
	}
	
	private Date getMostRecentPubdate() {
		Date mostRecentPubdate =null;
		try {
			mostRecentPubdate = new SimpleDateFormat(DATE_FORMAT).parse(getMostRecentReport().getPubDate());
		} catch (ParseException e) {
			Log.d("VOLCANO_DEBUG", "DateFormat pasing error: " + e);
		}
		return mostRecentPubdate;
	}

}
