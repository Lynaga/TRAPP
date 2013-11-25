package com.example.therunningapp;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.therunningapp.TrappContract.TrappEntry;

public class History extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		//Get the DB
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		ListView workoutList = (ListView) findViewById(R.id.listViewHistory);
		workoutList.setClickable(true);
		
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_DATE};
		String sortOrder = TrappEntry._ID + " DESC";
		
		//Query the DB
		final Cursor c = db.query(TrappEntry.TABLE_NAME, projection, null, null,null,null,sortOrder);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		//Display the date of each workout
		if(c.moveToFirst()){
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
		
		db.close();
	}
}


