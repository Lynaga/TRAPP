package com.example.therunningapp;

import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.therunningapp.TrappContract.TrappEntry;

public class Settings extends Activity {
	private String talk;
	private Spinner talkspinner;
	private SharedPreferences pref;
	public static String soundpackage = "en";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//Getting the DB and doing a query to get the info
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_NAME, TrappEntry.COLUMN_NAME_WEIGHT, 
												TrappEntry.COLUMN_NAME_HEIGHT, TrappEntry.COLUMN_NAME_AGE
												,TrappEntry.COLUMN_NAME_GENDER};
		
		Cursor c = db.query(TrappEntry.TABLE_NAMEPREF, projection, null, null,null,null,null);

		//If the database contains data, get's the data and set the text in textview for display and/or editing
		if(c.moveToFirst()){
				String name = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_NAME));
				String weight = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_WEIGHT));
				String height = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_HEIGHT));
				String age = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_AGE));
				String gender = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_GENDER));
				
				EditText name1 = (EditText) findViewById(R.id.editText_name);
				EditText height1 = (EditText) findViewById(R.id.editText_height);
				EditText weight1 = (EditText) findViewById(R.id.editText_weight);
				EditText age1 = (EditText) findViewById(R.id.editText_age);
				Spinner genderspinner = (Spinner) findViewById(R.id.spinner_gender);
				
				name1.setText(name);
				height1.setText(height);
				weight1.setText(weight);
				age1.setText(age);
				
				@SuppressWarnings("unchecked")
				ArrayAdapter<String> myAdap = (ArrayAdapter<String>) genderspinner.getAdapter(); //cast to an ArrayAdapter
				int spinnerPosition = myAdap.getPosition(gender);
				//set the default according to value
				genderspinner.setSelection(spinnerPosition);
		}
		db.close();
		
		talkspinner = (Spinner) findViewById(R.id.spinner_talking);
		pref = getSharedPreferences(talk, MODE_PRIVATE);
		talk = pref.getString("speech", "null");
		
		@SuppressWarnings("unchecked")
		ArrayAdapter<String> myAdap = (ArrayAdapter<String>) talkspinner.getAdapter(); //cast to an ArrayAdapter
		int spinnerPosition = myAdap.getPosition(talk);
		//set the default according to value
		talkspinner.setSelection(spinnerPosition);
	}




		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}
	
	public void save(View view){
		//spinner for speech
		talk = String.valueOf(talkspinner.getSelectedItem());
		SharedPreferences.Editor edit = pref.edit();
        edit.putString("speech", talk);
        edit.commit();
		
        if(talk.equals("English")||talk.equals("Engelsk"))
        	soundpackage = "en";
        else if(talk.equals("Norwegian")||talk.equals("Norsk"))
        	soundpackage = "no";
        else if(talk.equals("For fun")||talk.equals("For moro"))
        	soundpackage = "funny";
        
		//get the DB
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();

		//setup the EditText fields
		EditText name = (EditText) findViewById(R.id.editText_name);
		EditText height = (EditText) findViewById(R.id.editText_height);
		EditText weight = (EditText) findViewById(R.id.editText_weight);
		EditText age = (EditText) findViewById(R.id.editText_age);
		Spinner genderspinner = (Spinner)findViewById(R.id.spinner_gender);
		
		//Get's the strings from EditText fields
		String namestring = name.getText().toString();
		String heightstring = height.getText().toString();
		String weightstring = weight.getText().toString();
		String agestring = age.getText().toString();
		String genderstring = genderspinner.getSelectedItem().toString();
		
		ContentValues values = new ContentValues();
		//Puts the data into the contentvalues
		values.put(TrappEntry.COLUMN_NAME_NAME, namestring);
		values.put(TrappEntry.COLUMN_NAME_HEIGHT, heightstring);
		values.put(TrappEntry.COLUMN_NAME_WEIGHT, weightstring);
		values.put(TrappEntry.COLUMN_NAME_AGE, agestring);
		values.put(TrappEntry.COLUMN_NAME_GENDER, genderstring);
		String selection = TrappEntry.COLUMN_NAME_NAME + " LIKE ?";
		//String[] selectionArgs = { String.valueOf(rowId) };
		
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_NAME, TrappEntry.COLUMN_NAME_WEIGHT, 
				TrappEntry.COLUMN_NAME_HEIGHT, TrappEntry.COLUMN_NAME_AGE , TrappEntry.COLUMN_NAME_GENDER};
		Cursor c = db.query(TrappEntry.TABLE_NAMEPREF, projection, null, null,null,null,null);
		//check is data is in the database if so it updates it, els it creates a entry
		int test = c.getCount();
		
		if(test >= 1){
			db.update(TrappEntry.TABLE_NAMEPREF, values, "_id "+"="+1, null);
		}
		else
		db.insert(TrappEntry.TABLE_NAMEPREF, null, values);

		
		//db.update(TrappEntry.TABLE_NAMEPREF, values, selection, selectionArgs);
		db.close();
		finish();
	}
	
	public void norwegian(View view){
		language("no");
	}
	
	public void english(View view){
		language("en");
	}
	
	public void language(String ch){
		Locale mLocale = null;
		
		if(ch == "en")
		{
			mLocale = new Locale("");
			soundpackage = "en";
		}
		else if(ch == "no")
		{
			mLocale = new Locale("no");
			soundpackage = "no";
		}
		
	    Locale.setDefault(mLocale); 
	    Configuration config = getBaseContext().getResources().getConfiguration(); 
	    if (!config.locale.equals(mLocale)) 
	    { 
	        config.locale = mLocale; 
	        getBaseContext().getResources().updateConfiguration(config, null);
	    }
	    
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);	
	}
}
