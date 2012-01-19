package edu.feri.jager.soslokator;

import java.util.List;

import edu.feri.jager.soslokator.structure.MyContacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ContactsActivity extends ListActivity implements OnItemClickListener, OnItemLongClickListener{
	private static final int CONTACTS_REQ = 123;
	MyApplication mainApp;
	Menu mMenu;
	View lastSelectedView = null;
	int lastSelectedItem;
	Drawable selectedBackgrund;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mainApp = (MyApplication) getApplication();

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getList()));

		lastSelectedItem = -1;

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contacts_list_menu, mMenu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_contacts_add:

			System.out.println("ADD BUTTON");
			Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
			startActivityForResult(intent, CONTACTS_REQ);

			return true;
		default:
			return false;
		}
	}

	private String[] getList() {
		List<MyContacts> vec = mainApp.getListContactsID();

		if(vec == null || vec.size() == 0)
			return new String[]{"Seznam prejemnikov je prazen, dodajte prejemnika!"};

		String[] str = new String[vec.size()];
		for(int i = 0; i < vec.size(); i++) {
			Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + vec.get(i).getContactID(), null, null); 
			if(phones.moveToFirst()) {
				str[i] = phones.getString(phones.getColumnIndex(Phone.DISPLAY_NAME));    
			}
			phones.close();
		}

		return str;
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Brisasnje kontakta")
		.setMessage("Ali ste preprièani, da želite odstraniti kontakt?")
		.setPositiveButton("Da", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				long id = mainApp.getListContactsID().get(ContactsActivity.this.getSelectedItemPosition()).getId();
				mainApp.removeContactFromDB(id);
				mainApp.getListContactsID().remove(ContactsActivity.this.getSelectedItemPosition());
				ContactsActivity.this.setListAdapter(new ArrayAdapter<String>(ContactsActivity.this, android.R.layout.simple_list_item_1, getList()));  
			}

		})
		.setNegativeButton("Ne", null)
		.show();

		return true;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(intent, CONTACTS_REQ);

		//		if(mainApp.getListContactsID().size() == 0) {
		//			Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		//			startActivityForResult(intent, CONTACTS_REQ);
		//		} else {	
		//			if (lastSelectedView != null) {
		//				lastSelectedView.setBackgroundColor(Color.BLACK);
		//			}
		//			lastSelectedItem = position;
		//			lastSelectedView = view;
		//			lastSelectedView.requestFocus();
		//
		//			lastSelectedView.setBackgroundColor(Color.BLUE);
		//		}
		//				lastSelectedView.setBackgroundResource(android.R.drawable.list_selector_background);
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		if(reqCode == CONTACTS_REQ) {
			if (resultCode == Activity.RESULT_OK) {
				addContact(data.getData());

				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getList()));
			}
		}
	}

	private void addContact(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		if(cursor.moveToFirst()) {
			boolean exists = false;
			String contactId = cursor.getString(cursor.getColumnIndex(Contacts._ID)); 
			for(int i = 0; i < mainApp.getListContactsID().size(); i++) 
				if(mainApp.getListContactsID().get(i).getContactID().equalsIgnoreCase(contactId)) {
					exists = true;
					break;
				}

			if(!exists) {
				String t = "";
				Cursor phones = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null, null); 
				if(phones.moveToFirst()) {
					t = phones.getString(phones.getColumnIndex(Phone.NUMBER));    
				}
				phones.close();

				if(t.equalsIgnoreCase("")) {
					Toast.makeText(ContactsActivity.this, "Kontakt, ki ste ga izbrali ne vsebuje kontaktne številke.", Toast.LENGTH_SHORT).show();
				} else {
					mainApp.getListContactsID().add(new MyContacts(contactId));
					int size = mainApp.getListContactsID().size();
					mainApp.addContactToDB(mainApp.getListContactsID().get(size - 1));
				}
			}
		}
	}


}