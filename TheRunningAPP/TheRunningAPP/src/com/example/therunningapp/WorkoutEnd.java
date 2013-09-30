package com.example.therunningapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class WorkoutEnd extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_end);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workout_end, menu);
		return true;
	}

}
