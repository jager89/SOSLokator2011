package edu.feri.jager.soslokator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class LocationDataActivity extends Activity {
	TextView textViewLatitude;
	TextView textViewLongitude;
	TextView textViewAdress;
	
	MyApplication myApp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication)getApplication();
		setContentView(R.layout.location_data);
		initElements();
		setData();
	}

	private void initElements() {
		textViewAdress = (TextView) findViewById(R.id.layout_data_textview_location_adress);
		textViewLatitude = (TextView) findViewById(R.id.layout_data_textview_location_latitude);
		textViewLongitude = (TextView) findViewById(R.id.layout_data_textview_location_longitude);
	}
	
	private void setData() {
		Location location = myApp.getCurrLocation();
		textViewLatitude.setText(getString(R.string.latitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(location.getLatitude()));
		textViewLongitude.setText(getString(R.string.longitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(location.getLongitude()));
		
		Geocoder gc = new Geocoder(this, Locale.getDefault());

		try {
			List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			//obstaja tudi obratno iz imena latatude ... getFromLocationName
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) 
				sb.append(address.getAddressLine(i)).append("\n");
//				sb.append(address.getLocality()).append("\n");
//				sb.append(address.getPostalCode()).append("\n");
				sb.append(address.getCountryName());
			}
			textViewAdress.setText(sb);

			
		} catch (IOException e) {
			e.printStackTrace();
			textViewAdress.setText(getString(R.string.widget_text_nodata));
		}
	}
}
