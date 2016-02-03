package com.android.helper;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.helper.HomeActivity.ErrorDialogFragment;
import com.google.android.gms.maps.model.LatLng;

public class UserLocationPreferences extends FragmentActivity implements OnItemSelectedListener {
	public static final String EXTRA_USER_LOCATION = "com.android.helper.CURRENT_LOCATION";
	private static final String EXTRA_USER_CATEGORY = "com.android.helper.VOLUNTEER_CATEGORY";
	private String category = "";
	private String province = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_location_preferences);
		//Intent userLocationIntent = getIntent();
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Spinner spinner = (Spinner) findViewById(R.id.categories_dropdown);
		Spinner provSpinner = (Spinner) findViewById(R.id.province_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.categories, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> provSpinAdapter = ArrayAdapter.createFromResource(this, 
				R.array.provinces, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		provSpinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		provSpinner.setAdapter(provSpinAdapter);
		spinner.setOnItemSelectedListener(this);
		provSpinner.setOnItemSelectedListener(this);
	}

/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_location_preferences, menu);
		return true;
	}
*/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		if (id == android.R.id.home) {
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void submitUserLocation (View view){
		
		EditText addressText = (EditText)findViewById(R.id.street_name);
		EditText cityEdit = (EditText)findViewById(R.id.city);
		//Spinner provEdit = (Spinner)findViewById(R.id.province_spinner);
		EditText[] validation = {addressText,cityEdit};
		String[] message = {"Address","City", "Province/Territory"};
		if( !validateTexts(validation,message) ){
		    return;
		}
		
		Intent intent = new Intent(this, InformationDisplay.class);

		TextView streetNameText = (TextView) findViewById(R.id.street_name);
		TextView cityText = (TextView) findViewById(R.id.city);
		TextView postalCodeText = (TextView) findViewById(R.id.postal_code);
		String address = String.format("%s, %s, %s %s",
				streetNameText.getText().toString(), cityText.getText().toString(),province,postalCodeText.getText().toString());
		LatLng latAndLng = latNLongFromAddress(address);
		
		if (latAndLng != null) {
		
		intent.putExtra(EXTRA_USER_LOCATION, latAndLng);
		intent.putExtra(EXTRA_USER_CATEGORY,this.category);
		
		startActivity(intent);
		} else {
			new AlertDialog.Builder(this)
			.setTitle("Invalid Address!")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}).setIcon(android.R.drawable.ic_dialog_alert).show();
		}
	}

	public boolean validateTexts(EditText[] txt,String[] str){
		boolean validTexts = true;
		for (int i = 0; i < txt.length; i++){
			if( txt[i].getText().toString().length() == 0 ){
				String err = String.format("%s is required!", str[i]);
			    txt[i].setError( err );
			    validTexts = false;
			}
		}
		return validTexts;
	}
	
	//obtain the longitude and latitude coordinate of the address
	public LatLng latNLongFromAddress(String adr){
		double latitude;
		double longitude;
		List<Address> geocodeMatches = null;
		try {
			geocodeMatches = 
		          new Geocoder(this).getFromLocationName(
		               adr, 1);
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (!geocodeMatches.isEmpty())
		{
			latitude = geocodeMatches.get(0).getLatitude(); 
			longitude = geocodeMatches.get(0).getLongitude();
		}else {
			return null;
		}
		
		return new LatLng(latitude, longitude);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		switch (parent.getId()) {
		case R.id.categories_dropdown:
			if (parent.getItemAtPosition(position).toString().equals("All")) {
				this.category = "";
			} else {
				this.category = parent.getItemAtPosition(position).toString();
			}
			break;

		case R.id.province_spinner:
			this.province = parent.getItemAtPosition(position).toString();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

}

