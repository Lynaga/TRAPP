package com.example.therunningapp;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.therunningapp.TrappContract.TrappEntry;

public class History extends Activity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		
		
		String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_DATE };
		
		String sortOrder = TrappEntry.COLUMN_NAME_DATE + " DESC";
		
		Cursor c = db.query(TrappEntry.TABLE_NAME, projection, null, null,null,null,sortOrder);
		ListView list = (ListView) findViewById(R.id.listViewHistory);
		list.setClickable(true);
	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

		if(c.moveToFirst()){
			do{
				String date = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DATE));
				adapter.add(date);
			
			}while(c.moveToNext());
			list.setAdapter(adapter);
		}

	
		
/*
		 list.setOnItemClickListener(new OnItemClickListener() {
	          public void onItemClick(AdapterView<?> parent, View view,
	              int position, long id) {
	               
	              // selected item 
	              String date = ((ListView) view).getContext().toString();
	              
	              
	              // Launching new Activity on selecting single List Item
	              Intent i = new Intent(getApplicationContext(), SingleListItem.class);
	              // sending data to new activity
	              i.putExtra("date", date);
	              startActivity(i);
	             
	          }
	        });
*/
		db.close();
	}
	}
/*
	  public void onItemClick(AdapterView<?> l, View v, int position, long id) {
	        Log.i("TAG", "You clicked item " + id + " at position " + position);
	        // Here you start the intent to show the contact details
	    }
}
*/

