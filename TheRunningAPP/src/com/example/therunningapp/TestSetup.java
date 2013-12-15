package com.example.therunningapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.example.therunningapp.TrappContract.TrappEntry;

public class TestSetup extends Activity {
	String testType;
	String type = "Test";
	int test;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_setup);
		// Show the Up button in the action bar.
		setupActionBar();
		
		
		//Get the DB
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		ListView workoutList = (ListView) findViewById(R.id.listView1); 
		workoutList.setClickable(true);
		

		
		//Query the DB
			final Cursor c = db.query(TrappEntry.TABLE_TESTS, null, null, null, null, null, null); 
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
				
			//Display the name of tests
			if(c.moveToFirst()){
				do{
					String name = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_NAME));
					adapter.add(name);
				}while(c.moveToNext());
					
				workoutList.setAdapter(adapter);
					// listener to send the needed data to the workoutstart to setup the desired test
				workoutList.setOnItemClickListener(new OnItemClickListener() {
					  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						  c.moveToPosition(position);	
					      Intent intent = new Intent(TestSetup.this, WorkoutStart.class);
					      intent.putExtra("lengder", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE)));	
						  intent.putExtra("min", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_MIN)));
						  intent.putExtra("sec", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_SEC)));
						  intent.putExtra("testType", c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_TEST_TYPE)));
						  intent.putExtra("chose", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_CHOSE)));
						  intent.putExtra("workoutType", type);
						  startActivity(intent);
						  finish();          
						  }
					});	
				} 
			db.close();
	}


	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
		// Makes the edit text views visable or invisible depending on the selection in the radio button
		// Set's the value of the testype accordingly
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
		// the variables for the data to be stored in, depening on the selection in radiobutton
		int min = 0;
		int sec = 0;
		int distances = 0;
		String workoutType = "Test";
	
	
		//setup the EditText fields
		EditText time_min = (EditText) findViewById(R.id.min);
		EditText time_sec = (EditText) findViewById(R.id.sec);
		EditText distance = (EditText) findViewById(R.id.distance);
		
		
		//Checks if the test type is distance
		if(testType == "Distance"){
			//If the field is not empty it get's the data, els it pops up an alert dialog for the user
			if(isEmpty(distance)){
				distances = Integer.parseInt(distance.getText().toString());
			}
			else
			{
				new AlertDialog.Builder(this)
			    .setTitle("Error")
			    .setMessage("You did not enter a distance, do you want to start a plain workout?")
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			        	running();
			        }
			     })
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			     .show();
			}
			
		}
		//If the testtype is not distance it is then a time based test
		else{
		
			if(isEmpty(time_min)){
				min = Integer.parseInt(time_min.getText().toString());
			}
			if(isEmpty(time_sec)){
				sec = Integer.parseInt(time_sec.getText().toString());
			}
			// If user has not typed in anything in the fields for setting up a time based test
			if(!isEmpty(time_sec) && !isEmpty(time_min))
			{
				new AlertDialog.Builder(this)
			    .setTitle("Error")
			    .setMessage("You did not enter a test time, do you want to start a plain workout?")
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            running();
			        }
			     })
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			     .show();
			}
		}
		
		
		
		//Check if the user has  typed in anything in distance or min/sec, then start a workout based on the data user entered
	if((isEmpty(time_sec) || isEmpty(time_min)) || isEmpty(distance) ){			
		
		//Intent sent to workoutstart
		int temp = 6;
		Intent intent = new Intent(this, WorkoutStart.class);
		intent.putExtra("testType", testType);
		intent.putExtra("lengder", distances);	
		intent.putExtra("min", min);
		intent.putExtra("sec", sec);
		intent.putExtra("workoutType", workoutType);
		intent.putExtra("chose", temp);
		startActivity(intent);
		finish();
		}
	}
	
	
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
	public void running () { 
		Intent intent = new Intent(this, WorkoutStart.class);
		String running = "Running";
		intent.putExtra("workoutType", running);
		startActivity(intent);									
	}
}

