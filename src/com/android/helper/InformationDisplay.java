package com.android.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class InformationDisplay extends FragmentActivity implements ActionBar.TabListener {

	private LatLng currentLatLng;
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;
	ViewPager mViewPager;
	private UserLocation customLoc;
	public ArrayList<VolunteerData> volunteerList = new ArrayList<VolunteerData>();
	HelperIntro hlprIntro ;
	InformationList infoList ;
	String infoListFragmentTag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infromation_display);
		Intent currLocIntent = getIntent();
		currentLatLng = (LatLng) currLocIntent.getExtras().get(HomeActivity.EXTRA_CURRENT_LOCATION);
		setupActionBar(); 
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
		mAppSectionsPagerAdapter.setCurLocLatLng(currentLatLng);
		mAppSectionsPagerAdapter.setVolList(volunteerList);
		mAppSectionsPagerAdapter.setKeyword(currLocIntent.getStringExtra(HomeActivity.EXTRA_VOLUNTEERING_CATEGORY));
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections, select the corresponding tab.
				// We can also use ActionBar.Tab#select() to do this if we have a reference to the
				// Tab.
				actionBar.setSelectedNavigationItem(position);
			}
		});
		actionBar.addTab(actionBar.newTab().setText("Map View").setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("List View").setTabListener(this));

	}

	public void setListFragmentTag(String tag) {
		infoListFragmentTag = tag;
	}

	public String getListFragmentTag () {
		return infoListFragmentTag;
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

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());

	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void onSetNewLocation (View view) {
		Intent userLocationIntent = new Intent(this, UserLocationPreferences.class);
		startActivity(userLocationIntent);
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		private LatLng curLocLatLng;
		private String keyword = "";
		private ArrayList<VolunteerData> volList = new ArrayList<VolunteerData>();

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int i) {
			Fragment helperIntroFragment = new HelperIntro();
			Bundle args = new Bundle();
			args.putSerializable(HelperIntro.DATA_LIST, volList);
			args.putDouble(HelperIntro.CUR_LOC_LAT, curLocLatLng.latitude);
			args.putDouble(HelperIntro.CUR_LOC_LNG, curLocLatLng.longitude);
			args.putString(HelperIntro.VOLUNTEER_CATEGORY, keyword);
			helperIntroFragment.setArguments(args);
			switch (i) {
			case 0:
				return helperIntroFragment;

			default: 
				Fragment infoFragment = new InformationList();
				infoFragment.setArguments(args);
				return infoFragment;
			}
		}

		public LatLng getCurLocLatLng() {
			return curLocLatLng;
		}

		public void setCurLocLatLng(LatLng curLocLatLng) {
			this.curLocLatLng = curLocLatLng;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		public String getKeyword() {
			return keyword;
		}

		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}

		public ArrayList<VolunteerData> getVolList() {
			return volList;
		}

		public void setVolList(ArrayList<VolunteerData> volList) {
			this.volList = volList;
		}

	}

	public static class HelperIntro extends Fragment implements OnInfoWindowClickListener{

		private LatLng curLocLatLng;
		private GoogleMap mainMap;
		SupportMapFragment mMapFragment;
		private Geocoder geoCode;
		private List<Address> locDescription;
		private String keyword="";
		private ArrayList<VolunteerData> volunteerDataList = new ArrayList<VolunteerData>();
		public static final String CUR_LOC_LAT = "com.android.HELPER.LOCATION_LAT";
		public static final String CUR_LOC_LNG = "com.android.HELPER.LOCATION_LNG";
		public static final String VOLUNTEER_CATEGORY = "com.android.HELPER.VOLUNTEER_CATEGORY";
		public static final String LOADED_DATA = "com.android.HELPER.LOADED_DATA";
		public static final String DATA_LIST = "com.android.HELPER.DATA_LIST";
		private AsyncTask<String, Integer, Integer> dataScrape;
		private GoogleMapsMarkerInfoAdapter popupAdapter = null;

		@Override 
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			geoCode = new Geocoder(getActivity(), Locale.CANADA);
			popupAdapter = new GoogleMapsMarkerInfoAdapter(inflater);
			// use setup map and pass the location data as args - try this to make it work
			Bundle args = getArguments();
			if (args != null) {
				curLocLatLng = new LatLng(args.getDouble(CUR_LOC_LAT),
						args.getDouble(CUR_LOC_LNG));
				keyword = args.getString(VOLUNTEER_CATEGORY);
			}
			View rootView = inflater.inflate(R.layout.activity_helper_intro, container, false);
			try {
				locDescription = geoCode.getFromLocation(curLocLatLng.latitude, curLocLatLng.longitude, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (locDescription != null && volunteerDataList.size() == 0) {
				dataScrape = new DataScraper(this).execute(locDescription.get(0).getAdminArea(), locDescription.get(0).getLocality(), keyword);
			}

			mMapFragment = new SupportMapFragment() {
				@Override
				public void onActivityCreated(Bundle args) {	
					super.onActivityCreated(args);
					setupMap();
					if (curLocLatLng != null) {
						mainMap.addMarker(new MarkerOptions().position(curLocLatLng).title("Your Location"));
						setCameraView(curLocLatLng);
					}

				}
			};
			android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.map_container, mMapFragment);
			fragmentTransaction.commit();
			return rootView;
		}

		public LatLng getCurLocLatLng() {
			return curLocLatLng;
		}

		public void setCurLocLatLng(LatLng curLocLatLng) {
			this.curLocLatLng = curLocLatLng;
		}

		private void setupMap () {
			if (mainMap == null) {
				mainMap = mMapFragment.getMap();
			}
		}

		private void setCameraView(LatLng loc) {
			CameraPosition camPosition = new CameraPosition.Builder().target(loc).zoom(13).build();
			mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
		}

		public void addThreadResults (ArrayList<VolunteerData> dataList) {
			validateData(dataList, mainMap);
			volunteerDataList = dataList;
			for (VolunteerData data : dataList) {
				List<Address> temp = null;
				try {
					temp = geoCode.getFromLocationName(data.getAddress(), 1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (temp != null && temp.size() > 0) {
					LatLng tempLoc = new LatLng(temp.get(0).getLatitude(), temp.get(0).getLongitude());
					mainMap.addMarker(new MarkerOptions().position(tempLoc)
							.title(data.getName())
							.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
							.snippet("Address: " + data.getAddress() + "\nPhone Number: " + data.getPhoneNumber() 
									+ "\n" + data.getCategory() 
									+ "\nFor more information, please refer to: " + data.getLink()));
					mainMap.setInfoWindowAdapter(popupAdapter);
					mainMap.setOnInfoWindowClickListener(this);
				}
			}
			String listInfoFragTag = ((InformationDisplay) getActivity()).getListFragmentTag();
			InformationList infoListFrag = (InformationList)getActivity()
					.getSupportFragmentManager()
					.findFragmentByTag(listInfoFragTag);

			infoListFrag.addThreadResults(dataList);
			if (dataList != null || dataList.size() > 0) {
				Toast.makeText(getActivity(), "Data load complete.", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		public void onInfoWindowClick(Marker marker) {
			Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_LONG).show();

		}

		public void validateData (ArrayList<VolunteerData> volData, GoogleMap validateMap) {
			if (volData == null || volData.size() == 0) {
				validateMap.clear();
				new AlertDialog.Builder(getActivity())
				.setTitle("Search Completed")
				.setMessage("No volunteer oportunities found for the particular category: " + keyword)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						getActivity().finish();
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
			}
		}

	}

	public static class InformationList extends Fragment{

		private ArrayList<VolunteerData> volunteerList = new ArrayList<VolunteerData>();
		public static final String CUR_LOC_LAT = "com.android.HELPER.LOCATION_LAT";
		public static final String CUR_LOC_LNG = "com.android.HELPER.LOCATION_LNG";
		public static final String VOLUNTEER_CATEGORY = "com.android.HELPER.VOLUNTEER_CATEGORY";
		public static final String WEB_INFO_EXTRA = "com.android.HELPER.WEB_INFO_EXTRA";

		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View viewRoot = inflater.inflate(R.layout.activity_list_info, container, false);
			((InformationDisplay) getActivity()).setListFragmentTag(getTag());
			return viewRoot;
		}

		public void setVolunteerList (ArrayList<VolunteerData> data) {
			volunteerList = data;
		}

		public ArrayList<VolunteerData> getVolunteerList () {
			return this.volunteerList;
		}

		public void addThreadResults (ArrayList<VolunteerData> dataList) {
			InformationListCustomAdapter arrayAdapter = new InformationListCustomAdapter(getActivity(), R.layout.list_view, dataList);
			ListView listOfData = (ListView) getView().findViewById(R.id.volunteer_list);
			listOfData.setAdapter(arrayAdapter);
			listOfData.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ListView lv = (ListView) getView().findViewById(R.id.volunteer_list);
					VolunteerData volObj = (VolunteerData) lv.getItemAtPosition(position);
					String url = volObj.getLink();
					if (!url.isEmpty()) {
						Intent webInfo = new Intent(getActivity(), WebviewInfoActivity.class);
						webInfo.putExtra(WEB_INFO_EXTRA, url);
						startActivity(webInfo);
					}
				}
			});
		}
	}

}
