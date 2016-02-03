package com.android.helper;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class InformationListCustomAdapter extends ArrayAdapter<VolunteerData> {
	
	private ArrayList<VolunteerData> volunteerObjects = new ArrayList<VolunteerData>();

	public InformationListCustomAdapter(Context context, int resource,
			ArrayList<VolunteerData> objects) {
		super(context, resource, objects);
		this.volunteerObjects = objects;
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.list_view, null);
		}
		VolunteerData volObject = volunteerObjects.get(position);
		if (volObject != null) {
			TextView objectName = (TextView) view.findViewById(R.id.vol_name);
			TextView objectAddress = (TextView) view.findViewById(R.id.vol_address);
			TextView objectPhone = (TextView) view.findViewById(R.id.vol_phone);
			
			if (objectName != null) {
				objectName.setText(volObject.getName());
			}
			if (objectAddress != null) {
				objectAddress.setText(volObject.getAddress());
			}
			if (objectPhone != null) {
				objectPhone.setText(volObject.getPhoneNumber());
			}
		}
		return view;
	}

}
