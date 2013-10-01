package com.example.therunningapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.therunningapp.TrappContract.TrappEntry;

public class TrappDBHelper extends SQLiteOpenHelper {
	
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "TRAPP.db";
	
	//table for each workout
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " 
				+ TrappEntry.TABLE_NAME +
				" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_DATE + " TEXT, "
				+ TrappEntry.COLUMN_NAME_TIME + " INTEGER, " + TrappEntry.COLUMN_NAME_DISTANCE + " INTEGER, " 
				+ TrappEntry.COLUMN_NAME_CALORIES + " INTEGER)";
	
	
	//table for storing preferances
	private static final String SQL_CREATE_ENTRIES1 = "CREATE TABLE " 
			+ TrappEntry.TABLE_NAMEPREF +
			" (" + TrappEntry._ID + " INTEGER PRIMARY KEY, " + TrappEntry.COLUMN_NAME_NAME + " TEXT, "
			+ TrappEntry.COLUMN_NAME_WEIGHT + " INTEGER, " + TrappEntry.COLUMN_NAME_HEIGHT + " INTEGER)";
	
	//Delete table for workout
	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAME;
	//delete table for preferences
	private static final String SQL_DELETE_ENTRIES1 = "DROP TABLE IF EXISTS " + TrappEntry.TABLE_NAMEPREF;
	
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES1);
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

