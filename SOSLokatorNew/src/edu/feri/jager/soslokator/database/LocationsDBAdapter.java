package edu.feri.jager.soslokator.database;

import edu.feri.jager.soslokator.structure.MyLocation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class LocationsDBAdapter implements BaseColumns {
	public static final  String PHONE_NUMBER = "phoneNumber";
	public static final  String DISPLAY_NAME = "sender";
	public static final  String LONGITUDE = "longitude";
	public static final  String LATITUDE = "latitude";
	public static final  String TIMESTAMP = "timestamp";
	
	public static final  int POS__ID = 0;
	public static final  int POS_PHONE_NUMBER = 1;
	public static final  int POS_DISPLAY_NAME = 2;
	public static final  int POS_LONGITUDE = 3;
	public static final  int POS_LATITUDE = 4;
	public static final  int POS_TIMESTAMP = 5;
	
	public static final  String TABLE = "locations";

	private final Context context;
	private LocationsDBHelper DBHelper;
	private SQLiteDatabase db;

	public LocationsDBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new LocationsDBHelper(context);
	}

	public LocationsDBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		DBHelper.close();
	}

	public long insertContact(MyLocation structure) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(PHONE_NUMBER, structure.getPhoneNumber()); 
		initialValues.put(DISPLAY_NAME, structure.getDisplayName()); 
		initialValues.put(LONGITUDE, structure.getLongitude());
		initialValues.put(LATITUDE, structure.getLatitude());
		initialValues.put(TIMESTAMP, structure.getTimestamp());
		return db.insert(TABLE, null, initialValues);
	}


	public boolean deleteContact(long rowId) {
		return db.delete(TABLE, _ID + "=" + rowId, null) > 0;
	}


	public Cursor getAll() {
		return db.query(TABLE, new String[] {_ID, PHONE_NUMBER, DISPLAY_NAME, LONGITUDE, LATITUDE, TIMESTAMP},
				null, 
				null, 
				null, 
				null, 
				null);
	}

	public Cursor getContact(long rowId) throws SQLException {
		Cursor mCursor =
			db.query(true, TABLE, new String[] {_ID, PHONE_NUMBER, DISPLAY_NAME, LONGITUDE, LATITUDE, TIMESTAMP}, 
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
