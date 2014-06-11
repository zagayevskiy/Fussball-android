/**
 * Copyright (C) 2010-2012 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  If you own a pjsip commercial license you can also redistribute it
 *  and/or modify it under the terms of the GNU Lesser General Public License
 *  as an android library.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.zagayevskiy.fussball.db;

import com.zagayevskiy.fussball.User;
import com.zagayevskiy.fussball.utils.C;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CallLog;

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
			+ User.TABLE_NAME
			+ " ("
				+ User.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ User.FIELD_EMAIL + " TEXT UNIQUE NOT NULL,"
				+ User.FIELD_RATING + " DOUBLE NOT NULL"
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
	
	private static void addColumn(SQLiteDatabase db, String table, String field, String type) {
	    db.execSQL("ALTER TABLE " + table + " ADD "+ field + " " + type);
	}
	

}
