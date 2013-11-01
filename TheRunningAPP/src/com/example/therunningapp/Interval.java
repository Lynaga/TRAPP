package com.example.therunningapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.support.v4.app.NavUtils;

public class Interval extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interval);
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
					findViewById(R.id.textView_time_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_time_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_distance_interval).setVisibility(View.GONE);
				}break;
			case R.id.A_radiobutton_distance:
				if(checked){
					findViewById(R.id.textView_time_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_time_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_distance_interval).setVisibility(View.VISIBLE);
				}break;
		}
	}
	
	//function for intervals. With run-time, pause-time and repetitions
	public void Delay(int run, int pause, int rep){
		for(int i = 0; i<rep; i++)
		{
			DelayTime(run);					//Run-time
			//her skal det en lydfunksjon
			
			if(i < rep-1)					// if it's NOT the last rep, Pause-time
			{	DelayTime(pause);
				//her skal det en lydfunksjon
			}
			
			if(i == rep-1)					// if it's the last rep, stop
			{
				//lydfunksjon for stopp
			}
		}
	}
	
	//For handling the delay   ** DENNE ER IKKE FERDIG!!!!** 
	public void DelayTime(final int delaytime){	
		
	}

}
