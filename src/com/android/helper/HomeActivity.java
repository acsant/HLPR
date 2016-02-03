package com.android.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class HomeActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
													  GooglePlayServicesClient.OnConnectionFailedListener, OnItemSelectedListener, 
													  com.google.android.gms.location.LocationListener {
	
	// Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_CURRENT_LOCATION = "com.android.helper.CURRENT_LOCATION";
	public static final String EXTRA_VOLUNTEERING_CATEGORY = "com.android.helper.VOLUNTEER_CATEGORY";
	
	private LocationClient mLocationClient;
	// Global variable to hold the current location
	LocationRequest mLocationRequest;
	SharedPreferences mPrefs;
	SharedPreferences.Editor mEditor;
	boolean mUpdatesRequested;
	private Location mCurrentLocation;
	private LatLng curLocation;
	private String category = "";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
		super.onCreate(savedInstanceState);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		mPrefs = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
		mEditor = mPrefs.edit();
		mLocationClient = new LocationClient (this, this, this);
		mUpdatesRequested = false;
		setContentView(R.layout.activity_home);
		Spinner categorySpinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categorySpinner.setAdapter(categoryAdapter);
		categorySpinner.setOnItemSelectedListener(this);
	}
	
	@Override
	protected void onPause() {
		mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
		mEditor.commit();
		super.onPause();
	}
	
	public LocationClient getmLocationClient() {
		return mLocationClient;
	}

	public void setmLocationClient(LocationClient mLocationClient) {
		this.mLocationClient = mLocationClient;
	}

	public Location getmCurrentLocation() {
		return mCurrentLocation;
	}

	public void setmCurrentLocation(Location mCurrentLocation) {
		this.mCurrentLocation = mCurrentLocation;
	}

	public LatLng getCurLocation() {
		return curLocation;
	}

	public void setCurLocation(LatLng curLocation) {
		this.curLocation = curLocation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	protected void onResume () {
		/*
         * Get any previous setting for location updates
         * Gets "false" if an error occurs
         */
		if (mPrefs.contains("KEY_UPDATES_ON")) {
			mUpdatesRequested = mPrefs.getBoolean("KEY_UPDATES_ON", false);
		} else {
			mEditor.putBoolean("KEY_UPDATES_ON", false);
			mEditor.commit();
		}
		super.onResume();
	}

	@Override
	protected void onStart () {
		super.onStart();
		// Connect the client
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop () {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
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
		volunteerIntent.putExtra(EXTRA_VOLUNTEERING_CATEGORY, category);
		startActivity(volunteerIntent);
	}
	
	public void changeUserLocation (View view){
		Intent userLocationIntent = new Intent(this, UserLocationPreferences.class);
		startActivity(userLocationIntent);
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
	
	private boolean servicesConnected () {
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
		//LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		if (mCurrentLocation == null) {
			mCurrentLocation = mLocationClient.getLastLocation();
		}
		if (mCurrentLocation == null) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
		if (mCurrentLocation == null) {
			LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			LocationListener locationListener = new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String provider) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					mCurrentLocation = location;
					
				}
			};
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		}
		if (mCurrentLocation != null) {
			curLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
		} else {
			new AlertDialog.Builder(HomeActivity.this)
			.setTitle("Location Services Offline.")
			.setMessage("Could not connect to Location Services. "
					+ "Please turn on GPS and set Location Services mode to 'High Accuracy' or 'Power Saving' "
					+ "under settings and try again later once the GPS symbol for your device stops blinking.")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}).show();
			
		}
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

	@Override
	public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
			long arg3) {
		if (parent.getItemAtPosition(pos).toString().equals("All")) {
			this.category = "";
		} else {
			this.category = parent.getItemAtPosition(pos).toString();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
		
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			mCurrentLocation = location;
		}
		
	}

}
