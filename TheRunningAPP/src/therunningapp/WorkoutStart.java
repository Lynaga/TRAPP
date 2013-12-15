package therunningapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import project.therunningapp.R;
import project.therunningapp.R.string;
import therunningapp.TrappContract.TrappEntry;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

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
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener,
		SensorEventListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Constants to set the update intervals
	private final static int MILLISECONDS_PER_SECOND = 1000;
	private final static int UPDATE_INTERVAL_IN_SECONDS = 1;
	private final static int FASTEST_INTERVAL_IN_SECONDS = 1;

	private final static long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private final static long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	
	private final static double MAX_DISTANCE = 7;	//Max average distance pr. second when 
													//accellerating from 0 (applies for the first 3 seconds)
	//Location variables to store user locations

	private Location prevLocation = null;	// Location variables to store user locations

	LocationClient myLocationClient; // Object to connect to Google location
										// services and request location update
										// callbacks
	GoogleMap myMap; // Object to get map from fragment
	LocationRequest myLocationRequest; // Object to set parameters for the
										// requests to the LocationClient
	// Objects to access the accelerometer
	SensorManager mySensorManager;
	Sensor mySensor;

	// Variables used to pause / restart workout and store to database.
	int timeInterval = 1; // Time interval since last verified locations
							// (Standard = 1)
	long pauseTime = 0;
	boolean workoutStatus = false;
	double myDistance = 0;
	double averageDistance = 0;
	int tempCounter = 0;
	Chronometer myTimer;

	// getting extras
	int min = 0;
	int sec = 0;
	int lengde = 0;
	int test = 0;
	String testType = "0";
	
	//Variables for accelerometer data
	double averageAmp = 5;
	boolean accUpdateList = true;
	boolean accFillList = false;
	int accFillListCounter = 1;
	
	//Objects to play sounds

	double amplitude; // Used for accelerometer data

	MediaPlayer mediaPlayer;
	AudioManager am;

	// intervals
	Timer run;
	Timer pause;
	Timer stop;
	boolean TimerRunStart = false;
	boolean TimerPauseStart = false;
	boolean TimerStopStart = false;
	String intervalType, workoutType, workoutname;
	Bundle extras;

	public static class myLatLng implements Serializable { // Class to store lat / lng values of locations
		double lat;
		double lng;
	}

	List<myLatLng> locationList = new ArrayList<myLatLng>(); // List to store gps data

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_start);
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		//Getting extras
		extras = getIntent().getExtras();
		workoutType = extras.getString("workoutType");
		workoutname = extras.getString("workoutname");
		int suggestedId = extras.getInt("suggestedId", -1);

		mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		
		myLocationClient = new LocationClient(this, this, this);	//Initiate LocationClient
		myTimer = (Chronometer) findViewById(R.id.T_timer);			//Set chronometer to view

		// Get map from the fragment
		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment;
		mySupportMapFragment = (SupportMapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		myMap = mySupportMapFragment.getMap();
		myMap.setMyLocationEnabled(true); // Enable the my locaton button

		// Set parameters for the location updates
		myLocationRequest = LocationRequest.create();
		myLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		myLocationRequest.setInterval(UPDATE_INTERVAL);
		myLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		
		if(suggestedId != -1)	//If a route was suggested -> display the suggested route on map
			drawSuggestedRoute(suggestedId);
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        if(!myLocationClient.isConnected())
        	myLocationClient.connect();
    }
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Disconnecting the client if connected
		if (myLocationClient.isConnected())
        	myLocationClient.removeLocationUpdates(this);
        myLocationClient.disconnect();
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

		// Set map to user location and set initial text
		setCamera(myLocationClient.getLastLocation());
		CameraUpdate zoom = CameraUpdateFactory.zoomTo(19);
		myMap.animateCamera(zoom); // Set zoom
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
		/*
		 * The following code was retrieved from developer.android.com, to
		 * resolve connection errors. Google Play services can resolve some
		 * errors it detects. If the error has a resolution, try sending an
		 * Intent to start a Google Play services activity that can resolve
		 * error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
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
			 * If no solution is available, display a dialog to the user.
			 */
			String T_Errortext = "Google Play services could not resolve the connection problem.";
			TextView T_textView = (TextView) findViewById(R.id.T_distance);
			T_textView.setText(T_Errortext);
		}
	}

	// Function to get location updates
	public void onLocationChanged(Location newLocation) {
		myLatLng listLatLng = new myLatLng();
		double newDistance;
		
		if (prevLocation == null)	//Check if last location is set
			prevLocation = myLocationClient.getLastLocation();	//If not set -> Update last location
		
		if(accUpdateList == true) {		//If it is time to update accelerometer
			accUpdateList = false;		//Update variables and register listener
			new Thread(new Runnable() {	//Starts a thread to sleep for x seconds (delay between updating accelerometer values)
		        public void run() {
		        	SystemClock.sleep(MILLISECONDS_PER_SECOND * 10);	//Sleep for x seconds
		        	accUpdateList = true;								//Set bool to update list true after sleep is over
		        }
		    }).start();
			
			accFillList = true;		//Set bool to fill list and register accelerometer
			mySensorManager.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		if(averageAmp > 1) {	//If user is moving
			newDistance = prevLocation.distanceTo(newLocation);	//Getting distance between last 2 locations
			double tempLat = prevLocation.getLatitude();
			double tempLng = prevLocation.getLongitude();
			
			if(!checkDistance(newDistance)) {	//If new location is invalid -> update new location to a valid location
				double x = newLocation.getLatitude() - tempLat;		//Change in latitude
				double y = newLocation.getLongitude() - tempLng;	//Change in longitude
				double k;				//Scaling variable
				
				if(locationList.size() < 3)
					k = MAX_DISTANCE / newDistance;				//Sets scaling factor according for the first 3 seconds
				else
					k = averageDistance*4 / newDistance;		//Sets scaling factor according to the users average distance
																//during the last 3 locations
				newLocation.setLatitude(tempLat + x*k);	//Scales latitude
				newLocation.setLongitude(tempLng + y*k);	//Scales longitude
				newDistance = prevLocation.distanceTo(newLocation);	//Update distance between two lastest locations
			}
			//Updates variables to draw path on map
			listLatLng.lat = newLocation.getLatitude();
			listLatLng.lng = newLocation.getLongitude();
			
			//Drawing on the map from last location to new location
			myMap.addPolyline(new PolylineOptions()
			.add(new LatLng(tempLat, tempLng), new LatLng(listLatLng.lat, listLatLng.lng))
			.width(5)
			.color(Color.RED).geodesic(true));
	
			myDistance += newDistance;	//Updating total distance
			setCamera(newLocation);		//Update map to new location
			setText();					//Update text
			
			locationList.add(listLatLng);			//Add new location to list
			prevLocation = newLocation;				//Update last location for next update
		}
	}
	
	public void onSensorChanged(SensorEvent event) {	//Handling readings from the accelerometer
		if(accFillList == true && accFillListCounter < 31) {	//If app needs to update average amplitude and still
			double amplitude;									//need more readings
			amplitude = Math.sqrt((event.values[0] * event.values[0])	//Calculate amplitude
								+ (event.values[1] * event.values[1])
								+ (event.values[2] * event.values[2]));
			averageAmp += amplitude;		//Add amplitudes together to calculate average amplitude
			accFillListCounter++;
		}
		else {								//If app got enough readings
			accFillList = false;
			mySensorManager.unregisterListener(this, mySensor);	//Unregister accelerometer
			averageAmp /= accFillListCounter;					//Calculate average amplitude
			accFillListCounter = 1;								//Reset counter
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
	}

	public void test_check(int test) {
		int rounds = 1;
		// Getting data from the intent that comes from TestSetup.java
		int min = extras.getInt("min");
		int sec = extras.getInt("sec");
		int lengde = extras.getInt("lengder");
		String testType = extras.getString("testType");

		// variables for checking the test condition and ending the test at
		// apropiate time

		int value;
		int set = 0;
		//sounds(1);
		// The test checking happends as long as the distance/time is lower than
		// the test value.
		
		do {
			if (testType.equals("Distance")) {
				value = (int) myDistance;
				set = lengde;
			} else {
				value = (int) (SystemClock.elapsedRealtime() - myTimer
						.getBase());
				set = (min * 60000) + (sec * 1000);
			}

			if (test == 1 || test == 2) {
				int sound = (rounds * 500);
				if (value >= sound) {
					sounds(sound);
					rounds++;
				}
			}
			if (test == 3 || test == 4) {
				int sound = (rounds * 1000);
				if (value >= sound) {
					sounds(sound);
					rounds++;
				}
			}
			if (test == 5) {
				int sound = (rounds * 5);
				int value1 = (value / 1000) / 60;
				if (value1 >= sound) {
					sounds(sound);
					rounds++;
				}
			}
			// sleeps the thread for a second as the gps updates dosn't happend
			// more often. and u can't select a time lower than 1 second
			SystemClock.sleep(1000);
			
		}
		// Ends the test when u have reached the value(time or distance)
		while (value <= set);
		sounds(4);
		end();
	}

	// Function to center map on user
	public void setCamera(Location camLocation) {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(
				camLocation.getLatitude(), camLocation.getLongitude()));
		myMap.moveCamera(center);
	}

	// Function to set and update current workout info
	public void setText() {
		int tempDistance = (int) myDistance;
		StringBuilder sb = new StringBuilder();

		if (tempDistance > 1000) { // If user ran more than 1 km, format output
									// to (ex.) "1.23 km"
			int tempKm = tempDistance / 1000;
			int tempM = (tempDistance % 1000) / 10;
			sb.append(tempKm + "." + tempM + " km");
		} else {
			sb.append(tempDistance + " m");
		}

		// Set new values
		TextView textView = (TextView) findViewById(R.id.T_distance);
		textView.setText(sb.toString());

		TextView tempView = (TextView) findViewById(R.id.T_speed);
		tempView.setText("Coming soon");

		TextView tempView2 = (TextView) findViewById(R.id.T_calories);
		tempView2.setText("Coming soon");
	}

	// Onclick function for the start / pause workout button
	public void workoutStartPause(View view) {
		String tempString; // String to change the text on button
		Button tempButton; // Object to change the text on button

		if (workoutStatus == false) { // If workout is not started, or paused
			myTimer.setBase(SystemClock.elapsedRealtime() + pauseTime); // Sets timer to right start value
			myTimer.start();
			myLocationClient.requestLocationUpdates(myLocationRequest, this); // Starts location updates
			sounds(1);			//start sound
			
			if (workoutType.equals(getString(string.walking))) {	//For further development
			} else if (workoutType.equals(getString(string.running))) {	//For further development
			} else if (workoutType.equals("Interval")) {
				new Thread(new Runnable() {
					public void run() {
						Interval();
					}
				}).start();
			} else if (workoutType.equals("Test")) {
				int chose = extras.getInt("chose");

				switch (chose) {
				case 1:
					new Thread(new Runnable() {
						public void run() {
							test_check(1);
						}
					}).start();
				case 2:
					new Thread(new Runnable() {
						public void run() {
							test_check(2);
						}
					}).start();
				case 3:
					new Thread(new Runnable() {
						public void run() {
							test_check(3);
						}
					}).start();
				case 4:
					new Thread(new Runnable() {
						public void run() {
							test_check(4);
						}
					}).start();
				case 5:
					new Thread(new Runnable() {
						public void run() {
							test_check(5);
						}
					}).start();
				case 6:
					new Thread(new Runnable() {
						public void run() {
							test_check(0);
						}
					}).start();
				}
			}

			workoutStatus = true; // Change workout status
			tempString = getString(R.string.T_pause_workout_button_string); // Get text for button
		}

		else {
			myTimer.stop();
			pauseTime = myTimer.getBase() - SystemClock.elapsedRealtime(); // Stores value of the timer

			if (myLocationClient.isConnected()) // If client is connected
				myLocationClient.removeLocationUpdates(this); // remove location
																// updates
			workoutStatus = false; // Change workout status
			tempString = getString(R.string.T_start_workout_button_string); // Get text for button
		}

		// Set new text for button
		tempButton = (Button) findViewById(R.id.T_pause_workout_button);
		tempButton.setText(tempString);
	}

	public void workoutEnd(View view) {
		end();
	}

	public void end() {
		// stop the loops if it's an Interval
		if (TimerRunStart) {
			run.cancel();
			TimerRunStart = false;
		}
		if (TimerPauseStart) {
			pause.cancel();
			TimerPauseStart = false;
		}
		if (TimerStopStart) {
			stop.cancel();
			TimerStopStart = false;
		}

		// Get the database
		long nextDbId;
		TrappDBHelper mDBHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		myTimer.stop();

		String[] projection = { TrappEntry._ID, TrappEntry.COLUMN_NAME_WEIGHT };

		Cursor w = db.query(TrappEntry.TABLE_NAMEPREF, projection, null, null,
				null, null, null);

		// Initiate variables needed to write to the database
		int calories = 0;
		Float time;
		pauseTime = SystemClock.elapsedRealtime() - myTimer.getBase();
		time = (float) pauseTime / 3600000;

		Date cDate = new Date();
		String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate); // Set the dateformat

		if (w.moveToFirst()) { // Checks if the user has set the weight
			int weight = w.getInt(w
					.getColumnIndex(TrappEntry.COLUMN_NAME_WEIGHT));
			// If weight is set calculate calories burnt during the workout
			calories = Calorie_math(time, weight);
		}

		if (time != 0 && myDistance != 0) { // Check if users started workout
			// If workout was started -> write to database and start new
			// activity, WorkoutEnd
			ContentValues values = new ContentValues();
			byte[] buff = null;

			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bos);
				out.writeObject(locationList);
				out.close();
				buff = bos.toByteArray();
			} catch (IOException ioe) {
				Log.e("serializeObject", "error", ioe);
			}
			
			if(workoutType.equals("Walking") || workoutType.equals("Running") || workoutType.equals("Test"))
				values.put(TrappEntry.COLUMN_NAME_WORKOUTTYPE, workoutType);
			else if(workoutType.equals("Interval"))
				values.put(TrappEntry.COLUMN_NAME_WORKOUTTYPE, workoutname);
			
			values.put(TrappEntry.COLUMN_NAME_DATE, fDate);
			values.put(TrappEntry.COLUMN_NAME_DISTANCE, (int) myDistance);
			values.put(TrappEntry.COLUMN_NAME_TIME, pauseTime);
			values.put(TrappEntry.COLUMN_NAME_CALORIES, calories);
			values.put(TrappEntry.COLUMN_NAME_LOCATIONS, buff);
			nextDbId = db.insert(TrappEntry.TABLE_NAME, null, values);

			Intent intent = new Intent(this, WorkoutDisplay.class);
			intent.putExtra("id", Long.toString(nextDbId));
			startActivity(intent);
		}
		db.close();
		finish();
	}
	
	public boolean checkDistance(double tempDistance) {		//Checks if distance between the two latest locations are valid
		int i = locationList.size();
		if(i < 3) {								//If not enough locations to determine average distance
			if(tempDistance <= MAX_DISTANCE)	//If the distance between the two latest locations is valid
				return true;
			else
				return false;
		}
		else	//If the application got enough locations to determine average distance
		{
			//Declaring variables to calculate average distance
			Location tempLoc1 = new Location("Temp location 1");
			Location tempLoc2 = new Location("Temp location 2");
			Location tempLoc3 = new Location("Temp location 3");
			
			//Initiating variables to calculate average distance
			tempLoc1.setLatitude(locationList.get(i-1).lat);
			tempLoc1.setLongitude(locationList.get(i-1).lng);
			tempLoc2.setLatitude(locationList.get(i-2).lat);
			tempLoc2.setLongitude(locationList.get(i-2).lng);
			tempLoc3.setLatitude(locationList.get(i-3).lat);
			tempLoc3.setLongitude(locationList.get(i-3).lng);
		
			//Calculating average distance based on the last three locations
			averageDistance = (tempLoc3.distanceTo(tempLoc2) + tempLoc2.distanceTo(tempLoc1)) / 2;
			
			if(tempDistance <= averageDistance*4)	//If distance between the two latest locations is within the probable threshold
				return true;
			else
				return false;
		}
	}
	
	public void Interval() {
		Bundle extras = getIntent().getExtras();
		final int run = extras.getInt("run");
		final int pause = extras.getInt("pause");
		final int rep = extras.getInt("rep");
		String intervalType = extras.getString("intervalType");

		if (intervalType.equals("time"))
			Interval_time(run, pause, rep);
		else if (intervalType.equals("distance")) {
			new Thread(new Runnable() {
				public void run() {
					Interval_distance(run, pause, rep);
				}
			}).start();
		} else
			end();
	}

	//Function for interval distance
	public void Interval_distance(int RunDistance, int PauseDistance,
			int Repetition) {
		
		Interval_distance(RunDistance);
		
		for (int rep = 1; rep < Repetition; rep++) {
			sounds(3); // sound for pause
			Interval_distance((RunDistance + PauseDistance) * rep);
			sounds(2); // sound for run
			Interval_distance(((RunDistance + PauseDistance) * rep)
					+ RunDistance);
		}

		sounds(4); // sound for stop
		end();
	}

	//when the value is the same as Distance, the loop is over.
	public void Interval_distance(int Distance) {
		int value;
		int set = Distance;

		do {
			value = (int) myDistance;
			SystemClock.sleep(1000); // since the gps doesn't update more often
		} while (value <= set);
	}
	
	//starts three threads with different loops
	public void Interval_time(int RunTime, int PauseTime, int Repetition) {
		DelayRun(RunTime, PauseTime);			//infinite loop
		DelayStop((RunTime + PauseTime) * (Repetition - 1) + 1, 1);	//stops DelayRun loop
		DelayStop((RunTime * Repetition) + (PauseTime * (Repetition - 1)), 2);	//stops the workout
	}

	public void DelayRun(final int RunTime, final int PauseTime) {
		TimerRunStart = true;
		run = new Timer();
		run.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
							sounds(3); // sound for pause
							DelayPause(PauseTime);
							TimerRunStart = false;
							
					}
				}).start();
			} // wait 'RunTime*1000' before it start, and loop every
				// '(PauseTime+RunTime)*1000' (milliseconds)
		}, RunTime * 1000, (RunTime + PauseTime) * 1000);
	}

	public void DelayPause(int PauseTime) {
		TimerPauseStart = true;
		pause = new Timer();
		pause.schedule(new TimerTask() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {
							sounds(2); // sound for run
							TimerPauseStart = false;
							
					}
				}).start();
			} // wait 'PauseTime*1000' before it does something (milliseconds)
		}, PauseTime * 1000);
	}

	public void DelayStop(int Time, final int x) {
		TimerStopStart = true;
		stop = new Timer();
		stop.schedule(new TimerTask() {

			@Override
			public void run() {
				new Thread(new Runnable() {

					@Override
					public void run() {

						if (x == 1)
							run.cancel();
						else if (x == 2 && TimerStopStart == true) {
							sounds(4); // sound for stop
							TimerStopStart = false;
							end();
						}
					}
				}).start();
			} // wait 'Time*1000' before it does one of the things.
				// (milliseconds)
		}, Time * 1000);
	}

	public int Calorie_math(float time, int weight) {	//For further development
		int caloriemath = 0;
		if (workoutType.equals("Walk"))
			caloriemath = 9;
		else if (workoutType.equals("Running"))
			caloriemath = 9;
		else if (workoutType.equals("Interval"))
			caloriemath = 9;
		else if (workoutType.equals("Test"))
			caloriemath = 9;

		int calories = (int) ((weight * caloriemath) * time);
		return calories;
	}

	// Function to make the music duck while playing notification sounds..
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
				
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
			
			}
		}
	};
	
	public void sounds(int sound) {
		MediaPlayer MP = null;
		String soundpackage = MainActivity.soundpackage;
		// Request audio focus for playback
	/*	int result = am.requestAudioFocus(afChangeListener,
                	// Use the music stream.
					AudioManager.STREAM_MUSIC,
					// Request permanent focus.
					AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
		
		*/
		if (soundpackage.equals("en")) {
			switch (sound) {
			case 1: { MP = MediaPlayer.create(this, R.raw.english_start); break;}
			case 2: { MP = MediaPlayer.create(this, R.raw.english_run); break;}
			case 3: { MP = MediaPlayer.create(this, R.raw.english_pause); break;}
			case 4: { MP = MediaPlayer.create(this, R.raw.english_stop); break; }
			case 5: { MP = MediaPlayer.create(this, R.raw.norwegian_stop); break; }
			case 10: { MP = MediaPlayer.create(this, R.raw.norwegian_stop); break; }
			case 500: { MP = MediaPlayer.create(this, R.raw.english_500); break; }
			case 1000: { MP = MediaPlayer.create(this, R.raw.english_1000); break; }
			case 1500: { MP = MediaPlayer.create(this, R.raw.english_1500); break; }
			case 2000: { MP = MediaPlayer.create(this, R.raw.english_2000); break; }
			case 2500: { MP = MediaPlayer.create(this, R.raw.english_2500); break; }
			case 3000: { MP = MediaPlayer.create(this, R.raw.english_3000); break; }
			case 4000: { MP = MediaPlayer.create(this, R.raw.english_4000); break; }
			case 5000: { MP = MediaPlayer.create(this, R.raw.english_5000); break; }
			case 6000: { MP = MediaPlayer.create(this, R.raw.english_6000); break; }
			case 7000: { MP = MediaPlayer.create(this, R.raw.english_7000); break; }
			case 8000: { MP = MediaPlayer.create(this, R.raw.english_8000); break; }
			case 9000: { MP = MediaPlayer.create(this, R.raw.english_9000); break; }
			} 
		}
		else if (soundpackage.equals("no")) {
			switch (sound) {
			case 1: { MP = MediaPlayer.create(this, R.raw.norwegian_start); break; }
			case 2: { MP = MediaPlayer.create(this, R.raw.norwegian_run); break; }
			case 3: { MP = MediaPlayer.create(this, R.raw.norwegian_pause); break; }
			case 4: { MP = MediaPlayer.create(this, R.raw.norwegian_stop); break; }
			case 5: { MP = MediaPlayer.create(this, R.raw.norwegian_stop); break; }
			case 10: { MP = MediaPlayer.create(this, R.raw.norwegian_stop); break; }
			case 500: { MP = MediaPlayer.create(this, R.raw.norwegian_500); break; }
			case 1000: { MP = MediaPlayer.create(this, R.raw.norwegian_1000); break; }
			case 1500: { MP = MediaPlayer.create(this, R.raw.norwegian_1500); break; }
			case 2000: { MP = MediaPlayer.create(this, R.raw.norwegian_2000); break; }
			case 2500: { MP = MediaPlayer.create(this, R.raw.norwegian_2500); break; }
			case 3000: { MP = MediaPlayer.create(this, R.raw.norwegian_3000); break; }
			case 4000: { MP = MediaPlayer.create(this, R.raw.norwegian_4000); break; }
			case 5000: { MP = MediaPlayer.create(this, R.raw.norwegian_5000); break; }
			case 6000: { MP = MediaPlayer.create(this, R.raw.norwegian_6000); break; }
			case 7000: { MP = MediaPlayer.create(this, R.raw.norwegian_7000); break; }
			case 8000: { MP = MediaPlayer.create(this, R.raw.norwegian_8000); break; }
			case 9000: { MP = MediaPlayer.create(this, R.raw.norwegian_9000); break; }
			} 
		}
	
	// int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	// Skru ned lyd på all lyd
	// Skru opp lyd til volume MP stream id. 	(id: MP.getAudioSessionId()) 
	// Skru opp all lyd til volume
	
	//	if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			try{
				MP.start();
				}catch(Exception e)
				{Log.e("MusicBug", e.getMessage(), e);}
			
			MP.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
			    public void onCompletion(MediaPlayer player) {
			    	player.stop();
			    	player.release();
			    }
			});
	//	}
	}
	
	public void drawSuggestedRoute(int dbId) {
		//Get the db
		TrappDBHelper mDbHelper = new TrappDBHelper(this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		//Query the db
		String[] projection = {TrappEntry.COLUMN_NAME_DISTANCE, TrappEntry.COLUMN_NAME_LOCATIONS };
		final Cursor c = db.query(TrappEntry.TABLE_NAME, projection, "_ID=?", new String[] { Integer.toString(dbId) }, null,null,null,null);
		
		if(c.moveToFirst()){	//If any results from db
			List<myLatLng> tempList = new ArrayList<myLatLng>();
			byte[] locations = c.getBlob(c.getColumnIndex(TrappEntry.COLUMN_NAME_LOCATIONS));
			
			try { 	//Deserialize object
			      ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(locations)); 
			      tempList = (List<myLatLng>) in.readObject();
			      in.close(); 

			    } catch(ClassNotFoundException cnfe) { }
				  catch(IOException ioe) { }
			
			int numberOfElements = tempList.size();		//Get numbers in list
			
			if(numberOfElements > 0) {		//If database contained locations for this workout
				LatLng prevLatLng = null, newLatLng = null;
				for(int i = 0; i < numberOfElements; i++) {			//Loop through all locations
					
					if(prevLatLng == null)	//Setting first location
						prevLatLng = new LatLng(tempList.get(i).lat,
												tempList.get(i).lng);
					
					else {						//Updating new location
						newLatLng = new LatLng(tempList.get(i).lat,
											   tempList.get(i).lng);
						
						//Draw polyline
						myMap.addPolyline(new PolylineOptions()	
					     .add(prevLatLng, newLatLng)
					     .width(5)
					     .color(Color.BLUE).geodesic(true));
						
						prevLatLng = newLatLng;			//Updating for next loop
						}	
				}
			}
		}
		db.close();	//Close the db
	}
}
