package com.zagayevskiy.fussball;

import android.content.ContentResolver;
import android.net.Uri;

import com.zagayevskiy.fussball.utils.C;

public class Game {
	public static final String TABLE_NAME = "games";
	
	public static final String FIELD_ID = "_id"; 
	public static final String FIELD_PLAYER1_EMAIL = "player1email";
	public static final String FIELD_PLAYER2_EMAIL = "player2email";
	public static final String FIELD_SCORE1 = "score1";
	public static final String FIELD_SCORE2 = "score2";
	public static final String FIELD_TIMESTAMP = "timestamp";
	
	public static final String CONTENT_TYPE = C.db.BASE_CONTENT_TYPE + ".game";
	public static final String CONTENT_ITEM_TYPE = C.db.BASE_CONTENT_TYPE + ".game";
	
	public static final Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME + "/");
	public static final Uri URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME);
	
	public static final long INVALID_ID = -1L;
	
	public static final String[] FULL_PROJECTION = {
		FIELD_ID,
		FIELD_PLAYER1_EMAIL,
		FIELD_PLAYER2_EMAIL,
		FIELD_SCORE1,
		FIELD_SCORE2,
		"p1." + Player.FIELD_EMAIL, 
		"p2." + Player.FIELD_EMAIL 
	};
	
	private long id;
	private String player1Email;
	private String player2Email;
	
}
