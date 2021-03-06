package com.zagayevskiy.fussball.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.v4.database.DatabaseUtilsCompat;

import com.zagayevskiy.fussball.Game;
import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.db.DBAdapter.DatabaseHelper;
import com.zagayevskiy.fussball.utils.C;

public class DBProvider extends ContentProvider {

	private DatabaseHelper mOpenHelper;
	private static final String UNKNOWN_URI_LOG = "Unknown URI ";

	// Ids for matcher
	private static final int PLAYERS = 1, PLAYERS_ID = 2;
	private static final int GAMES = 3, GAMES_ID = 4;

	/**
	 * A UriMatcher instance
	 */
	private static final UriMatcher URI_MATCHER;
	static {
		// Create and initialize URI matcher.
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

		URI_MATCHER.addURI(C.db.AUTHORITY, Player.TABLE_NAME, PLAYERS);
		URI_MATCHER.addURI(C.db.AUTHORITY, Player.TABLE_NAME + "/#", PLAYERS_ID);
		URI_MATCHER.addURI(C.db.AUTHORITY,  Game.TABLE_NAME, GAMES);
		URI_MATCHER.addURI(C.db.AUTHORITY, Game.TABLE_NAME + "/#", GAMES_ID);
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
			case PLAYERS:
				return Player.CONTENT_TYPE;
			case PLAYERS_ID:
				return Player.CONTENT_ITEM_TYPE;
			case GAMES:
				return Game.CONTENT_TYPE;
			case GAMES_ID:
				return Game.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
		}
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		String finalWhere;
		int count = 0;
		final int matched = URI_MATCHER.match(uri);
		Uri regUri = uri;

		switch (matched) {

			case PLAYERS:
				count = db.delete(Player.TABLE_NAME, where, whereArgs);
			break;
	
			case PLAYERS_ID:
				finalWhere = DatabaseUtilsCompat.concatenateWhere(Player.FIELD_ID + " = " + ContentUris.parseId(uri), where);
				count = db.delete(Player.TABLE_NAME, finalWhere, whereArgs);
			break;
	
			case GAMES:
				count = db.delete(Game.TABLE_NAME, where, whereArgs);
			break;
	
			case GAMES_ID:
				finalWhere = DatabaseUtilsCompat.concatenateWhere(Game.FIELD_ID + " = " + ContentUris.parseId(uri), where);
				count = db.delete(Game.TABLE_NAME, finalWhere, whereArgs);
			break;
			
			default:
				throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
		}

		getContext().getContentResolver().notifyChange(regUri, null);

		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {

		int matched = URI_MATCHER.match(uri);
		String matchedTable = null;
		Uri baseInsertedUri = null;
		switch (matched) {

			case PLAYERS:
			case PLAYERS_ID:
				matchedTable = Player.TABLE_NAME;
				baseInsertedUri = Player.URI_BASE;
			break;
	
			case GAMES:
			case GAMES_ID:
				matchedTable = Game.TABLE_NAME;
				baseInsertedUri = Game.URI_BASE;
			break;
			
			default:
				break;
		}

		if (matchedTable == null) {
			throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
		}

		ContentValues values;

		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		long rowId = db.insert(matchedTable, null, values);

		// If the insert succeeded, the row ID exists.
		if (rowId >= 0) {

			Uri retUri = ContentUris.withAppendedId(baseInsertedUri, rowId);
			getContext().getContentResolver().notifyChange(retUri, null);

			return retUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String finalSortOrder = sortOrder;
		String[] finalSelectionArgs = selectionArgs;
		String finalGrouping = null;
		String finalHaving = null;
		int type = URI_MATCHER.match(uri);

		Uri regUri = uri;

		Cursor c;

		switch (type) {

			case PLAYERS:
				qb.setTables(Player.TABLE_NAME);
			break;
	
			case PLAYERS_ID:
				qb.setTables(Player.TABLE_NAME);
				qb.appendWhere(Player.FIELD_ID + "=?");
				finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[] { uri.getLastPathSegment() });
			break;
	
			case GAMES:
				qb.setTables(Game.TABLE_NAME);
			break;
	
			case GAMES_ID:
				qb.setTables(Game.TABLE_NAME);
				qb.appendWhere(Game.FIELD_ID + "=?");
				finalSelectionArgs = DatabaseUtilsCompat.appendSelectionArgs(selectionArgs, new String[] { uri.getLastPathSegment() });
			
			default:
				throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		c = qb.query(db, projection, selection, finalSelectionArgs,
				finalGrouping, finalHaving, finalSortOrder);

		c.setNotificationUri(getContext().getContentResolver(), regUri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalWhere;
		int matched = URI_MATCHER.match(uri);

		switch (matched) {
			case PLAYERS:
				count = db.update(Player.TABLE_NAME, values, where, whereArgs);
			break;
	
			case PLAYERS_ID:
				finalWhere = DatabaseUtilsCompat.concatenateWhere(Player.FIELD_ID + " = " + ContentUris.parseId(uri), where);
				count = db.update(Player.TABLE_NAME, values, finalWhere, whereArgs);
			break;
			
			case GAMES:
				count = db.update(Game.TABLE_NAME, values, where, whereArgs);
			break;
	
			case GAMES_ID:
				finalWhere = DatabaseUtilsCompat.concatenateWhere(Game.FIELD_ID + " = " + ContentUris.parseId(uri), where);
				count = db.update(Game.TABLE_NAME, values, finalWhere, whereArgs);
			break;

			default:
				throw new IllegalArgumentException(UNKNOWN_URI_LOG + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}
}
