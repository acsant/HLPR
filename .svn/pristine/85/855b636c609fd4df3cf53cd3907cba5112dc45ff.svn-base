package com.android.helper;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class InformationDisplay extends FragmentActivity {
	
	private GoogleMap mainMap;
	private LatLng currentLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_helper_intro);
		Intent currLocIntent = getIntent();
		currentLatLng = (LatLng) currLocIntent.getExtras().get(HomeActivity.EXTRA_CURRENT_LOCATION);
		setupMap();
		if (currentLatLng != null) {
			mainMap.addMarker(new MarkerOptions().position(currentLatLng).title("Your Location"));
			setCameraView(currentLatLng);
		}
		setupActionBar();
	}
/**
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.helper_intro, menu);
		return true;
	}
	*/
	/** Setup the actionbar if the API is available */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	/** Action bar items selected */
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			//This ID represents the Home or the Up button on the action bar
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setupMap () {
		if (mainMap == null) {
			mainMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.main_map)).getMap();
		}
	}
	
	private void setCameraView(LatLng loc) {
		CameraPosition camPosition = new CameraPosition.Builder().target(loc).zoom(13).build();
		mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}

}
