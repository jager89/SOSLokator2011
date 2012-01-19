package edu.feri.jager.soslokator.location;

import edu.feri.jager.soslokator.MyApplication;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {

	private final int TWO_MINUTES = 1000 * 60 * 2;
	private MyApplication myApp;

	public MyLocationListener(MyApplication myApp) {
		this.myApp = myApp;
	}

	public void onLocationChanged(Location location) {
		if(isBetterLocation(location, myApp.getCurrLocation())) {
			myApp.setCurrLocation(location);
			myApp.refreshCurrLocation();
		}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		System.out.println("STATUS SPREMENJEN : " + provider + ", status: " + status + "; extras: " + extras);
	}

	public void onProviderEnabled(String provider) {
		System.out.println("PROVIDER ENABLED: " + provider);
	}

	public void onProviderDisabled(String provider) {
		System.out.println("PROVIDER DISABLED: " + provider);
	}

	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	private boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null)
			return true;

		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		if (isSignificantlyNewer)
			return true;
		else if (isSignificantlyOlder)
			return false;

		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		if (isMoreAccurate)
			return true;
		else if (isNewer && !isLessAccurate)
			return true;
		else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
			return true;

		return false;
	}
}
