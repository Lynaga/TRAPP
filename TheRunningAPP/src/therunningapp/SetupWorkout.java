package therunningapp;

import project.therunningapp.R;
import project.therunningapp.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;

public class SetupWorkout extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_workout);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void walk (View view){
		Intent intent = new Intent(this, WorkoutStart.class);
		String walk = "Walking";
		intent.putExtra("workoutType", walk);
		startActivity(intent);
		finish();
	}
	
	public void running (View view) { 
		Intent intent = new Intent(this, WorkoutStart.class);
		String running = "Running";
		intent.putExtra("workoutType", running);
		startActivity(intent);
		finish();
	}
	
	public void intervalSetup (View view){
		Intent intent = new Intent(this, Interval.class);
		startActivity(intent);
		finish();
	}
	
	public void testStart (View view){
		Intent intent = new Intent(this, TestSetup.class);
		startActivity(intent);
		finish();
	}
}
