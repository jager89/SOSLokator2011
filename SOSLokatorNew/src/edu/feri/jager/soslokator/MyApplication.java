package edu.feri.jager.soslokator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.feri.jager.soslokator.database.ContactsDBAdapter;
import edu.feri.jager.soslokator.database.LocationsDBAdapter;
import edu.feri.jager.soslokator.location.MyLocationListener;
import edu.feri.jager.soslokator.structure.MyContacts;
import edu.feri.jager.soslokator.structure.MyLocation;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class MyApplication extends Application {
	
	private final int MIN_DISTANCE = new Integer(10);
	private final int MIN_TIME = new Integer(2000);
	private final int VALUE = new Integer(60); 

	private Location currLocation = null;
	private LocationManager locationManager = null;
	private ContactsDBAdapter dbContacts = null;
	private LocationsDBAdapter dbLocation = null;
	
	private MainActivity  mainActivity = null;
	
	private List<MyContacts> listContactsID = null;
	private List<MyLocation> listLocations = null;
	
	private int widgetCounter;
	
	@Override
	public void onCreate() {
		super.onCreate();
		widgetCounter = -1;
		
		dbContacts = new ContactsDBAdapter(this);
		dbLocation = new LocationsDBAdapter(this);
		
		requestLocation();
		
		fillContactsFromDB();
		fillLocationsFromDB();
	}
		
	public void requestLocation() {
		if(locationManager == null) {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
//		if(locationManager != null) {
			String provider = locationManager.getBestProvider(getCriteria(), true);
//			Location location = locationManager.getLastKnownLocation(provider);
//			if(location != null) {
//				setCurrLocation(location);
//			}
			locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, new MyLocationListener(this));
//		}
	}
	
	private Criteria getCriteria() {
		Criteria temp = new Criteria();

		temp.setPowerRequirement(Criteria.POWER_LOW);
		temp.setAccuracy(Criteria.ACCURACY_FINE);
		temp.setAltitudeRequired(false);
		temp.setBearingRequired(false);
		temp.setCostAllowed(true);

		return temp;
	}
	
	public String convertDoubleToDegreesMinutesSeconds(double value) {
		int degrees = (int) value;
		value = Math.abs(value);
		value = (value - (int) value) * VALUE; 
		int minutes = (int) value;
		value = (value - (int) value) * VALUE; 
		int seconds = (int) value;

		return degrees + "° " + minutes + "' " + seconds + "\"";
	}

	public void fillContactsFromDB() {
		listContactsID = new Vector<MyContacts>();
		dbContacts.open();
		Cursor c = dbContacts.getAll();
		MyContacts tmp;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			tmp = new MyContacts();
			tmp.setContactID(c.getString(ContactsDBAdapter.POS_CONTACT_ID));
			tmp.setId(c.getLong(ContactsDBAdapter.POS__ID));
			listContactsID.add(tmp); 
		}
		c.close();

		for(int i = 0; i < listContactsID.size(); i++) {
			Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " +  listContactsID.get(i).getContactID(), null, null); 
			if(phones != null && !phones.moveToFirst()) {
				dbContacts.deleteContact(listContactsID.get(i).getId());
				listContactsID.remove(i);
			}
		}
		dbContacts.close();

	}
	
	
	

	public void fillLocationsFromDB() {
		listLocations = new ArrayList<MyLocation>();
		dbLocation.open();
		Cursor c = dbLocation.getAll();
		MyLocation location;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			location = new MyLocation();
			location.setPhoneNumber(c.getString(LocationsDBAdapter.POS_PHONE_NUMBER));
			location.setDisplayName(c.getString(LocationsDBAdapter.POS_DISPLAY_NAME));
			location.setLongitude(c.getString(LocationsDBAdapter.POS_LONGITUDE));
			location.setLatitude(c.getString(LocationsDBAdapter.POS_LATITUDE));
			location.setTimestamp(c.getString(LocationsDBAdapter.POS_TIMESTAMP));
			location.setId(c.getLong(LocationsDBAdapter.POS__ID));
			listLocations.add(location); 
		}
		c.close();

		List<MyLocation> tempList = new ArrayList<MyLocation>();
		for(int i = listLocations.size() - 1; i >= 0; i--) {
			tempList.add(listLocations.get(i));
		}
		listLocations = tempList;
		
		dbLocation.close();
	}
	public void addLocationToDB(MyLocation s) {
		dbLocation.open();
		s.setId(dbLocation.insertContact(s));
		dbLocation.close();	
	}
	
	public void removeLocationFromDB(long id) {
		dbLocation.open();
		dbLocation.deleteContact(id);
		dbLocation.close();
	}
		
	public void addContactToDB(MyContacts s) {
		dbContacts.open();
		s.setId(dbContacts.insertContact(s));
		dbContacts.close();	
	}

	public void removeContactFromDB(long id) {
		dbContacts.open();
		dbContacts.deleteContact(id);
		dbContacts.close();
	}

	public List<MyLocation> getListLocations() {
		return listLocations;
	}
	
	public List<MyContacts> getListContactsID() {
		return listContactsID;
	}
	
	public void refreshCurrLocation() {
		mainActivity.refreshCurrLocation();
	}

	public Location getCurrLocation() {
		return currLocation;
	}
	
	public MainActivity getMainActivity() {
		return mainActivity;
	}

	public void setCurrLocation(Location currLocation) {
		this.currLocation = currLocation;
	}
	
	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}
	public int getWidgetCounter() {
		return widgetCounter;
	}
	public void setWidgetCounter(int widgetCounter) {
		this.widgetCounter = widgetCounter;
	}	
}
