package edu.feri.jager.soslokator.structure;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class MyLocation {

	private String phoneNumber;
	private String longitude;
	private String latitude;
	private String timestamp;
	private String displayName;

	private long id;

	public MyLocation() {
		phoneNumber = new String();
		longitude = new String();
		latitude = new String();
		setDisplayName(new String());
		timestamp = getCurrTimeString();
		id = -1;
	}

	public MyLocation(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		this.longitude = new String();
		this.latitude = new String();
		this.displayName = new String();
		timestamp = getCurrTimeString();
		this.id = -1;
	}

	public MyLocation(String phoneNumber, String longitude, String latitude) {
		this.phoneNumber = phoneNumber;
		this.longitude = longitude;
		this.latitude = latitude;
		this.displayName = new String();
		timestamp = getCurrTimeString();
		this.id = -1;
	}



	public MyLocation(String[] array) {
		if(array.length == 5) {
			this.phoneNumber = array[0];
			this.displayName = array[1];
			this.latitude = array[2];
			this.longitude = array[3];
			this.timestamp = array[4];
		} else {
			new MyLocation();
		}
	}

	public String[] getArray() {
		return new String[]{phoneNumber, displayName, latitude, longitude, timestamp};
	}

	private String getCurrTimeString() {
		StringBuilder builder = new StringBuilder();

		// get the supported ids for GMT-08:00 (Pacific Standard Time)
		String[] ids = TimeZone.getAvailableIDs(60 * 60 * 1000);
		// if no ids were returned, something is wrong. get out.
		if (ids.length == 0)
			System.exit(0);

		// create a Pacific Standard Time time zone
		SimpleTimeZone pdt = new SimpleTimeZone(60 * 60 * 1000, ids[0]);

		// set up rules for daylight savings time
		pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
		pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

		// create a GregorianCalendar with the Pacific Daylight time zone
		// and the current date and time
		Calendar calendar = new GregorianCalendar(pdt);
		Date currTime = new Date();
		calendar.setTime(currTime);
		if(calendar.get(Calendar.DATE) < 10) {
			builder.append("0");
		}
		builder.append(calendar.get(Calendar.DATE) + ".");
		if(calendar.get(Calendar.MONTH) < 10) {
			builder.append("0");
		}
		builder.append((calendar.get(Calendar.MONTH) + 1) + ".");
		builder.append(calendar.get(Calendar.YEAR) + " ");
		if(calendar.get(Calendar.HOUR_OF_DAY) < 10) {
			builder.append("0");
		}
		builder.append(calendar.get(Calendar.HOUR_OF_DAY) + ":");
		if(calendar.get(Calendar.MINUTE) < 10) {
			builder.append("0");
		}
		builder.append(calendar.get(Calendar.MINUTE) + ":");
		if(calendar.get(Calendar.SECOND) < 10) {
			builder.append("0");
		}
		builder.append(calendar.get(Calendar.SECOND));

		return builder.toString();
	}

	public boolean equal(MyLocation structure) {
		if(id != structure.getId()) {
			return false;
		}
		if(!phoneNumber.equalsIgnoreCase(structure.getPhoneNumber())) {
			return false;
		}
		if(!longitude.equalsIgnoreCase(structure.getLongitude())) {
			return false;
		}
		if(!latitude.equalsIgnoreCase(structure.getLatitude())) {
			return false;
		}
		if(!timestamp.equalsIgnoreCase(structure.getTimestamp())) {
			return false;
		}
		if(!displayName.equalsIgnoreCase(structure.getDisplayName())) {
			return false;
		}

		return true;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}