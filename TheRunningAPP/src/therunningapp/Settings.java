package therunningapp;

import project.therunningapp.R;
import therunningapp.TrappContract.TrappEntry;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends Activity {
	
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
	}
	
	public void save(View view){
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
}
