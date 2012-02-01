package edu.feri.jager.soslokator.service;

import edu.feri.jager.soslokator.PathActivity;
import edu.feri.jager.soslokator.structure.MyLocation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
	private static final String SOS_MESSAGE_CONTENT = new String("<soslokator>");

	private static final String SOS_DATA_START = new String("<data>");
	private static final String SOS_DATA_END = new String("</data>");
	private static final String SOS_ADRESS_CONTENT = new String("Naslov:\n");
	
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}
		if(smsMessage.length > 0) {
			String body = "";
			
			for(int i = 0; i < messages.length; i++) {
				body += smsMessage[i].getMessageBody();

			}
						
			if(body.startsWith(SOS_MESSAGE_CONTENT)) {
				String sender = smsMessage[0].getOriginatingAddress();
				if(sender.startsWith("+")) {
					sender = "0" + sender.substring(4);
				}

				try {
					if(body.indexOf(SOS_DATA_START) > 0 && body.indexOf(SOS_DATA_END) > 0 && body.indexOf(SOS_ADRESS_CONTENT) > 0) {
						String s = body.substring(body.indexOf(SOS_DATA_START) + SOS_DATA_START.length(), body.indexOf(SOS_DATA_END));
						
						String[] data = s.split(" ");
						String latitude = data[0];
						String longitude = data[1];
						
						MyLocation location = new MyLocation(sender, longitude, latitude);
						
						try {
							Intent i = new Intent(context, PathActivity.class);  
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
							i.putExtra("smsData", location.getArray());
							context.startActivity(i); 
//							this.abortBroadcast();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("NAPAKA PRI PREJEMU SMS SPOROCILA!");
				}		
			}
		}
	}
}
