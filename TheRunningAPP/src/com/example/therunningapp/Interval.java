package com.example.therunningapp;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.support.v4.app.NavUtils;


public class Interval extends Activity {
	String intervalType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interval);
		// Show the Up button in the action bar.
		setupActionBar();
		intervalType = "time";
		
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
		getMenuInflater().inflate(R.menu.interval, menu);
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
	
	//switch for radiobuttons, which only hide/show elements on the screen 
	// based on what radiobutton is checked.
	public void onRadioButtonClicked(View view){
		boolean checked = ((RadioButton) view).isChecked();
		
		switch(view.getId()){
			case R.id.A_radiobutton_time:
				if(checked){
					findViewById(R.id.textView_time_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_time_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_time_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_time_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_run_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_distance_run_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_distance_pause_interval).setVisibility(View.GONE);
					intervalType = "time";
				}break;
			case R.id.A_radiobutton_distance:
				if(checked){
					findViewById(R.id.textView_time_run_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_time_run_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_time_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_time_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_distance_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_distance_pause_interval).setVisibility(View.VISIBLE);
					intervalType = "distance";
				}break;
		}
	}
	
	public void cancel(View view){
		finish();
	}
	
	public void save(View view){
		int run = 0;
		int pause = 0;
		int rep = 0;
		String Interval = "Interval";
		
		if(intervalType == "time")
		{
			EditText run_time = (EditText) findViewById(R.id.editText_time_run_interval);
			EditText pause_time = (EditText) findViewById(R.id.editText_time_pause_interval);
	
			run = Integer.parseInt(run_time.getText().toString());
			pause = Integer.parseInt(pause_time.getText().toString());
		}
		else if(intervalType == "distance")
		{
			EditText run_distance = (EditText) findViewById(R.id.editText_distance_run_interval);
			EditText pause_distance = (EditText) findViewById(R.id.editText_distance_pause_interval);
	
			run = Integer.parseInt(run_distance.getText().toString());
			pause = Integer.parseInt(pause_distance.getText().toString());
		}
		
		EditText repitition = (EditText) findViewById(R.id.editText_repetition_interval);
		rep = Integer.parseInt(repitition.getText().toString());
			
		Intent intent = new Intent(this, WorkoutStart.class);
	//	intent.putExtra("intervalType", intervalType);
		intent.putExtra("run", run);	
		intent.putExtra("pause", pause);
		intent.putExtra("rep", rep);
		intent.putExtra("workoutType", Interval);
		startActivity(intent); 
		finish();
	}
}
