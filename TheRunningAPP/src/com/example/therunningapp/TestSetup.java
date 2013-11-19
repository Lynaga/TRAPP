package com.example.therunningapp;

import com.example.therunningapp.TrappContract.TrappEntry;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.AdapterView.OnItemClickListener;

public class TestSetup extends Activity {
	String testType;
	String type = "test";
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
		
		ListView workoutList = (ListView) findViewById(R.id.listViewInterval); 
		workoutList.setClickable(true);
		
		String sortOrder = TrappEntry._ID + " DESC";
		
		//Query the DB
				final Cursor c = db.query(TrappEntry.TABLE_TESTS, null, null, null, null, null, sortOrder); 
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
				
				//Display the name of test
				if(c.moveToFirst()){
					do{
						String name = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_NAME));
						adapter.add(name);
					}while(c.moveToNext());
					
					workoutList.setAdapter(adapter);
					
					workoutList.setOnItemClickListener(new OnItemClickListener() {
						  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							  c.moveToPosition(position);	
						      Intent intent = new Intent(TestSetup.this, WorkoutStart.class);
						      intent.putExtra("lengder", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE)));	
							  intent.putExtra("min", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_MIN)));
							  intent.putExtra("sec", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_SEC)));
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
		finish();
	}

}

