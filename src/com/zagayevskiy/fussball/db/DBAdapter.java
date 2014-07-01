package com.zagayevskiy.fussball.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zagayevskiy.fussball.Game;
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
		
		private static final String TABLE_PLAYERS_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Player.TABLE_NAME
			+ " ("
				+ Player.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Player.FIELD_EMAIL + " TEXT UNIQUE NOT NULL,"
				+ Player.FIELD_NICK + " TEXT UNIQUE NOT NULL,"
				+ Player.FIELD_RATING + " DOUBLE NOT NULL,"
				+ Player.FIELD_IS_OWNER + " INTEGER"
			+ ");";
		
		private static final String TABLE_GAMES_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ Game.TABLE_NAME
			+ " ("
				+ Game.FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Game.FIELD_PLAYER1_NICK + " TEXT NOT NULL,"
				+ Game.FIELD_PLAYER2_NICK + " TEXT NOT NULL,"
				+ Game.FIELD_SCORE1 + " INTEGER NOT NULL,"
				+ Game.FIELD_SCORE2 + " INTEGER NOT NULL,"
				+ Game.FIELD_PLAYER1_RATING_DELTA + " INTEGER,"
				+ Game.FIELD_PLAYER2_RATING_DELTA + " INTEGER,"
				+ Game.FIELD_TIMESTAMP + " INTEGER"
			+ ");";
		
		DatabaseHelper(Context context) {
			super(context, C.db.AUTHORITY, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TABLE_PLAYERS_CREATE);
			db.execSQL(TABLE_GAMES_CREATE);
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
