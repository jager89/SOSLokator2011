package edu.feri.jager.soslokator;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import edu.feri.jager.soslokator.overlay.MyPositionOverlay;
import edu.feri.jager.soslokator.service.SMSSender;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends MapActivity {

	private TextView textViewLatitude;
	private TextView textViewLongitude;
	private TextView textViewStatus;
	private Button buttonMulti;

	private MyApplication myApp;
	private MapView mapView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myApp = (MyApplication) getApplication();
		myApp.setMainActivity(this);

		if(checkInternetConnection()) {
			initElements();	
		}
		else {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("INTERNET??")
			.setMessage("Ni zaznane internetne povezave.\nVzpostavite internetno povezavo in ponovno odprite aplikacijo.")
			.setPositiveButton("V redu", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(checkInternetConnection()) {
			if(textViewLatitude == null) {
				textViewLatitude = (TextView) findViewById(R.id.layout_main_textview_location_latitude);
			}
			textViewLatitude.setVisibility(TextView.GONE);

			if (textViewLongitude == null) {
				textViewLongitude = (TextView) findViewById(R.id.layout_main_textview_location_longitude);
			}
			textViewLongitude.setVisibility(TextView.GONE);
		}
		else {
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle("INTERNET??")
			.setMessage("Ni zaznane internetne povezave.\nVzpostavite internetno povezavo in ponovno odprite aplikacijo.")
			.setPositiveButton("V redu", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.show();
		}
	}

	private void initElements() {
		textViewStatus = (TextView) findViewById(R.id.layout_main_textview_location_status);

		textViewLatitude = (TextView) findViewById(R.id.layout_main_textview_location_latitude);
		textViewLatitude.setVisibility(TextView.GONE);

		textViewLongitude = (TextView) findViewById(R.id.layout_main_textview_location_longitude);
		textViewLongitude.setVisibility(TextView.GONE);

		mapView = (MapView) findViewById(R.id.layout_main_mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapView.displayZoomControls(true);
		MapController mapController = mapView.getController();
		mapController.setZoom(15);

		refreshCurrLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_main_refresh:
			refreshCurrLocation();
			break;
		case R.id.menu_main_data:
			if(myApp.getCurrLocation() == null) {
				Toast.makeText(MainActivity.this, "Vaša trenutna lokacija še ni doloèena. Ni podatkov o lokaciji.", Toast.LENGTH_SHORT).show();
			} else {
				startActivity(new Intent(MainActivity.this, PathDataActivity.class));						

			}					
			break;
		case R.id.menu_main_sendsms:
			if(myApp.getCurrLocation() == null) {
				Toast.makeText(MainActivity.this, "Vaša trenutna lokacija še ni doloèena. Sporoèilo ni bilo poslano.", Toast.LENGTH_SHORT).show();
			} else {
				SMSSender sender = new SMSSender(MainActivity.this);
				sender.sendSMS();						
			}
			break;
		case R.id.menu_main_contacts:
			startActivity(new Intent(MainActivity.this, ContactsActivity.class));
			break;
		case R.id.menu_main_sospaths:
			startActivity(new Intent(MainActivity.this, PathListActivity.class));
		case R.id.menu_main_exit:
			finish();
		default:
			return false;
		}
		return true;
	}

	public void onTextViewLocationStatusClick(View v) {
		if(textViewLatitude.getVisibility() == TextView.VISIBLE) {
			textViewLatitude.setVisibility(TextView.GONE);
		} else {
			textViewLatitude.setVisibility(TextView.VISIBLE);
		}	
		if(textViewLongitude.getVisibility() == TextView.VISIBLE) {
			textViewLongitude.setVisibility(TextView.GONE);
		} else {
			textViewLongitude.setVisibility(TextView.VISIBLE);
		}
	}  

	public void refreshCurrLocation() {
		StringBuilder latitudeText = new StringBuilder(getString(R.string.latitude_text) + ": ");
		StringBuilder longitudeText = new StringBuilder(getString(R.string.longitude_text) + ": ");
		Location location = myApp.getCurrLocation();

		if(location == null) {
			latitudeText.append(getString(R.string.widget_text_nodata));
			longitudeText.append(getString(R.string.widget_text_nodata));
			if(textViewStatus != null)
			textViewStatus.setText(getString(R.string.getting_location_data));
			if(textViewLatitude != null)
			textViewLatitude.setVisibility(TextView.GONE);
			if(textViewLongitude != null)	
			textViewLongitude.setVisibility(TextView.GONE);
		}
		else {
			latitudeText.append(myApp.convertDoubleToDegreesMinutesSeconds(location.getLatitude()));
			longitudeText.append(myApp.convertDoubleToDegreesMinutesSeconds(location.getLongitude()));
			if(textViewStatus != null)
			textViewStatus.setText(getString(R.string.location_header));
			if(textViewLatitude != null)
			textViewLatitude.setVisibility(TextView.VISIBLE);
			if(textViewLongitude != null)
			textViewLongitude.setVisibility(TextView.VISIBLE);
			if(mapView != null) {
				mapView.getOverlays().clear();
				mapView.getOverlays().add(new MyPositionOverlay(location, "VAŠA LOKACIJA"));
				
				Double geoLat = location.getLatitude() * 1E6;
				Double geoLng = location.getLongitude() * 1E6;
				MapController mapController = mapView.getController();
				
				mapController.animateTo(new GeoPoint(geoLat.intValue(), geoLng.intValue()));				
			}
		}
		if(textViewLatitude != null)
		textViewLatitude.setText(latitudeText);
		if(textViewLongitude != null)
		textViewLongitude.setText(longitudeText);
	}

	public boolean checkInternetConnection() {
		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public TextView getTextViewLatitude() {
		return textViewLatitude;
	}

	public TextView getTextViewLongitude() {
		return textViewLongitude;
	}

	public TextView getTextViewStatus() {
		return textViewStatus;
	}

	public MapView getMapView() {
		return mapView;
	}

	public Button getButtonMulti() {
		return buttonMulti;
	}
}