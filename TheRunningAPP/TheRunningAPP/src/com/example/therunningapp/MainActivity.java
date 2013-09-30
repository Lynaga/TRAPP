package com.example.therunningapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.therunningapp.TrappContract.TrappEntry;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void settings (View view) {
		Intent intent = new Intent(this, Settings.class);
		startActivity(intent);

	}

	public void history (View view) {
		Intent intent = new Intent(this, History.class);
		startActivity(intent);

	}
	
	/*
	 * public void workout (View view) {
		Intent intent = new Intent(this, Workout.class);
		startActivity(intent);

	}*/
	public void db(View view){
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TrappEntry.COLUMN_NAME_DATE, "30.09.2013");

		
		db.insert(TrappEntry.TABLE_NAME, null, values);
		
		db.close();


	}
	public void stigaasen(){
		
	}
	
	public void andersnyen(){
		
	}
	
}
