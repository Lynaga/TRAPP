package therunningapp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import project.therunningapp.R;
import therunningapp.TrappContract.TrappEntry;
import therunningapp.WorkoutStart.myLatLng;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RouteSuggestion extends Activity {

	int minDistance, maxDistance;
	List<myLatLng> locationList = new ArrayList<myLatLng>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_suggestion);
	}
	
	public void suggestRoutes(View view) {
		//Get text from EditTexts and change visibilities
		final ListView displayRoutes = (ListView) findViewById(R.id.T_display_routes);
		EditText minText = (EditText) findViewById(R.id.T_min_distance_input);
		EditText maxText = (EditText) findViewById(R.id.T_max_distance_input);
		minText.setVisibility(View.GONE);
		maxText.setVisibility(View.GONE);
		findViewById(R.id.T_max_distance).setVisibility(View.GONE);
		findViewById(R.id.T_min_distance).setVisibility(View.GONE);
		displayRoutes.setVisibility(View.VISIBLE);
		minDistance = Integer.parseInt(minText.getText().toString());
		maxDistance = Integer.parseInt(maxText.getText().toString());
		
		//Get the database
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		//Querying the db
		String sql = "SELECT * FROM " + TrappEntry.TABLE_NAME + " WHERE " + TrappEntry.COLUMN_NAME_DISTANCE
							  + " > " + minDistance + " AND " + TrappEntry.COLUMN_NAME_DISTANCE + " < " + maxDistance
							  + " ORDER BY " + TrappEntry.COLUMN_NAME_DISTANCE + " ASC";
		final Cursor c = db.rawQuery(sql, null);
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		
		if(c.moveToFirst()) {	//If any results from the db
			do{
				byte[] locations = c.getBlob(c.getColumnIndex(TrappEntry.COLUMN_NAME_LOCATIONS));
				
				try { 	//Deserialize from blob to list
				      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(locations)); 
				      locationList = (List<myLatLng>) in.readObject();
				      in.close(); 
				    } catch(ClassNotFoundException cnfe) { }
				catch(IOException ioe) { } 
				//Add new entry to the adapter, displayed by its distance
				adapter.add(c.getString(c.getColumnIndex(TrappEntry.COLUMN_NAME_DISTANCE)) + " m");
			} while(c.moveToNext());
			
			//Adding content of adapter to ListView
			displayRoutes.setAdapter(adapter);
			
			//Initiate onClickListener for the ListView
			displayRoutes.setOnItemClickListener(new OnItemClickListener() {
				  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					  c.moveToPosition(position);												//On item clicked;
				      Intent intent = new Intent(RouteSuggestion.this, WorkoutDisplay.class);	//Get the position of the item,
				      intent.putExtra("id", c.getString(c.getColumnIndex(TrappEntry._ID)));		//get the database id of the entry
				      intent.putExtra("suggested", 1);											//and put as extra to retrieve
				      startActivity(intent); 													//correct entry in next activity.
				                }
				            }); 
		}
	}
	
}