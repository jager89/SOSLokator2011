package edu.feri.jager.soslokator;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import edu.feri.jager.soslokator.overlay.MyPathOverlay;
import edu.feri.jager.soslokator.overlay.MyPositionOverlay;
import edu.feri.jager.soslokator.service.DirectionPathData;
import edu.feri.jager.soslokator.structure.MyGeoPoint;
import edu.feri.jager.soslokator.structure.MyLocation;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PathActivity extends MapActivity{

	private TextView textViewTimestamp;
	private TextView textViewSender;
	private MapView mapView;
	private MyLocation message;
	private MyApplication myApp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.path);
		myApp = (MyApplication)getApplication();

		Bundle extras = getIntent().getExtras();
		if(extras.containsKey("smsData")) {
			message = new MyLocation(extras.getStringArray("smsData"));
			message.setDisplayName(getContactDisplayNameByNumber(message.getPhoneNumber()));
			myApp.addLocationToDB(message);

		} else if(extras.containsKey("dbData")) {
			message = new MyLocation(extras.getStringArray("dbData"));
		}

		
		
		if(checkInternetConnection()) {
			if(message == null || message.equal(new MyLocation())) {
				Toast.makeText(this, "NAPAKA!", Toast.LENGTH_SHORT);
			} else {
				initElements();
				drawMap(message);
			}
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
	protected void onResume() {
		super.onResume();

		if (checkInternetConnection()) {

			if(textViewTimestamp == null) {
				textViewTimestamp = (TextView) findViewById(R.id.layout_path_textview_location_timestamp);
			}
			textViewTimestamp.setVisibility(TextView.GONE);

			if (textViewSender == null) {
				textViewSender = (TextView) findViewById(R.id.layout_path_textview_location_sender);
			}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.path_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_path_exit:
			finish();
			return true;
		case R.id.menu_path_data:

			System.out.println("ADD BUTTON");
			Intent intent = new Intent(PathActivity.this, PathDataActivity.class);
			intent.putExtra("sosData", message.getArray());
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}

	public void onTextViewLocationSenderClick(View v) {
		if(textViewTimestamp.getVisibility() == TextView.VISIBLE) {
			textViewTimestamp.setVisibility(TextView.GONE);
		} else {
			textViewTimestamp.setVisibility(TextView.VISIBLE);
		}
	}

	public String getContactDisplayNameByNumber(String number) {
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		String name = new String();

		ContentResolver contentResolver = getContentResolver();
		Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
				ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

		try {
			if (contactLookup != null && contactLookup.getCount() > 0) {
				contactLookup.moveToNext();
				name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
			}
		} finally {
			if (contactLookup != null) {
				contactLookup.close();
			}
		}

		return name;
	}

	private void initElements() {
		textViewTimestamp = (TextView) findViewById(R.id.layout_path_textview_location_timestamp);
		textViewTimestamp.setVisibility(TextView.GONE);

		textViewSender = (TextView) findViewById(R.id.layout_path_textview_location_sender);
		mapView = (MapView) findViewById(R.id.layout_path_mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(true);
		mapView.displayZoomControls(true);
		MapController mapController = mapView.getController();
		mapController.setZoom(15);
	}

	private void drawMap(MyLocation message) {
		mapView.getOverlays().clear();
		Location location = myApp.getCurrLocation();
		if(location == null) {
			Toast.makeText(this, "Trenutna lokacija ni določena. Pot ni bila izrisana.", Toast.LENGTH_SHORT);
		} else {
			textViewTimestamp.setText(message.getTimestamp());

			if(message.getDisplayName().length() == 0) {
				textViewSender.setText(message.getPhoneNumber());
			}
			else {
				textViewSender.setText(message.getDisplayName() + " [" + message.getPhoneNumber() + "]");
			}

			List<Overlay> overlays = mapView.getOverlays();
			overlays.add(new MyPositionOverlay(location, "VAŠA LOKACIJA"));

			MyPositionOverlay sosPositionOverlay = new MyPositionOverlay("CILJ");
			Location sosLocation = new Location("SOSLocation");

			String sLongitude = message.getLongitude();
			String sLatitude = message.getLatitude();

			double endLatitude = Double.parseDouble(sLatitude);
			sosLocation.setLatitude(endLatitude);

			double endLongitude = Double.parseDouble(sLongitude);
			sosLocation.setLongitude(endLongitude);

			sosPositionOverlay.setLocation(sosLocation);
			overlays.add(sosPositionOverlay);

			Double startLatitude = location.getLatitude();
			Double startLongitude = location.getLongitude();
			MyGeoPoint startPoint = new MyGeoPoint(startLatitude, startLongitude);
			MyGeoPoint endPoint = new MyGeoPoint(endLatitude, endLongitude);	

			DirectionPathData directionPathData = new DirectionPathData();
			List<List<MyGeoPoint>> list = directionPathData.getDirectionData(startPoint, endPoint);

			if(list != null) {
				GeoPoint startGP = new GeoPoint(
						(int) (list.get(0).get(0).getLatitude() * 1E6), (int) (list.get(0).get(0).getLongitude() * 1E6));

				GeoPoint gp1;
				GeoPoint gp2 = startGP;

				for(int i = 0; i < list.size(); i++) {
					List<MyGeoPoint> pairs = list.get(i);
					for (int j = 1; j < pairs.size(); j++) {
						gp1 = gp2;
						gp2 = new GeoPoint((int) (pairs.get(j).getLatitude() * 1E6),
								(int) (pairs.get(j).getLongitude() * 1E6));
						overlays.add(new MyPathOverlay(gp1, gp2, Color.RED, 3));
					}
				}
			}
		}
	}

	public boolean checkInternetConnection() {
		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
