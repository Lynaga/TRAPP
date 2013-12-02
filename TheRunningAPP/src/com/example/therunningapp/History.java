package com.example.therunningapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.therunningapp.TrappContract.TrappEntry;

public class History extends Activity {

	String delete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		//Get the DB
		final TrappDBHelper mDbHelper = new TrappDBHelper(this);
		final SQLiteDatabase db = mDbHelper.getReadableDatabase();

		final ListView workoutList = (ListView) findViewById(R.id.listViewHistory);
		workoutList.setClickable(true);
		
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_DATE, TrappEntry._ID};
		String sortOrder = TrappEntry._ID + " DESC";
		
		//Query the DB
		final Cursor c = db.query(TrappEntry.TABLE_NAME, projection, null, null,null,null,sortOrder);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		//Display the date of each workout
		if(c.moveToFirst()){
			findViewById(R.id.textView_empty_history).setVisibility(View.GONE);
			do{
				String date = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DATE));
				adapter.add(date);
			}while(c.moveToNext());
			
			workoutList.setAdapter(adapter);
			
			workoutList.setOnItemClickListener(new OnItemClickListener() {
				  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					  c.moveToPosition(position);	
				      Intent intent = new Intent(History.this, WorkoutDisplay.class);
				      intent.putExtra("id", c.getString(c.getColumnIndex(TrappEntry._ID)));
				      startActivity(intent);          
				                }
				            }); 
		}
		else{
			findViewById(R.id.textView_empty_history).setVisibility(View.VISIBLE);
		}
		
		workoutList.setOnItemLongClickListener(new OnItemLongClickListener() {
		    public boolean onItemLongClick(AdapterView<?> parent, View view,
		                int position, long id) {
		    		c.moveToPosition(position);
		    		delete = new Integer(c.getInt(c.getColumnIndex(TrappEntry._ID))).toString();
		    		dialog();
		            return true;
		        }
		    });	
		
		db.close();
	}
	
	public void dialog(){

		new AlertDialog.Builder(this)
	    .setTitle("Delete Entry")
	    .setMessage("Are you sure you want to delete this workout?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	delete();

	        }
	     })
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })
	     .show();
		
	}
	
	public void delete(){
		final TrappDBHelper mDbHelper = new TrappDBHelper(this);
		final SQLiteDatabase db = mDbHelper.getReadableDatabase();
    	
    	//String delete = toString()c.getInt(c.getColumnIndex(TrappEntry._ID));
        Log.v("long clicked","delte: " + delete);
    	db.delete(TrappEntry.TABLE_NAME, "_ID ="+delete, null);
    	Intent intent = getIntent();
    	finish();
    	startActivity(intent);
		
	}
	
}






