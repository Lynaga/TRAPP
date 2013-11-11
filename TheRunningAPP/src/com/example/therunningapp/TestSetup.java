package com.example.therunningapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class TestSetup extends Activity {
	String testType;
	int test;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_setup);
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.test_setup, menu);
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
	

	
	public void onRadioButtonClicked(View view){
		boolean checked = ((RadioButton) view).isChecked();
		
		switch(view.getId()){
			case R.id.Button_Time:
				if(checked){
					findViewById(R.id.sec).setVisibility(View.VISIBLE);
					findViewById(R.id.min).setVisibility(View.VISIBLE);
					findViewById(R.id.Time_sec).setVisibility(View.VISIBLE);
					findViewById(R.id.Time_Min).setVisibility(View.VISIBLE);
					findViewById(R.id.distance).setVisibility(View.GONE);
					findViewById(R.id.Distance).setVisibility(View.GONE);
					testType = "";
				}break;
			case R.id.Button_Distance:
				if(checked){
					findViewById(R.id.distance).setVisibility(View.VISIBLE);
					findViewById(R.id.Distance).setVisibility(View.VISIBLE);
					findViewById(R.id.sec).setVisibility(View.GONE);
					findViewById(R.id.min).setVisibility(View.GONE);
					findViewById(R.id.Time_sec).setVisibility(View.GONE);
					findViewById(R.id.Time_Min).setVisibility(View.GONE);
					
					testType = "Distance";
				}
				break;
		}
	}
	
	
	
	public void starttest(View view){
		int min = 0;
		int sec = 0;
		int distances = 0;
		String workoutType = "Test";
		//setup the EditText fields
		EditText time_min = (EditText) findViewById(R.id.min);
		EditText time_sec = (EditText) findViewById(R.id.sec);
		EditText distance = (EditText) findViewById(R.id.distance);
		
		//Get's the strings from EditText fields
		if(testType == "Distance"){
			distances = Integer.parseInt(distance.getText().toString());
		}
		else{
		min = Integer.parseInt(time_min.getText().toString());
		sec = Integer.parseInt(time_sec.getText().toString());
		}
		
		
		//Intent
		Intent intent = new Intent(this, WorkoutStart.class);
		intent.putExtra("testType", testType);
		intent.putExtra("lengder", distances);	
		intent.putExtra("min", min);
		intent.putExtra("sec", sec);
		intent.putExtra("workoutType", workoutType);
		startActivity(intent);
	}

}

