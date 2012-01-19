package edu.feri.jager.soslokator.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class ContactsDBHelper extends SQLiteOpenHelper {	
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "db_contactsID";
	private static final String DATABASE_CREATE =
		"create table " + ContactsDBAdapter.TABLE + " (" + ContactsDBAdapter._ID + " integer primary key autoincrement, "
		+ ContactsDBAdapter.CONTACT_ID + " TEXT not null);";

	ContactsDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + ContactsDBAdapter.TABLE);
		onCreate(db);
	}
}
