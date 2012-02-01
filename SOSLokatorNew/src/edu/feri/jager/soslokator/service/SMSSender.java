package edu.feri.jager.soslokator.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import edu.feri.jager.soslokator.MainActivity;
import edu.feri.jager.soslokator.MyApplication;
import edu.feri.jager.soslokator.structure.MyContacts;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SMSSender {

	private MainActivity activity = null;
	private MyApplication myApp = null;
	
	public SMSSender(MainActivity activity) {
		this.activity = activity;
		myApp = (MyApplication)activity.getApplication();
	}
	
	public void sendSMS() {
//		String str = "Sporočila so bila poslana!\n(poslanih 3 od 3 sporočil)";
//		Toast.makeText(activity, str , Toast.LENGTH_LONG).show();

		System.out.println("BUTTON SEND_SMS CLICK!!!");
		List<MyContacts> vecID = myApp.getListContactsID();

		Vector<String> vec = new Vector<String>();
		for(int j = 0; j < vecID.size(); j++) {
			Cursor phones = activity.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + vecID.get(j).getContactID(), null, null); 

			while (phones.moveToNext()) { 
				String phoneNumber = phones.getString(phones.getColumnIndex(Phone.NUMBER)); 
				
				int type = phones.getInt(phones.getColumnIndex(Phone.TYPE)); 
				if(type == Phone.TYPE_MOBILE) {
					System.out.println("PHONE: " + phoneNumber + "; " + type);
					phoneNumber = phoneNumber.replace("-", "");
					phoneNumber = phoneNumber.replace("(", "");
					phoneNumber = phoneNumber.replace(")", "");
					vec.add(phoneNumber);
				}
				
			}  
		}
		if(vec.size() > 0) {
			ProgressDialog dialog;
			String str;
			if(vec.size() == 1)
				str = "Pošiljam Sporočilo! Počakajte prosim...";
			else
				str = "Pošiljam Sporočila! Počakajte prosim...";
			dialog = ProgressDialog.show(activity, "", str, true);
			String text = LocationToString("SOS!!! Moja trenutna lokacija:");

			
			
			int messageCount = 0;
			for(int i  = 0; i < vec.size(); i++) {
				try {
					System.out.println(vec.get(i));
					SmsManager sm = SmsManager.getDefault();
					if(text.length() == 0) {
						break;
					}
					sm.sendMultipartTextMessage(vec.get(i), null, sm.divideMessage(text), null, null);
					messageCount++;
				} catch (Exception e) {
					e.printStackTrace();
						Toast.makeText(activity, "Napaka pri pošiljanju!\n" + (messageCount + 1) + ". sporo�ilo ni bilo poslano!\n(prejemnik: " + vec.get(i) + ")", Toast.LENGTH_LONG).show();
				}
				System.out.println("SMS " + i + " SENDED!!!");
			}
			dialog.cancel();
			
			
			
			if(vec.size() == 1)
				str = "Sporočilo je bilo poslano!";
			else
				str = "Sporočila so bila poslana!\n(poslanih " + messageCount + " od " + vec.size() + " sporočil)";
			Toast.makeText(activity, str , Toast.LENGTH_LONG).show();

		} else
			Toast.makeText(activity, "V seznamu ni kontaktov!\nDodajte kontakt", Toast.LENGTH_LONG).show();
	}
	
	public String LocationToString(String text) {
		Location location = myApp.getCurrLocation();
		if(location !=  null) {
			String zDolzina = myApp.convertDoubleToDegreesMinutesSeconds(location.getLongitude());
			String zSirina = myApp.convertDoubleToDegreesMinutesSeconds(location.getLatitude());

			double lat = location.getLatitude();
			double lng = location.getLongitude();
			String geodata = getLocationInfo(lat, lng);
			return "<soslokator>" + text + "\n" + "Zemljepisna širina: " + zSirina + "\n" + "Zemljepisna dolžina:" + zDolzina + "\n" + "Naslov:\n" + geodata +
					"\n<data>" + location.getLatitude() + " " + location.getLongitude() + "</data>";
		}
		return new String("");
	}
	
	private String getLocationInfo(double latitude, double longitude) {
		Geocoder gc = new Geocoder(activity, Locale.getDefault());

		try {
			List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
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

			return  sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Ni podatka";
	}
}
