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
				+ TrappEntry.COLUMN_NAME_CALORIES + " INTEGER)";
	
	//table for storing preferances
	private static final String SQL_CREATE_PREF = "CREATE TABLE " 
			+ TrappEntry.TABLE_NAMEPREF +
			" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_NAME + " TEXT, "
			+ TrappEntry.COLUMN_NAME_WEIGHT + " INTEGER, " + TrappEntry.COLUMN_NAME_HEIGHT + " INTEGER)";
	
	//table for storing Interval
	private static final String SQL_CREATE_INTERVAL = "CREATE TABLE " + TrappEntry.TABLE_NAME_INTERVAL
				+ " (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_NAME + " TEXT, "
				+ TrappEntry.COLUMN_NAME_RUN_TIME + " INTEGER, " + TrappEntry.COLUMN_NAME_PAUSE_TIME + " INTEGER, "
				+ TrappEntry.COLUMN_NAME_REPETITION + " INTEGER)";

	//table for storing Test
		private static final String SQL_CREATE_TEST = "CREATE TABLE " + TrappEntry.TABLE_TESTS + 
				" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_TEST_TYPE + " TEXT, "
				+ TrappEntry.COLUMN_NAME_DISTANCE + "INTEGER, " + TrappEntry.COLUMN_NAME_MIN + "INTEGER, "
				+ TrappEntry.COLUMN_NAME_SEC +"INTEGER)";
	
	//table for storing GPS locations
	private static final String SQL_CREATE_LOCATIONS = "CREATE TABLE " + TrappEntry.TABLE_NAME_LOCATIONS
				+ " (" + TrappEntry._ID + " INTEGER PRIMARY KEY, "
				+ TrappEntry.COLUMN_NAME_WORKOUT + " INTEGER, "
				+ TrappEntry.COLUMN_NAME_LATITUDE + " DOUBLE, "
				+ TrappEntry.COLUMN_NAME_LONGITUDE + " DOUBLE)";

		
	//Delete table for workout
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME;
	//delete table for preferences
	private static final String SQL_DELETE_ENTRIES1 = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAMEPREF;
	//delete table for intervals
	private static final String SQL_DELETE_ENTRIES2 = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME_INTERVAL;

	//delete table for test
	private static final String SQL_DELETE_ENTRIES3 = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_TESTS;
		

	//delete table for GPS locations
	private static final String SQL_DELETE_LOCATIONS = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME_LOCATIONS;
	

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_WORKOUTLOG);
		db.execSQL(SQL_CREATE_PREF);
		db.execSQL(SQL_CREATE_INTERVAL);
		db.execSQL(SQL_CREATE_TEST);
		db.execSQL(SQL_CREATE_LOCATIONS);
	}
	
	public TrappDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		db.execSQL(SQL_DELETE_ENTRIES1);
		db.execSQL(SQL_DELETE_ENTRIES2);
		db.execSQL(SQL_DELETE_ENTRIES3);
		db.execSQL(SQL_DELETE_LOCATIONS);
		onCreate(db);
	}
	

}

