package com.nbapps.volcanoreport;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class VolcanoDB extends SQLiteOpenHelper {

	public VolcanoDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE reports (_id INTEGER PRIMARY KEY AUTOIMCREMENT, pubdate TEXT NOT NULL");
		db.execSQL("CREATE TABLE activities (_id INTEGER PRIMARY KEY AUTOIMCREMENT,  TEXT NOT NULL");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
