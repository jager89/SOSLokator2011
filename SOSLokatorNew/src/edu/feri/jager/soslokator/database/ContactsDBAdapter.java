package edu.feri.jager.soslokator.database;

import edu.feri.jager.soslokator.structure.MyContacts;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ContactsDBAdapter implements BaseColumns {
	public static final  String CONTACT_ID = "contactID";
	public static final  int POS__ID = 0;
	public static final  int POS_CONTACT_ID = 1;
	public static final  String TABLE = "contactsID";

	private final Context context;
	private ContactsDBHelper DBHelper;
	private SQLiteDatabase db;

	public ContactsDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new ContactsDBHelper(context);
	}

	public ContactsDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public long insertContact(MyContacts contact) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(CONTACT_ID, contact.getContactID()); 

		return db.insert(TABLE, null, initialValues);
	}


	public boolean deleteContact(long rowId) {
		return db.delete(TABLE, _ID + "=" + rowId, null) > 0;
	}


	public Cursor getAll() {
		return db.query(TABLE, new String[] {_ID, CONTACT_ID},
				null, 
				null, 
				null, 
				null, 
				null);
	}

	public Cursor getContact(long rowId) throws SQLException {
		Cursor mCursor =
			db.query(true, TABLE, new String[] {_ID, CONTACT_ID}, 
					_ID + "=" + rowId, 
					null,
					null, 
					null, 
					null, 
					null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
}
