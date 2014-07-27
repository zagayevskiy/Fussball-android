package com.zagayevskiy.fussball;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.zagayevskiy.fussball.utils.C;

public class Player {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_ID = "_id";
	public static final String FIELD_EMAIL_HASH = "hashedEmail";	
	public static final String FIELD_NICK = "nick";	
	public static final String FIELD_RATING = "rating";
	public static final String FIELD_IS_OWNER = "isowner";
	public static final String FIELD_TOTAL_PLAYED = "totalPlayed";
	public static final String FIELD_TOTAL_WON = "totalWon";
	
	public static final String CONTENT_TYPE = C.db.BASE_CONTENT_TYPE + ".user";

	public static final String CONTENT_ITEM_TYPE = C.db.BASE_CONTENT_TYPE + ".task";
	public static final Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME + "/");
	public static final Uri URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME);
	
	public static final long INVALID_ID = -1L;
	public static final int OWNER = 1;
	public static final int NOT_OWNER = 0;
	
	public static final String WHERE_ID = FIELD_ID + "=?";
	public static final String WHERE_ID_NOT_IN_FMT = FIELD_ID + " NOT IN ( %s )";
	
	public static final String WHERE_NICK = FIELD_NICK + "=?";
	public static final String WHERE_IS_OWNER = FIELD_IS_OWNER + "=?";
	
	public static final String ORDER_RATING_DESC = FIELD_RATING + " DESC";
	public static final String ORDER_NICK_ASC = "LOWER(" + FIELD_NICK + ") ASC";
	
	public static final String[] FULL_PROJECTION = new String[]{
		FIELD_ID,
		FIELD_EMAIL_HASH,
		FIELD_NICK,
		FIELD_RATING,
		FIELD_IS_OWNER,
		FIELD_TOTAL_PLAYED,
		FIELD_TOTAL_WON
	};
	
	public static final String[] SEARCH_PROJECTION = new String[]{
		FIELD_ID,
		FIELD_EMAIL_HASH, 
		FIELD_NICK
	};
	
	private static final String[] SELECTION_ONE_ELEMENT = new String[]{ String.valueOf(INVALID_ID) };
	
	private long mId;
	private String mEmailHash;
	private String mNick;
	private double mRating;
	private boolean mIsOwner; 
	private int mTotalPlayed;
	private int mTotalWon;
	
	public Player(JSONObject json) throws JSONException{
		mId = INVALID_ID;
		mEmailHash = json.getString(FIELD_EMAIL_HASH);
		mNick = json.getString(FIELD_NICK);
		mRating = json.getDouble(FIELD_RATING);
		mIsOwner = false;
		mTotalPlayed = json.getInt(FIELD_TOTAL_PLAYED);
		mTotalWon = json.getInt(FIELD_TOTAL_WON);
		
	}
	
	public Player(Cursor c){
		mId = c.getLong(c.getColumnIndex(FIELD_ID));
		mEmailHash = c.getString(c.getColumnIndex(FIELD_EMAIL_HASH));
		mNick = c.getString(c.getColumnIndex(FIELD_NICK));
		mRating = c.getDouble(c.getColumnIndex(FIELD_RATING));
		mIsOwner = c.getInt(c.getColumnIndex(FIELD_IS_OWNER)) == OWNER;
		mTotalPlayed = c.getInt(c.getColumnIndex(FIELD_TOTAL_PLAYED));
		mTotalWon = c.getInt(c.getColumnIndex(FIELD_TOTAL_WON));
	}
	
	public long getId(){
		return mId;
	}
	
	public String getEmailHash(){
		return mEmailHash;
	}
	
	public String getNick(){
		return mNick;
	}
	
	public int getRating(){
		return (int) mRating;
	}
	
	public int getTotalPlayed(){
		return mTotalPlayed;
	}
	
	public int getTotalWon(){
		return mTotalWon;
	}
	
	public void makeOwner(){
		mIsOwner = true;
	}
	
	public boolean isOwner(){
		return mIsOwner;
	}
	
	public ContentValues getDBContentValues(){
		ContentValues result = new ContentValues();
		if(mId != INVALID_ID){
			result.put(FIELD_ID, mId);
		}
		
		result.put(FIELD_EMAIL_HASH, mEmailHash);
		result.put(FIELD_NICK, mNick);
		result.put(FIELD_RATING, mRating);
		result.put(FIELD_IS_OWNER, mIsOwner ? OWNER : NOT_OWNER);
		result.put(FIELD_TOTAL_PLAYED, mTotalPlayed);
		result.put(FIELD_TOTAL_WON, mTotalWon);
		
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		return (o == null) ? false : (o instanceof Player ? mNick.equals(((Player)o).getNick()) : false); 
	}
	
	public static final ContentValues[] jsonToDBContentValues(JSONArray array) throws JSONException{
		final int count = array.length();
		ContentValues[] result = new ContentValues[count];
		
		for(int i = 0; i < count; ++i){
			JSONObject item = array.getJSONObject(i);
			
			ContentValues values = new ContentValues();
			
			values.put(FIELD_EMAIL_HASH, item.getString(FIELD_EMAIL_HASH));
			values.put(FIELD_NICK, item.getString(FIELD_NICK));
			values.put(FIELD_RATING, item.getDouble(FIELD_RATING));
			values.put(FIELD_IS_OWNER, NOT_OWNER);
			values.put(FIELD_TOTAL_PLAYED, item.getInt(FIELD_TOTAL_PLAYED));
			values.put(FIELD_TOTAL_WON, item.getInt(FIELD_TOTAL_WON));
			
			result[i] = values;
		}
		
		return result;
	}
	
	public static final Player getOwner(Context context){
		return getSingle(context, WHERE_IS_OWNER, String.valueOf(OWNER), null);
	}
	
	public static final Player getSingle(Context context, String where, String whereArg, String order){
		final ContentResolver resolver =  context.getContentResolver();
		
		SELECTION_ONE_ELEMENT[0] = whereArg;
		final Cursor c = resolver.query(URI, FULL_PROJECTION, where, SELECTION_ONE_ELEMENT, order);
		if(!c.moveToFirst()){
			return null;
		}
		
		final Player player = new Player(c);
		
		c.close();
		
		return player;
	}
	
	public static final ArrayList<Player> fromCursor(Cursor c){
		ArrayList<Player> result = new ArrayList<Player>(c.getCount());
		
		while(c.moveToNext()){
			result.add(new Player(c));
		}
		
		return result;
	}
}
