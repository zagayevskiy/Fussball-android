package com.zagayevskiy.fussball;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.zagayevskiy.fussball.utils.C;

public class Game {
	public static final String TABLE_NAME = "games";
	
	public static final String FIELD_ID = "_id"; 
	public static final String FIELD_PLAYER1_NICK = "player1nick";
	public static final String FIELD_PLAYER2_NICK = "player2nick";
	public static final String FIELD_SCORE1 = "score1";
	public static final String FIELD_SCORE2 = "score2";
	public static final String FIELD_PLAYER1_RATING_DELTA = "player1delta";
	public static final String FIELD_PLAYER2_RATING_DELTA = "player2delta";
	public static final String FIELD_TIMESTAMP = "timestamp";
	
	public static final String CONTENT_TYPE = C.db.BASE_CONTENT_TYPE + ".game";
	public static final String CONTENT_ITEM_TYPE = C.db.BASE_CONTENT_TYPE + ".game";
	
	public static final Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME + "/");
	public static final Uri URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME);
	
	public static final long INVALID_ID = -1L;
	
	public static final String ORDER_DATE_DESC = FIELD_ID + " DESC";
	
	public static final String[] FULL_PROJECTION = {
		FIELD_ID,
		FIELD_PLAYER1_NICK,
		FIELD_PLAYER2_NICK,
		FIELD_SCORE1,
		FIELD_SCORE2,
		FIELD_PLAYER1_RATING_DELTA,
		FIELD_PLAYER2_RATING_DELTA
	};
	
	private long id = INVALID_ID;
	private String mPlayer1Nick;
	private String mPlayer2Nick;
	private int mScore1;
	private int mScore2;
	private int mRatingDelta1;
	private int mRatingDelta2;
	
	public Game(String nick1, String nick2, int score1, int score2){
		mPlayer1Nick = nick1;
		mPlayer2Nick = nick2;
		mScore1 = score1;
		mScore2 = score2;
	}
	
	public Game(JSONObject json) throws JSONException{
		final JSONObject side1 = json.getJSONObject("side1");
		final JSONObject side2 = json.getJSONObject("side2");
		final JSONObject player1 = side1.getJSONObject("player");
		final JSONObject player2 = side2.getJSONObject("player");
		mPlayer1Nick = player1.getString("nick");
		mPlayer2Nick = player2.getString("nick");
		mRatingDelta1 = player1.getInt("ratingDelta");
		mRatingDelta2 = player2.getInt("ratingDelta");
		mScore1 = side1.getInt("score");
		mScore2 = side2.getInt("score");
	}
	
	public ContentValues getDBContentValues(){
		ContentValues values = new ContentValues();
		
		if(id != INVALID_ID){
			values.put(FIELD_ID, id);
		}
		
		values.put(FIELD_PLAYER1_NICK, mPlayer1Nick);
		values.put(FIELD_PLAYER2_NICK, mPlayer2Nick);
		values.put(FIELD_PLAYER1_RATING_DELTA, mRatingDelta1);
		values.put(FIELD_PLAYER2_RATING_DELTA, mRatingDelta2);
		values.put(FIELD_SCORE1, mScore1);
		values.put(FIELD_SCORE2, mScore2);
		
		return values;
	}
	
	public static List<ContentValues> fromJson(JSONArray array) throws JSONException{
		ArrayList<ContentValues> result = new ArrayList<ContentValues>(array.length());
		
		for(int i = 0; i < array.length(); ++i){
			final JSONObject item = array.getJSONObject(i);
			final JSONObject side1 = item.getJSONObject("side1");
			final JSONObject side2 = item.getJSONObject("side2");
			final JSONObject player1 = side1.getJSONObject("player");
			final JSONObject player2 = side2.getJSONObject("player");
			
			final ContentValues values = new ContentValues();
			
			values.put(FIELD_PLAYER1_NICK, player1.getString("nick"));
			values.put(FIELD_PLAYER2_NICK, player2.getString("nick"));
			values.put(FIELD_PLAYER1_RATING_DELTA, player1.getInt("ratingDelta"));
			values.put(FIELD_PLAYER2_RATING_DELTA, player2.getInt("ratingDelta"));
			values.put(FIELD_SCORE1, side1.getInt("score"));
			values.put(FIELD_SCORE2, side2.getInt("score"));
			
			result.add(values);
		}
		
		return result;
	}
	
	public JSONObject toJson() throws JSONException{
		JSONObject json = new JSONObject();
		JSONObject side;
		JSONObject player;
		
		//Side #1
		side = new JSONObject();
		player = new JSONObject();
		player.put("nick", mPlayer1Nick);
		side.put("player", player);
		side.put("score", mScore1);
		json.put("side1", side);
		
		//Side #2
		side = new JSONObject();
		player = new JSONObject();
		player.put("nick", mPlayer2Nick);
		side.put("player", player);
		side.put("score", mScore2);
		json.put("side2", side);
		
		return json;
	}	
}
