package edu.feri.jager.soslokator.structure;

public class MySOSMessage {
	private String sender;
	private String longitude;
	private String latitude;
	private String adress;

	public MySOSMessage() {
		sender = longitude = latitude = adress = "N/A";
	}

	public MySOSMessage(String sender, String latitude, String longitude, String adress) {
		this.sender = sender;
		this.latitude = latitude;
		this.longitude = longitude;
		this.adress = adress;
	}

	public boolean equals(MySOSMessage message) {
		if(!sender.equalsIgnoreCase(message.getSender())) {
			return false;
		}
		if(!longitude.equalsIgnoreCase(message.getLongitude())) {
			return false;
		}
		if(!latitude.equalsIgnoreCase(message.getLatitude())) {
			return false;
		}
		if(!adress.equalsIgnoreCase(message.getAdress())) {
			return false;
		}
		
		return true;
	}
	
	public String[] getArray() {
		return new String[]{sender, latitude, longitude, adress};
	}

	public MySOSMessage(String[] array) {
		if(array.length == 4) {
			this.sender = array[0];
			this.latitude = array[1];
			this.longitude = array[2];
			this.adress = array[3];
		} else {
			new MySOSMessage();
		}
	}

	public MySOSMessage(String text) {
		String temp = new String(text);
		this.sender = temp.substring(0, temp.indexOf("\n"));
		temp = temp.substring(0, temp.indexOf("\n"));
		this.longitude = temp.substring(0, temp.indexOf("\n"));
		temp = temp.substring(0, temp.indexOf("\n"));
		this.latitude = temp.substring(0, temp.indexOf("\n"));
		temp = temp.substring(0, temp.indexOf("\n"));
		this.adress = temp.substring(0, temp.indexOf("\n"));
	}

	public String toString() {
		return sender + "\n" + latitude + "\n" + longitude + "\n" + adress;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
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

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}
}
