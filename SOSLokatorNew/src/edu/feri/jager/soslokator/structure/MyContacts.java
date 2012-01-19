package edu.feri.jager.soslokator.structure;

public class MyContacts {
	private String contactID;
	private long id;

	public MyContacts() {
		this.contactID = "N/A";
		this.id = -1;
	}
	
	public MyContacts(String contactID) {
		this.contactID = contactID;
		this.id = -1;
	}
	
	public boolean equals(MyContacts contacts) {
		if(!contactID.equalsIgnoreCase(contacts.getContactID())) {
			return false;
		}
		if(id != contacts.getId()) {
			return false;
		}
		
		return true;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public void setContactID(String contactID) {
		this.contactID = contactID;
	}
	
	public String getContactID() {
		return contactID;
	}
}
