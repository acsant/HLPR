package com.android.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

public class HomeActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
													  GooglePlayServicesClient.OnConnectionFailedListener {
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_CURRENT_LOCATION = "com.android.helper.CURRENT_LOCATION";
	
	LocationClient mLocationClient;
	// Global variable to hold the current location
	Location mCurrentLocation;
	LatLng curLocation;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
		mLocationClient = new LocationClient (this, this, this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
	}
	
	@Override
	protected void onStart () {
		super.onStart();
		// Connect the client
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop () {
		// Disconnecting the client invalidates it
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/** Display volunteer opportunities */
	public void displayVolOpportunities (View view) {
		Intent volunteerIntent = new Intent(this, InformationDisplay.class);
		volunteerIntent.putExtra(EXTRA_CURRENT_LOCATION, curLocation);
		startActivity(volunteerIntent);
	}
	
	/** Display charities */
	public void charityDisplay (View view) {
		Intent charityIntent = new Intent(this, InformationDisplay.class);
		charityIntent.putExtra(EXTRA_CURRENT_LOCATION, curLocation);
		startActivity(charityIntent);
	}
	
	/** Display workout locations */
	public void workoutLocDisplay (View view) {
		Intent workoutDisplayIntent = new Intent(this, InformationDisplay.class);
		workoutDisplayIntent.putExtra(EXTRA_CURRENT_LOCATION, curLocation);
		startActivity(workoutDisplayIntent);
	}
	
	// Define a DialogFragment that displays the error dialog
	public static class ErrorDialogFragment extends DialogFragment {
		// Global field to contain the error dialog
		private Dialog mDialog;
		
		// Default constructor sets the dialog field to null
		public ErrorDialogFragment () {
			super();
			mDialog = null;
		}
		
		// Set the dialog to display
		public void setDialog (Dialog dialog) {
			mDialog = dialog;
		}
		
		// Return a dialog to the dialog fragment
		@Override
		public Dialog onCreateDialog (Bundle savedInstanceState) {
			return mDialog;
		}
	}
	
	/*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST :
			 /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */	
			switch (resultCode) {
			case Activity.RESULT_OK :
				// Try the request again
				break;
			}
		}  
	}
	
	private boolean serviceConnected () {
		// Check that google play service is available
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		// If google play service is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play Service is available.");
			// Continue
			return true;
			
			//Google Play service was not available for some reason unknown to me or the mankind
		} else {
			// Get the errior dialog from google play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			
			// If google play services can provice an error dialog
			if (errorDialog != null) {
				// Create a new dialog fragment for the error Dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				// Set the dialog in the dialog fragment
				errorFragment.setDialog(errorDialog);
				// Show the error dialog in the dialog fragment
				errorFragment.show(getSupportFragmentManager(), "Location Updates");
			}
			return false;
		}
	}
	
	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected (Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		mCurrentLocation = mLocationClient.getLastLocation();
		curLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
	}
	
	/*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
	@Override
	public void onDisconnected () {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}
	
	/*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed (ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
		if (connectionResult.hasResolution()) {
			try {
				// Start an activity that tried to resolve the error
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
			} catch (IntentSender.SendIntentException e) {
				/*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
				e.printStackTrace();
			}
		} else {
			/*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
			showErrorDialog(connectionResult.getErrorCode());
		}
	}
	
	public void showErrorDialog(int errorCode) {
		GooglePlayServicesUtil.getErrorDialog(errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
	}

}
