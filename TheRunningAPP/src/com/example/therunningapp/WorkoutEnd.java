package com.example.therunningapp;

import com.example.therunningapp.TrappContract.TrappEntry;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class WorkoutEnd extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the DB
		setContentView(R.layout.activity_workout_end);
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		//Set the TextView
		TextView viewDate = (TextView) findViewById(R.id.textdate_display);
		TextView viewCalories = (TextView) findViewById(R.id.textcalories_display);
		TextView viewDistance = (TextView) findViewById(R.id.textdistance_display);
		TextView viewTime = (TextView) findViewById(R.id.texttime_display);
		//Query the DB
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_DATE, TrappEntry.COLUMN_NAME_CALORIES, TrappEntry.COLUMN_NAME_DISTANCE, TrappEntry.COLUMN_NAME_TIME};
		final Cursor c = db.query(TrappEntry.TABLE_NAME, projection, null, null,null,null,null);
		
		//Display the last workout
		if(c.moveToLast()){
			String date = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DATE));
			String calories = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_CALORIES));
			String distance = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE));
			String time = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_TIME));

			//Formatting time from milliseconds to hh:mm:ss
			double tempTime = Integer.parseInt(time);
			double hours = (int) (tempTime / (1000 * 60 * 60));
			double minutes = ((tempTime / (1000 * 60)) % 60);
			double seconds = ((tempTime / 1000) % 60);
			StringBuilder sb = new StringBuilder();
			
			//Adding a 0 before hours, minutes and seconds if their values < 10 (to keep format correct)
			if(hours < 10)
				sb.append("0");
			sb.append(String.format("%.0f", hours) + ":");
			if(minutes < 10)	
				sb.append("0");
			sb.append(String.format("%.0f", minutes) + ":");
			if(seconds < 10)
				sb.append("0");
			sb.append(String.format("%.0f", seconds));
			time = sb.toString();
			
			viewDate.setText(date);
			viewDistance.setText(distance + "m");
			viewTime.setText(time);
			viewCalories.setText(calories);
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workout_end, menu);
		return true;
	}
	
	@Override
	public void onBackPressed(){
		Intent intent = new Intent(this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);	
	}

}
