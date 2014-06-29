package com.zagayevskiy.fussball;

import org.json.JSONException;
import org.json.JSONObject;

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
		FIELD_SCORE2
	};
	
	private long id;
	private String mPlayer1Nick;
	private String mPlayer2Nick;
	private int mScore1;
	private int mScore2;
	
	public Game(String nick1, String nick2, int score1, int score2){
		mPlayer1Nick = nick1;
		mPlayer2Nick = nick2;
		mScore1 = score1;
		mScore2 = score2;
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		JSONObject side = new JSONObject();
		JSONObject player = new JSONObject();
		
		player.put("nick", mPlayer1Nick);
		side.put("player", player);
		side.put("score", mScore1);
		json.put("side1", side);
		
		player.put("nick", mPlayer2Nick);
		side.put("player", player);
		side.put("score", mScore2);
		json.put("side2", side);
		
		return json;
	}
	
}
