package com.example.therunningapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.therunningapp.TrappContract.TrappEntry;
import com.example.therunningapp.WorkoutStart.myLatLng;
import com.google.android.gms.location.LocationClient;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RouteSuggestion extends Activity {

	int minDistance, maxDistance, routesWithinDistance;
	LocationClient myLocationClient;
	Location myLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_suggestion);
		
		List<myLatLng> locationList = new ArrayList<myLatLng>();
		final ListView workoutList;/*Get Listview !!!!!!!!!!!!!!!!!!*/
		//Get the database
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		/*Get values for minDistance, maxDistance and routesWithinDistance from user */
		String sql = "SELECT * FROM " + TrappEntry.TABLE_NAME + " WHERE " + TrappEntry.COLUMN_NAME_DISTANCE
							  + " >= " + minDistance + " AND " + TrappEntry.COLUMN_NAME_DISTANCE + " =< " + maxDistance
							  + " ORDER BY " + TrappEntry.COLUMN_NAME_DISTANCE + " ASC";
		Cursor c = db.rawQuery(sql, null);
		
		myLocation = myLocationClient.getLastLocation();
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		if(c.moveToFirst()) {
			Location temp1 = new Location("Temp location 1");
			Location temp2 = new Location("Temp location 2");
			Location temp3 = new Location("Temp location 3");
			do{
				double tempSize = locationList.size();
				byte[] locations = c.getBlob(c.getColumnIndex(TrappEntry.COLUMN_NAME_LOCATIONS));
				
				try { 
				      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(locations)); 
				      locationList = (List<myLatLng>) in.readObject();
				      in.close(); 
				    } catch(ClassNotFoundException cnfe) { 
				      Log.e("deserializeObject", "class not found error", cnfe); 
				    } catch(IOException ioe) { 
				      Log.e("deserializeObject", "io error", ioe); 
				  } 
				temp1.setLatitude(locationList.get(0).lat);
				temp1.setLongitude(locationList.get(0).lng);
				temp2.setLatitude(locationList.get((int) tempSize / 2).lat);
				temp2.setLongitude(locationList.get((int) tempSize / 2).lng);
				temp3.setLatitude(locationList.get((int) tempSize - 1).lat);
				temp3.setLongitude(locationList.get((int) tempSize - 1).lng);
				
				if(temp1.distanceTo(myLocation) < routesWithinDistance
				|| temp2.distanceTo(myLocation) < routesWithinDistance
				|| temp3.distanceTo(myLocation) < routesWithinDistance) {
				String distance = c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE));
				adapter.add(distance);
				}
			} while(c.moveToNext());
			
			workoutList.setAdapter(adapter);
			
			workoutList.setOnItemClickListener(new OnItemClickListener() {
				  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					  c.moveToPosition(position);	
				      Intent intent = new Intent(WorkoutEnd.this, WorkoutDisplay.class);
				      intent.putExtra("id", c.getString(c.getColumnIndex(TrappEntry._ID)));
				      startActivity(intent);          
				                }
				            }); 
		}
	}
}