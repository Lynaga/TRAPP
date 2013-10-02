package com.example.therunningapp;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class WorkoutStart extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

	private final static int
    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	LocationClient myLocationClient;
	GoogleMap myMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_start);
		
		myLocationClient = new LocationClient(this, this, this);
		
		FragmentManager myFragmentManager = getSupportFragmentManager();
		SupportMapFragment mySupportMapFragment;
		mySupportMapFragment = (SupportMapFragment) myFragmentManager.findFragmentById(R.id.map);
		myMap = mySupportMapFragment.getMap();
		myMap.setMyLocationEnabled(true);
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
	}
	
	@Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        myLocationClient.disconnect();
        super.onStop();
    }
	
	@Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        
        Location myCurrentLocation;
		myCurrentLocation = myLocationClient.getLastLocation();
		
		CameraUpdate center =
		        CameraUpdateFactory.newLatLng(new LatLng(myCurrentLocation.getLatitude(),
		                                                 myCurrentLocation.getLongitude()));
		    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

		    myMap.moveCamera(center);
		    myMap.animateCamera(zoom);
		    
		    TextView textView = (TextView) findViewById(R.id.T_testView);
		    textView.setText("Current location: " + myCurrentLocation.getLatitude() +
		    				 " / " + myCurrentLocation.getLongitude());
	}
	
	@Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
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
			TextView T_textView = (TextView) findViewById(R.id.T_testView);
			T_textView.setText(T_Errortext);
        }
    }
	
	public void onLocationChanged(Location location) {
		
	}
	
}
