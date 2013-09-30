package com.example.therunningapp;

import android.app.Activity;
import android.provider.BaseColumns;

public class TrappContract extends Activity {

	public TrappContract(){
		
	}
	
	public static abstract class TrappEntry implements BaseColumns {
		
		public static final String TABLE_NAME = "entry";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_DISTANCE = "distance";
		public static final String COLUMN_NAME_TIME = "time";
		public static final String COLUMN_NAME_CALORIES = "calories";
		
	}

}