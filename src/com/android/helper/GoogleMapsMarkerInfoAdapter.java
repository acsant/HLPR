package com.android.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class GoogleMapsMarkerInfoAdapter implements InfoWindowAdapter {
	
	LayoutInflater inflater = null;
	
	public GoogleMapsMarkerInfoAdapter(LayoutInflater inflater) {
		this.inflater = inflater;
	}

	@Override
	public View getInfoContents(Marker marker) {
		View popupContentView = inflater.inflate(R.layout.marker_popup, null);
		TextView markerText = (TextView) popupContentView.findViewById(R.id.marker_title);
		markerText.setText(marker.getTitle());
		markerText = (TextView) popupContentView.findViewById(R.id.marker_snippet);
		markerText.setText(marker.getSnippet());
		
		return popupContentView;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}

}
