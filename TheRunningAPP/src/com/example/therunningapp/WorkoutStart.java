package com.example.therunningapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.therunningapp.TrappContract.TrappEntry;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class WorkoutStart extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener, SensorEventListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	//Constants to set the update intervals
	private final static int MILLISECONDS_PER_SECOND = 1000;
	private final static int UPDATE_INTERVAL_IN_SECONDS = 1;
	private final static int FASTEST_INTERVAL_IN_SECONDS = 1;
	private final static long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private final static long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	private final static double MAX_DISTANCE = 7;	//Max average distance pr. second when accellerating from 0 (applies for the first 3 seconds)
	
	//Location variables to store user locations
	private Location prevLocation = null;
	
	LocationClient myLocationClient;	//Object to connect to Google location services and request location update callbacks
	GoogleMap myMap;					//Object to get map from fragment
	LocationRequest myLocationRequest;	//Object to set parameters for the requests to the LocationClient
	
	//Objects to access the accelerometer
	SensorManager mySensorManager;
	Sensor mySensor;
	
	//Variables used to pause / restart workout and store to database.
	int timeInterval = 1;	//Time interval since last verified locations (Standard = 1)
	long pauseTime = 0;
	boolean workoutStatus = false;
	double myDistance = 0;
	int tempCounter = 0;
	Chronometer myTimer;
	
	//getting extras
	int min = 0;
	int sec = 0;
	int lengde = 0;
	int test = 0;
	String testType = "0";
	
	double x, y, z, amplitude;	//Used for accelerometer data
	int nextWorkoutID = -1;
	
	MediaPlayer mediaPlayer;
	AudioManager am;
	
	//intervals
	Timer run;
	Timer pause;
	Timer stop;
	boolean TimerRunStart = false;
	boolean TimerPauseStart = false;
	boolean TimerStopStart = false;
	String intervalType;
	String workoutType;
	Bundle extras;
	
	public static class myLatLng implements Serializable {	//Class to read/write lat /lng values
		double lat;
		double lng;
	}
	
	List<myLatLng> locationList = new ArrayList<myLatLng>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_start);
		mediaPlayer = MediaPlayer.create(this, R.raw.pause);
		am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);



		extras = getIntent().getExtras();
		workoutType = extras.getString("workoutType");

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		myLocationClient = new LocationClient(this, this, this);	//Initiate LocationClient
		myTimer = (Chronometer) findViewById(R.id.T_timer);			//Set chronometer to view
		
		//Get map from the fragment
		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment;
		mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
		myMap = mySupportMapFragment.getMap();
		myMap.setMyLocationEnabled(true);	//Enable the my locaton button
		
		//Set parameters for the location updates
		myLocationRequest = LocationRequest.create();
		myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		myLocationRequest.setInterval(UPDATE_INTERVAL);
		myLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.workout_start, menu);
		return true;
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        myLocationClient.connect();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		mySensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mySensorManager.unregisterListener(this, mySensor);
	}
	
	@Override
    protected void onStop() {
        // Disconnecting the client if connected
		if (myLocationClient.isConnected())
        	myLocationClient.removeLocationUpdates(this);
        myLocationClient.disconnect();
        super.onStop();
    }	
	@Override
    public void onDestroy()
    {
		 super.onDestroy();
		 end();

}
   
	 
	@Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        
        //Set map to users location and set initial text
		setCamera(myLocationClient.getLastLocation());
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(18);	
		myMap.animateCamera(zoom);	//Set zoom
		setText();
	}
	
	@Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please reconnect to continue.",
                Toast.LENGTH_SHORT).show();
    }
	
	@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /* The following code was retrieved from developer.android.com,
         * to resolve connection errors.
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user.
             */
        	String T_Errortext = "Google Play services could not resolve the connection problem.";
			TextView T_textView = (TextView) findViewById(R.id.T_distance);
			T_textView.setText(T_Errortext);
        }
    }
	
	//Function to get location updates
	public void onLocationChanged(Location newLocation) {
		myLatLng listLatLng = new myLatLng();
		if (prevLocation == null)	//Check if last location is set
			prevLocation = myLocationClient.getLastLocation();	//If not set -> Update last location
	
		double tempDistance = prevLocation.distanceTo(newLocation);	//Getting distance between last 2 locations
		myDistance = myDistance + tempDistance;	//Updating total distance
		setCamera(newLocation);		//Update map to new location
		setText();		//Update text
		
		double tempLat = newLocation.getLatitude();
		double tempLng = newLocation.getLongitude();
		listLatLng.lat = tempLat;
		listLatLng.lng = tempLng;
		
		LatLng prevLatLng = new LatLng(prevLocation.getLatitude(), prevLocation.getLongitude());
		LatLng newLatLng = new LatLng(tempLat, tempLng);
		
		//Drawing on the map from last location to new location
		myMap.addPolyline(new PolylineOptions()
	     .add(prevLatLng, newLatLng)
	     .width(5)
	     .color(Color.RED).geodesic(true));
	
		locationList.add(listLatLng);
		prevLocation = newLocation;				//Update last location for next update

	}
	
	public void onSensorChanged(SensorEvent event) {
		x = event.values[0];
		y = event.values[1];
		z = event.values[2];
		
		amplitude = Math.sqrt((x*x)+(y*y)+(z*z));
		
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
  
	public void test_check(int test){
		int rounds = 1;
		//Getting data from the intent that comes from TestSetup.java
		int min = extras.getInt("min");
		int sec = extras.getInt("sec");
		int lengde = extras.getInt("lengder");
		String testType = extras.getString("testType");
		
		//variables for checking the test condition and ending the test at apropiate time
		
		
		int value;
		int set = 0;
		//The test checking happends as long as the distance/time is lower than the test value.
		do{
			if(testType.equals("Distance")){
			value = (int) myDistance;
			set = lengde;
		}
		else {
			value = (int) (SystemClock.elapsedRealtime() - myTimer.getBase());
			set = (min * 60000) + (sec * 1000);
			}
		
		if(test == 1 || test == 2){
			int sound = rounds*500;
			if(value > sound){
				sounds(sound);
				rounds++;
			}
		}
		if(test == 3 || test == 4){
			int sound = rounds*1000;
			if (value > sound){
				sounds(sound);
				rounds++;
			}
		}
		if(test == 5)
		{
			int sound = rounds*5;
			if(value > sound){
				sounds(sound);
				rounds++;	
			}
		}
		// sleeps the thread for a second as the gps updates dosn't happend more often.
		SystemClock.sleep(1000);
		}
		// Ends the test when u have reached the value(time or distance)
		while(value <= set);
			end();
	}
	

	// Function to make the music duck while playing notification sounds..
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
	    public void onAudioFocusChange(int focusChange) {
	        if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
	        	
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	        	
	        }
	    }
	};
	
	
	//Function to center map on user
	public void setCamera(Location camLocation) {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(camLocation.getLatitude(),
                												camLocation.getLongitude()));
		myMap.moveCamera(center);
	}
	
	//Function to set and update current distance
	public void setText() {
		int tempDistance = (int) myDistance;
		StringBuilder sb = new StringBuilder();
		
		if(tempDistance > 1000) {		//If user ran more than 1 km, format output to (ex.) "1.23 km"
			int tempKm = tempDistance / 1000;
			int tempM = (tempDistance % 1000) / 100;
			sb.append(tempKm + "." + tempM + " km");
		}
		else {
			sb.append(tempDistance + " m");
		}
		
		//Set new values
		TextView textView = (TextView) findViewById(R.id.T_distance);
		textView.setText(sb.toString());		
		
		TextView tempView = (TextView) findViewById(R.id.T_speed);
		tempView.setText(String.format("%.2f", amplitude));
		
		TextView tempView2 = (TextView) findViewById(R.id.T_calories);
		tempView2.setText("Coming soon");
	}
	
	//Onclick function for the start / pause workout button
	public void workoutStartPause(View view) {
		String tempString;	//String to change the text on button
		Button tempButton;	//Object to change the text on button
		
		if(workoutStatus == false) {	//If workout is not started, or paused
			myTimer.setBase(SystemClock.elapsedRealtime() + pauseTime);	//Sets timer to right start value
			myTimer.start();
		    myLocationClient.requestLocationUpdates(myLocationRequest, this);	//Starts location updates
			
		    if(workoutType.equals("Walk"))
		    	{}
		    else if(workoutType.equals("Running"))
				{}
			else if(workoutType.equals("Interval"))
				{
				new Thread(new Runnable() {
			        public void run() {
			        	Interval();
			        }
			    }).start();
			}
			else if(workoutType.equals("Test"))
			{
				int abcd = 0;
			
				switch(abcd){
				case 1 : new Thread(new Runnable() {
							public void run() {
								test_check( 1 );}
			    			}).start();
				case 2 : new Thread(new Runnable() {
							public void run() {
								test_check( 2 );}
							}).start();
				case 3 : new Thread(new Runnable() {
							public void run() {
								test_check( 3 );}
							}).start();
				case 4 : new Thread(new Runnable() {
			        		public void run() {
			        			test_check( 4 );}
							}).start();
				case 5 : new Thread(new Runnable() {
							public void run() {
								test_check( 5 );}
							}).start();
				case 6 : new Thread(new Runnable() {
							public void run() {
								test_check( 0 );}
							}).start();
				}
			}
		    
		    workoutStatus = true;											//Change workout status
			tempString = getString(R.string.T_pause_workout_button_string);	//Get text for button
		}
		
		else {
			myTimer.stop();
			pauseTime = myTimer.getBase() - SystemClock.elapsedRealtime();	//Stores value of the timer
			
			if (myLocationClient.isConnected())		//If client is connected
	        	myLocationClient.removeLocationUpdates(this);	//remove location updates
			workoutStatus = false;								//Change workout status
			tempString = getString(R.string.T_start_workout_button_string);	//Get text for button
		}
		
		//Set new text for button
		tempButton = (Button) findViewById(R.id.T_pause_workout_button);
		tempButton.setText(tempString);
	}
	
	public void workoutEnd (View view) {
		// stop the loops if it's an Interval
		if(TimerRunStart){
			run.cancel();
			TimerRunStart = false;
		}
		if(TimerPauseStart){
			pause.cancel();
			TimerPauseStart = false;
		}
		if(TimerStopStart)
		{
			stop.cancel();
			TimerStopStart = false;
		}
		
		end();
	}


	public void end() {
			//Get the database
			TrappDBHelper mDBHelper = new TrappDBHelper(this);
			SQLiteDatabase db = mDBHelper.getWritableDatabase();
			myTimer.stop();
			
			String[] projection = {TrappEntry._ID, TrappEntry.COLUMN_NAME_WEIGHT};
			
			Cursor w = db.query(TrappEntry.TABLE_NAMEPREF, projection, null, null,null,null,null);
			
			//Initiate variables needed to write to the database
			int calories = 0;
			Float time;
			pauseTime = SystemClock.elapsedRealtime() - myTimer.getBase();
			time = (float) pauseTime / 3600000;
			
			Date cDate = new Date();
			String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);	//Set the dateformat
			
			if(w.moveToFirst()){	//Checks if the user has set the weight 
				int weight = w.getInt(w.getColumnIndex(TrappEntry.COLUMN_NAME_WEIGHT));
				//If weight is set calculate calories burnt during the workout
				calories = Calorie_math(time, weight);
				}
				
			if(time != 0 && myDistance != 0) {	//Check if users started workout
					//If workout was started -> write to database and start new activity, WorkoutEnd
				ContentValues values = new ContentValues();
				byte[] buff = null;
				
				try {
				    ByteArrayOutputStream bos = new ByteArrayOutputStream();
				    ObjectOutputStream out = new ObjectOutputStream(bos);
				    out.writeObject(locationList);
				    out.close();
				    buff = bos.toByteArray();
				} catch(IOException ioe) { 
				      Log.e("serializeObject", "error", ioe);
				    } 
				
				values.put(TrappEntry.COLUMN_NAME_DATE, fDate);
				values.put(TrappEntry.COLUMN_NAME_DISTANCE, (int) myDistance);
				values.put(TrappEntry.COLUMN_NAME_TIME, pauseTime);
				values.put(TrappEntry.COLUMN_NAME_CALORIES, calories);
				values.put(TrappEntry.COLUMN_NAME_LOCATIONS, buff);
				db.insert(TrappEntry.TABLE_NAME, null, values);
			
				Intent intent = new Intent(this, WorkoutEnd.class);
				startActivity(intent);
				}
			db.close();
			finish();
	}
	
	public void Interval(){
		MediaPlayer mediaPlayerRun = MediaPlayer.create(this, R.raw.run);
		Bundle extras = getIntent().getExtras();
		int run = extras.getInt("run");
		int pause = extras.getInt("pause");
		int rep = extras.getInt("rep");
		String intervalType = extras.getString("intervalType");
		
        mediaPlayerRun.start();
		
		if(intervalType.equals("time"))
			Interval_time(run,pause,rep);
		else if(intervalType.equals("distance"))
			Interval_distance(run,pause,rep);
		else
			end();
	}
	
	public void Interval_distance(int RunDistance, int PauseDistance, int Repetition){
		MediaPlayer mediaPlayerRun = MediaPlayer.create(this, R.raw.run);
		MediaPlayer mediaPlayerPause = MediaPlayer.create(this, R.raw.pause);
		MediaPlayer mediaPlayerStop = MediaPlayer.create(this, R.raw.stop);
        
		Interval_distance(RunDistance);
		
		for(int rep = 1; rep < Repetition; rep++)
		{
			mediaPlayerPause.start(); //pause
			Interval_distance((RunDistance+PauseDistance)*rep);
			
			mediaPlayerRun.start();	//run
			Interval_distance(((RunDistance+PauseDistance)*rep)+RunDistance);
		}
		
		mediaPlayerStop.start();
	}
	
	public void Interval_distance(int Distance){
		int value;
		int set = Distance;
		
		do{
		value = (int) myDistance;
		}while(value <= set);
	}
	
	public void Interval_time(int RunTime, int PauseTime, int Repetition){
        DelayRun(RunTime,PauseTime);
        DelayStop((RunTime+PauseTime)*(Repetition-1)+1 , 1);
        DelayStop((RunTime*Repetition)+(PauseTime*(Repetition-1)) , 2);
	}
	
	public void DelayRun(final int RunTime, final int PauseTime){
		TimerRunStart = true;
		final MediaPlayer mediaPlayerPause = MediaPlayer.create(this, R.raw.pause);
		run = new Timer();
		run.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		    	new Thread(new Runnable() {

		    	    @Override
		    	    public void run() {
		    	        
		    	        mediaPlayerPause.start();
		    	        TimerRunStart = false;
		    	        DelayPause(PauseTime);
		    	        
		    	    }
		    	}).start();
		    } //wait 'RunTime*1000' before it start, and loop every '(PauseTime+RunTime)*1000' (milliseconds)
		},RunTime*1000 , (RunTime+PauseTime)*1000);
	}
	
	public void DelayPause(int PauseTime){
		TimerPauseStart = true;
		final MediaPlayer mediaPlayerRun = MediaPlayer.create(this, R.raw.run);
		pause = new Timer();
		pause.schedule(new TimerTask() {

		    @Override
		    public void run() {
		    	new Thread(new Runnable() {

		    	    @Override
		    	    public void run() {
		    	        
		    	        mediaPlayerRun.start();
		    	        
		    	        TimerPauseStart = false;
		    	    }
		    	}).start();
		    } //wait 'PauseTime*1000' before it does something (milliseconds)
		},PauseTime*1000);
	}

	public void DelayStop(int Time, final int x){
		TimerStopStart = true;
		final MediaPlayer mediaPlayerStop = MediaPlayer.create(this, R.raw.stop);
		stop = new Timer();
		stop.schedule(new TimerTask() {

		    @Override
		    public void run() {
		    	new Thread(new Runnable() {

		    	    @Override
		    	    public void run() {
		    	    	
		    	    	if(x == 1)
		    	    		run.cancel();
		    	    	else if(x == 2){
		    	    		mediaPlayerStop.start();
		    	    		TimerStopStart = false;
		    	        }
		    	    }
		    	}).start();
		    } //wait 'Time*1000' before it does one of the things. (milliseconds)
		},Time*1000);
	}
	
	public boolean verifyLocation(double distance) {
		if(isMoving()) {
			
		}
		else {
			
		}
			return true;
	}
	
	public boolean isMoving() {
		if(amplitude > 0/*TOTAL_RUNNING_ACCELERATION*/) {
			return true;
		}
		else
			return false;
	}
	

	public void writeLocation(double lat, double lng) {
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		if(nextWorkoutID == -1)
			nextWorkoutID = (int) DatabaseUtils.queryNumEntries(db, TrappEntry.TABLE_NAME) + 1;
			 
		ContentValues values = new ContentValues();
		values.put(TrappEntry.COLUMN_NAME_WORKOUT, nextWorkoutID);
		values.put(TrappEntry.COLUMN_NAME_LATITUDE, lat);
		values.put(TrappEntry.COLUMN_NAME_LONGITUDE, lng);
		db.insert(TrappEntry.TABLE_NAME_LOCATIONS, null, values);
		db.close();
	}

	public int Calorie_math(float time, int weight) {
		int caloriemath = 0;
		if(workoutType.equals("Walk"))
			caloriemath = 9;
		else if(workoutType.equals("Running"))
			caloriemath = 9;
		else if(workoutType.equals("Interval"))
			caloriemath = 9;
		else if(workoutType.equals("Test"))
			caloriemath = 9;
		
		int calories = (int) ((weight * caloriemath) * time);
		
		return calories;
	}
	public void sounds(int sound) {
		/*switch(sound)
		{
		case 5 : mediaPlayer = MediaPlayer.create(this, R.raw.5);
		case 10 : mediaPlayer = MediaPlayer.create(this, R.raw.10);
		case 500 : mediaPlayer = MediaPlayer.create(this, R.raw.500);
		case 1000 : mediaPlayer = MediaPlayer.create(this, R.raw.1000);
		case 1500 : mediaPlayer = MediaPlayer.create(this, R.raw.1500);
		case 2000 : mediaPlayer = MediaPlayer.create(this, R.raw.2000);
		case 2500 : mediaPlayer = MediaPlayer.create(this, R.raw.2500);
		case 3000 : mediaPlayer = MediaPlayer.create(this, R.raw.3000);
		case 4000 : mediaPlayer = MediaPlayer.create(this, R.raw.4000);
		case 5000 : mediaPlayer = MediaPlayer.create(this, R.raw.5000);
		case 6000 : mediaPlayer = MediaPlayer.create(this, R.raw.6000);
		case 7000 : mediaPlayer = MediaPlayer.create(this, R.raw.7000);
		case 8000 : mediaPlayer = MediaPlayer.create(this, R.raw.8000);
		case 9000 : mediaPlayer = MediaPlayer.create(this, R.raw.9000);
		
		}*/
		
	}
}
