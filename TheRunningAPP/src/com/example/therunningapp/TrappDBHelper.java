package com.example.therunningapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.therunningapp.TrappContract.TrappEntry;

public class TrappDBHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "TRAPP.db";
	
	//table for each workout
	private static final String SQL_CREATE_WORKOUTLOG = "CREATE TABLE " 
				+ TrappEntry.TABLE_NAME +
				" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_DATE + " TEXT, "
				+ TrappEntry.COLUMN_NAME_TIME + " FLOAT, " + TrappEntry.COLUMN_NAME_DISTANCE + " DOUBLE, " 
				+ TrappEntry.COLUMN_NAME_CALORIES + " INTEGER, " + TrappEntry.COLUMN_NAME_AVGSPEED + " INTEGER, "
				+ TrappEntry.COLUMN_NAME_LOCATIONS + " BLOB)";
	
	
	//table for storing preferances
	private static final String SQL_CREATE_PREF = "CREATE TABLE " 
			+ TrappEntry.TABLE_NAMEPREF +
			" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_NAME + " TEXT, "
			+ TrappEntry.COLUMN_NAME_WEIGHT + " INTEGER, " + TrappEntry.COLUMN_NAME_HEIGHT + " INTEGER)";
	
	//Delete table for workout
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME;
	//delete table for preferences
	private static final String SQL_DELETE_ENTRIES1 = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAMEPREF;
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_WORKOUTLOG);
		db.execSQL(SQL_CREATE_PREF);
	}
	
	public TrappDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		db.execSQL(SQL_DELETE_ENTRIES1);
		onCreate(db);
	}
	

}

