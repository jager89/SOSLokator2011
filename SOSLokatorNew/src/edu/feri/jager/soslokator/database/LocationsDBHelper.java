package edu.feri.jager.soslokator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class LocationsDBHelper extends SQLiteOpenHelper {	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "db_locations";
	private static final String DATABASE_CREATE =
		"create table " + LocationsDBAdapter.TABLE + " (" + LocationsDBAdapter._ID + " integer primary key autoincrement, "
		+ LocationsDBAdapter.PHONE_NUMBER + " TEXT not null, "
		+ LocationsDBAdapter.DISPLAY_NAME + " TEXT, "
		+ LocationsDBAdapter.LONGITUDE + " TEXT not null, "
		+ LocationsDBAdapter.LATITUDE + " TEXT not null, "
		+ LocationsDBAdapter.TIMESTAMP + " TEXT not null);";

	LocationsDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + LocationsDBAdapter.TABLE);
		onCreate(db);
	}
}
