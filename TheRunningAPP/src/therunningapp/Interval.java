package therunningapp;

import project.therunningapp.R;
import project.therunningapp.R.string;
import therunningapp.TrappContract.TrappEntry;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ToggleButton;


public class Interval extends Activity {
	String intervalType;
	String Interval = "Interval";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_interval);
		// Show the Up button in the action bar.
		setupActionBar();
		intervalType = "time";
		
		//Get the DB
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		ListView workoutList = (ListView) findViewById(R.id.listViewInterval); 
		workoutList.setClickable(true);
		
		String sortOrder = TrappEntry._ID + " DESC";
		
		//Query the DB
				final Cursor c = db.query(TrappEntry.TABLE_NAME_INTERVAL, null, null, null, null, null, sortOrder); 
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
				
				//Display the name of interval
				if(c.moveToFirst()){
					do{
						String name = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_NAME));
						adapter.add(name);
					}while(c.moveToNext());
					
					workoutList.setAdapter(adapter);
					// when you push an item on listview, you get these intents
					workoutList.setOnItemClickListener(new OnItemClickListener() {
						  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							  c.moveToPosition(position);	
						      Intent intent = new Intent(Interval.this, WorkoutStart.class);
						      intent.putExtra("run", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_RUN_TIME)));	
							  intent.putExtra("pause", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_PAUSE_TIME)));
							  intent.putExtra("rep", c.getInt(c.getColumnIndex(TrappEntry.COLUMN_NAME_REPETITION)));
							  intent.putExtra("intervalType", c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_INTERVALTYPE)));
							  intent.putExtra("workoutType", Interval);
							  intent.putExtra("workoutname", c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_NAME)));
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
					findViewById(R.id.ToggleButton_time_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.ToggleButton_time_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_run_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_distance_run_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_distance_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_run_meter).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_pause_meter).setVisibility(View.GONE);
					intervalType = "time";
				}break;
			case R.id.A_radiobutton_distance:
				if(checked){
					findViewById(R.id.textView_time_run_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_time_run_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_time_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.editText_time_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.ToggleButton_time_run_interval).setVisibility(View.GONE);
					findViewById(R.id.ToggleButton_time_pause_interval).setVisibility(View.GONE);
					findViewById(R.id.textView_distance_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_distance_run_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.editText_distance_pause_interval).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_run_meter).setVisibility(View.VISIBLE);
					findViewById(R.id.textView_distance_pause_meter).setVisibility(View.VISIBLE);
					intervalType = "distance";
				}break;
		}
	}
	
	public void save(View view){
		int run = 0;
		int pause = 0;
		int rep = 0;
		String name = "";
		
		//open database
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		EditText run_time = (EditText) findViewById(R.id.editText_time_run_interval);
		EditText pause_time = (EditText) findViewById(R.id.editText_time_pause_interval);
		EditText run_distance = (EditText) findViewById(R.id.editText_distance_run_interval);
		EditText pause_distance = (EditText) findViewById(R.id.editText_distance_pause_interval);
		EditText repitition = (EditText) findViewById(R.id.editText_repetition_interval);
		EditText name1 = (EditText) findViewById(R.id.editText_name_interval);
		
		//add different values in int's from editText
		if(intervalType == "time")
		{	
			if(!isEmpty(run_time))
				{
				run = Integer.parseInt(run_time.getText().toString());
				run = onToggleClicked(findViewById(R.id.ToggleButton_time_run_interval), run);
				}
			if(!isEmpty(pause_time))
				{
				pause = Integer.parseInt(pause_time.getText().toString());
				pause = onToggleClicked(findViewById(R.id.ToggleButton_time_pause_interval), pause);
				}
		}
		else if(intervalType == "distance")
		{
			if(!isEmpty(run_distance))
				run = Integer.parseInt(run_distance.getText().toString());
			if(!isEmpty(pause_distance))
				pause = Integer.parseInt(pause_distance.getText().toString());
		}
		// if something is empty, you will get a popup that you didn't enter all the info needed..
		if(!isEmpty(repitition))
			rep = Integer.parseInt(repitition.getText().toString());
		if(!isEmpty(name1))
			name = name1.getText().toString();
		
		if((!isEmpty(run_time) && !isEmpty(pause_time)) || (!isEmpty(run_distance) && !isEmpty(pause_distance)))
		{
			if(!isEmpty(repitition) && !isEmpty(name1)){//save values in database
				ContentValues values = new ContentValues();
				values.put(TrappEntry.COLUMN_NAME_NAME, name);
				values.put(TrappEntry.COLUMN_NAME_RUN_TIME, run);
				values.put(TrappEntry.COLUMN_NAME_PAUSE_TIME, pause);
				values.put(TrappEntry.COLUMN_NAME_REPETITION, rep);
				values.put(TrappEntry.COLUMN_NAME_INTERVALTYPE, intervalType);
				db.insert(TrappEntry.TABLE_NAME_INTERVAL, null, values);
				db.close();
		
				//put values in intent and send them to an new activity
				Intent intent = new Intent(this, WorkoutStart.class);
				intent.putExtra("run", run);	
				intent.putExtra("pause", pause);
				intent.putExtra("rep", rep);
				intent.putExtra("workoutType", Interval);
				intent.putExtra("intervalType", intervalType);
				intent.putExtra("workoutname", name);
				startActivity(intent);
				finish(); 
			}
			else
				sintmelding();
		}
		else
			sintmelding();
		
	}
	
	public int onToggleClicked(View view, int time) {
	    boolean on = ((ToggleButton) view).isChecked();
	    
	    if (on) {
	        return time*60; // Minutes
	    } else {
	        return time; // Seconds
	    }
	}
	
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().length() > 0) {
	        return false;
	    } else 
	        return true;
	}
	// message you get when you didn't enter all the needed info
	public void sintmelding(){
		new AlertDialog.Builder(this)
	    .setTitle("Error")
	    .setMessage("You did not fill out all the information about your interval-workout,"
	    		+ " do you want to start a plain Running-workout?")
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
	
	public void running () { 
		Intent intent = new Intent(this, WorkoutStart.class);
		String running = "Running";
		intent.putExtra("workoutType", running);
		startActivity(intent);									
	}
}
