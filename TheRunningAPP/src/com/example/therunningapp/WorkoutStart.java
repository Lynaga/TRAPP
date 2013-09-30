package com.example.therunningapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class WorkoutStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_start);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workout_start, menu);
		return true;
	}

}
