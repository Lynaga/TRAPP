package com.example.therunningapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.therunningapp.TrappContract.TrappEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WorkoutDisplay extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_display);
		// Show the Up button in the action bar.
		// Getting DB
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		setupActionBar();
		
		Intent intent = getIntent();
		String db_id = intent.getStringExtra("id");
		//Setting the TextView
		TextView viewDate = (TextView) findViewById(R.id.date_display);
		TextView viewTime = (TextView) findViewById(R.id.time_display);
		TextView viewDistance = (TextView) findViewById(R.id.distance_display);
		TextView viewCalories = (TextView) findViewById(R.id.calories_display);
		TextView viewSpeed = (TextView) findViewById(R.id.average_speed_display);
		
		//query the DB
		String[] projection = { TrappEntry._ID, TrappEntry.COLUMN_NAME_DATE, TrappEntry.COLUMN_NAME_CALORIES,
								TrappEntry.COLUMN_NAME_DISTANCE, TrappEntry.COLUMN_NAME_TIME,
								TrappEntry.COLUMN_NAME_AVGSPEED, TrappEntry.COLUMN_NAME_LOCATIONS };
		final Cursor c = db.query(TrappEntry.TABLE_NAME, projection, "_ID=?", new String[] { db_id }, null,null,null,null);
		Gson gson = new Gson();
		//Display the workout
		if(c.moveToFirst()){
			String date = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DATE));
			String calories = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_CALORIES));
			String distance = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE));
			String time = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_TIME));
			String avgSpeed = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_AVGSPEED));
			String jsonLocations = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_LOCATIONS));
			
			Type type = new TypeToken<List<Location>>(){}.getType();
			List<Location> locationList = gson.fromJson(jsonLocations, type);
			
			//Formatting time from milliseconds to hh:mm:ss
			int tempTime = Integer.parseInt(time);
			int hours = (int) (tempTime / (1000 * 60 * 60));
			int minutes = ((tempTime / (1000 * 60)) % 60);
			int seconds = ((tempTime / 1000) % 60);
			String tempHours = Integer.toString(hours);
			String tempMinutes = Integer.toString(minutes);
			String tempSeconds = Integer.toString(seconds);
			double tempSpeed = Double.parseDouble(avgSpeed);	//Converting from string to double to format output
			
			StringBuilder sb = new StringBuilder();
			
			//Adding a 0 before hours, minutes and seconds if their values < 10 (to keep format correct)
			if(hours < 10)
				sb.append("0");
			sb.append(tempHours + ":");
			if(minutes < 10)
				sb.append("0");
			sb.append(tempMinutes + ":");
			if(seconds < 10)
				sb.append("0");
			sb.append(tempSeconds);
			time = sb.toString();
			
			String tempTimeString = getString(R.string.A_time_display_string);
			String tempDistanceString = getString(R.string.A_distance_display_string);
			String tempCaloriesString = getString(R.string.A_calories_display_string);
			String tempSpeedString = getString(R.string.A_speed_display_string);
			
			viewDate.setText(date);
			viewTime.setText(tempTimeString + ": " + time);
			viewDistance.setText(tempDistanceString + ": " + distance + " m");
			viewCalories.setText(tempCaloriesString + ": " + calories);
			viewSpeed.setText(tempSpeedString + ": " + String.format("%.2f", tempSpeed) + " m/s");
	}
		
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workout_display, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void back(View view){	//Back button to exit activity
		finish();
	}

}
