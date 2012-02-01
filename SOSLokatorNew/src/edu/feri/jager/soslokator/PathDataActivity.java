package edu.feri.jager.soslokator;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import edu.feri.jager.soslokator.structure.MyLocation;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class PathDataActivity extends Activity {
	TextView textViewMyLatitude;
	TextView textViewMyLongitude;
	TextView textViewMyAdress;
	TextView textViewSosLatitude;
	TextView textViewSosLongitude;
	TextView textViewSosAdress;
	TextView textViewSosAdressHeader;
	TextView textViewSosHeader;

	MyApplication myApp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myApp = (MyApplication)getApplication();
		setContentView(R.layout.path_data);
		initElements();
		setData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Bundle bundle = getIntent().getExtras();
		if(bundle == null || !bundle.containsKey("sosData")) {			
			textViewSosHeader.setVisibility(TextView.GONE);
			textViewSosAdress.setVisibility(TextView.GONE);
			textViewSosLatitude.setVisibility(TextView.GONE);
			textViewSosLongitude.setVisibility(TextView.GONE);
			textViewSosAdressHeader.setVisibility(TextView.GONE);
		} else {
			textViewSosHeader.setVisibility(TextView.VISIBLE);
			textViewSosAdress.setVisibility(TextView.VISIBLE);
			textViewSosLatitude.setVisibility(TextView.VISIBLE);
			textViewSosLongitude.setVisibility(TextView.VISIBLE);
			textViewSosAdressHeader.setVisibility(TextView.VISIBLE);
		}

	}

	private void initElements() {
		textViewMyAdress = (TextView) findViewById(R.id.layout_path_data_textview_my_location_adress);
		textViewMyLatitude = (TextView) findViewById(R.id.layout_path_data_textview_my_location_latitude);
		textViewMyLongitude = (TextView) findViewById(R.id.layout_path_data_textview_my_location_longitude);
		textViewSosAdress = (TextView) findViewById(R.id.layout_path_data_textview_sos_location_adress);
		textViewSosLatitude = (TextView) findViewById(R.id.layout_path_data_textview_sos_location_latitude);
		textViewSosLongitude = (TextView) findViewById(R.id.layout_path_data_textview_sos_location_longitude);
		textViewSosAdressHeader = (TextView) findViewById(R.id.layout_path_data_textview_sos_location_adress_header);
		textViewSosHeader = (TextView) findViewById(R.id.layout_path_data_textview_sos_location_header);
	}

	private void setData() {
		Location location = myApp.getCurrLocation();
		textViewMyLatitude.setText(getString(R.string.latitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(location.getLatitude()));
		textViewMyLongitude.setText(getString(R.string.longitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(location.getLongitude()));

		Geocoder gc = new Geocoder(this, Locale.getDefault());

		try {
			List<Address> addresses = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			//obstaja tudi obratno iz imena latatude ... getFromLocationName
			StringBuilder sb = new StringBuilder();
			if (addresses.size() > 0) {
				Address address = addresses.get(0);

				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) 
					sb.append(address.getAddressLine(i)).append("\n");
				sb.append(address.getCountryName());
			}
			textViewMyAdress.setText(sb);


		} catch (IOException e) {
			e.printStackTrace();
			textViewMyAdress.setText(getString(R.string.nodata));
		}

		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("sosData")) {
			MyLocation sosLocation = new MyLocation(bundle.getStringArray("sosData"));
			gc = new Geocoder(this, Locale.getDefault());

			textViewSosLatitude.setText(getString(R.string.latitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(Double.parseDouble(sosLocation.getLatitude())));
			textViewSosLongitude.setText(getString(R.string.longitude_text) + ": " + myApp.convertDoubleToDegreesMinutesSeconds(Double.parseDouble(sosLocation.getLongitude())));


			try {
				List<Address> addresses = gc.getFromLocation(Double.parseDouble(sosLocation.getLatitude()), Double.parseDouble(sosLocation.getLongitude()), 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);

					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) 
						sb.append(address.getAddressLine(i)).append("\n");
					sb.append(address.getCountryName());
				}
				textViewSosAdress.setText(sb);


			} catch (IOException e) {
				e.printStackTrace();
				textViewSosAdress.setText(getString(R.string.nodata));
			}
		}
		else {
			textViewSosHeader.setVisibility(TextView.GONE);
			textViewSosAdress.setVisibility(TextView.GONE);
			textViewSosLatitude.setVisibility(TextView.GONE);
			textViewSosLongitude.setVisibility(TextView.GONE);
			textViewSosAdressHeader.setVisibility(TextView.GONE);
		}
	}
}
