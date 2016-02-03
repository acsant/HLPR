package com.android.helper;

public class VolunteerData {

	private String link = null;
	private String address = "";
	private String category = "";
	private String name = "";
	private String phoneNumber = "";
	public VolunteerData (String link, String address, String category, String name, String phoneNumber) {
		this.link = link;
		this.address = address;
		this.category = category;
		this.name = name;
		this.setPhoneNumber(phoneNumber);
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Information for: " + name +
				"\nAddress: " + address + 
				"\nPhone Number: " + phoneNumber +
				"\n" + category;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}

