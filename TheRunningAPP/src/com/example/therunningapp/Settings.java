package com.example.therunningapp;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.example.therunningapp.TrappContract.TrappEntry;

public class Settings extends Activity {
	
	/*TrappDBHelper mDBsHelper = new TrappDBHelper(this);
	SQLiteDatabase dbs = mDBsHelper.getWritableDatabase();
*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void save(View view){
		
		TrappDBHelper mDBsHelper = new TrappDBHelper(this);
		SQLiteDatabase dbs = mDBsHelper.getWritableDatabase();

		
		EditText name = (EditText) findViewById(R.id.editText1);
		EditText height = (EditText) findViewById(R.id.editText2);
		EditText weight = (EditText) findViewById(R.id.editText3);
		
		String namestring = name.getText().toString();
		String heightstring = height.getText().toString();
		String weightstring = weight.getText().toString();
		
		ContentValues values = new ContentValues();
		
		values.put(TrappEntry.COLUMN_NAME_NAME, namestring);
		values.put(TrappEntry.COLUMN_NAME_HEIGHT, heightstring);
		values.put(TrappEntry.COLUMN_NAME_WEIGHT, weightstring);
		String selection = TrappEntry.COLUMN_NAME_NAME + " LIKE ?";
		//String[] selectionArgs = { String.valueOf(rowId) };
		
		dbs.update(TrappEntry.TABLE_NAMEPREF, values, "_id "+"="+0, null);
		
		//db.update(TrappEntry.TABLE_NAMEPREF, values, selection, selectionArgs);
		dbs.close();
		finish();

		
	}
	
	public void cancel(View view){
		finish();
		
		
	}

}
