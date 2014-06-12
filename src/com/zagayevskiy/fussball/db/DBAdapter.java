package com.zagayevskiy.fussball.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.utils.C;

public class DBAdapter {

	private final Context context;
	private DatabaseHelper databaseHelper;
	
	public DBAdapter(Context aContext) {
		context = aContext;
		databaseHelper = new DatabaseHelper(context);
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {
		
		private static final int DATABASE_VERSION = 1;
		
		private static final String TABLE_USERS_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Player.TABLE_NAME
			+ " ("
				+ Player.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Player.FIELD_EMAIL + " TEXT UNIQUE NOT NULL,"
				+ Player.FIELD_RATING + " DOUBLE NOT NULL"
			+ ");";
		
		
		DatabaseHelper(Context context) {
			super(context, C.db.AUTHORITY, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_USERS_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	private boolean opened = false;
	/**
	 * Open database
	 * 
	 * @return database adapter
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		databaseHelper.getWritableDatabase();
		opened = true;
		return this;
	}
	
	public SQLiteDatabase openDatabase() throws SQLException{
		SQLiteDatabase result = databaseHelper.getWritableDatabase();
		opened = true;
		return result;
	}

	/**
	 * Close database
	 */
	public void close() {
		databaseHelper.close();
		opened = false;
	}
	
	public boolean isOpen() {
		return opened;
	}
}
