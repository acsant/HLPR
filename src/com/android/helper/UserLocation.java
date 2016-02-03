package com.android.helper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6822888046645352483L;
	private String aptNumber;
	private String streetName;
	private String province;
	private String city;
	private String postalCode;
	private LatLng latNLng;

	
	public UserLocation(String apt, String street, String prov, String city, String post){
		this.aptNumber=apt;
		this.streetName=street;
		this.province=prov;
		this.city=city;
		this.postalCode=post;
	}
	
	public UserLocation(){
		
	}
	
	public void setAptNumber(String apt){
		this.aptNumber = apt;
	}
	
	public void setStreetName(String streetName){
		this.streetName = streetName;
	}
	
	public void setProvince(String province){
		this.province = province;
	}
	
	public void setCity(String city){
		this.city = city;
	}
	
	public void setPostalCode(String postalCode){
		this.postalCode = postalCode;
	}
	
	public String getAptNumber(){
		return this.aptNumber;
	}
	
	public String getStreetName(){
		return this.streetName;
	}
	
	public String getProvince(){
		return this.province;
	}
	
	public String getCity(){
		return this.city;
	}
	
	public String getPostalCode(){
		return this.postalCode;
	}
	

	public LatLng getLatandLang(){
		return this.latNLng;
	}
	
	
	public void setLatAndLang(LatLng loc){
		this.latNLng = loc;
	}
	
}
