package com.example.therunningapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.therunningapp.TrappContract.TrappEntry;

public class TrappDBHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "TRAPP.db";
	
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " 
				+ TrappEntry.TABLE_NAME +
				" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_DATE + " TEXT, "
				+ TrappEntry.COLUMN_NAME_TIME + " INTEGER, " + TrappEntry.COLUMN_NAME_DISTANCE + " INTEGER, " 
				+ TrappEntry.COLUMN_NAME_CALORIES + " INTEGER)";
	
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME;
	
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
	}
	
	public TrappDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}
	

}

