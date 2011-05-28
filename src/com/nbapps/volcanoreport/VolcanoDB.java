package com.nbapps.volcanoreport;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VolcanoDB extends SQLiteOpenHelper {
    
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "VolcanoReportDB";
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
				REPORTS_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
				REPORTS_PUBDATE_COLUMN + " TEXT NOT NULL)");
		
		db.execSQL("CREATE TABLE " +
				ACTIVITIES_TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
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

}