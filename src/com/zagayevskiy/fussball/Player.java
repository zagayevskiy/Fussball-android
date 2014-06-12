package com.zagayevskiy.fussball;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.zagayevskiy.fussball.utils.C;

public class Player {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_ID = "_id";
	public static final String FIELD_EMAIL = "email";	
	public static final String FIELD_RATING = "rating";
	
	public static final String CONTENT_TYPE = C.db.BASE_CONTENT_TYPE + ".user";

	public static final String CONTENT_ITEM_TYPE = C.db.BASE_CONTENT_TYPE + ".task";
	public static final Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME + "/");
	public static final Uri URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME);
	
	public static final long INVALID_ID = -1L;
	
	public static final String WHERE_ID = FIELD_ID + "=?";
	public static final String WHERE_ID_NOT_IN_FMT = FIELD_ID + " NOT IN ( %s )";
	
	public static final String WHERE_EMAIL = FIELD_EMAIL + "=?";
	
	public static final String[] FULL_PROJECTION = new String[]{
		FIELD_ID,
		FIELD_EMAIL,
		FIELD_RATING
	};
	
	public static final String[] SEARCH_PROJECTION = new String[]{
		FIELD_ID,
		FIELD_EMAIL
	};
	
	private static final String[] SELECTION_ONE_ELEMENT = new String[]{ String.valueOf(INVALID_ID) };
	
	private long mId;
	private String mEmail;
	private double mRating;
	
	public Player(JSONObject json) throws JSONException{
		mId = INVALID_ID;
		mEmail = json.getString(FIELD_EMAIL);
		mRating = json.getDouble(FIELD_RATING);
	}
	
	public Player(Cursor c){
		mId = c.getLong(c.getColumnIndex(FIELD_ID));
		mEmail = c.getString(c.getColumnIndex(FIELD_EMAIL));
		mRating = c.getDouble(c.getColumnIndex(FIELD_RATING));
	}
	
	public long getId(){
		return mId;
	}
	
	public String getEmail(){
		return mEmail;
	}
	
	public ContentValues getDBContentValues(){
		ContentValues result = new ContentValues();
		if(mId != INVALID_ID){
			result.put(FIELD_ID, mId);
		}
		
		result.put(FIELD_EMAIL, mEmail);
		result.put(FIELD_RATING, mRating);
		
		return result;
	}
	
	public static final ContentValues[] jsonToDBContentValues(String jsonStr) throws JSONException{
		JSONArray array = new JSONArray(jsonStr);
		final int count = array.length();
		ContentValues[] result = new ContentValues[count];
		
		for(int i = 0; i < count; ++i){
			JSONObject item = array.getJSONObject(i);
			
			final String email = item.getString(FIELD_EMAIL);
			ContentValues values = new ContentValues();
			values.put(FIELD_EMAIL, email);
			values.put(FIELD_RATING, item.getDouble(FIELD_RATING));
			
			result[i] = values;
		}
		
		return result;
	}
	
	public static final void setOwnerEmail(Context context, String email){
		context.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE)
			.edit()
				.putString(C.prefs.key.OWNER_EMAIL, email)
			.commit();
	}
	
	public static final Player getOwner(Activity activity){
		final String ownerEmail = activity.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE).getString(C.prefs.key.OWNER_EMAIL, "illegal");
		
		final Player owner = getSingle(activity, WHERE_EMAIL, ownerEmail, null);
		if(owner == null){
			throw new IllegalStateException("Owner not found");
		}
		
		return owner;
	}
	
	public static final Player getSingle(Activity activity, String where, String whereArg, String order){
		final ContentResolver resolver =  activity.getContentResolver();
		
		SELECTION_ONE_ELEMENT[0] = whereArg;
		final Cursor c = resolver.query(URI, FULL_PROJECTION, where, SELECTION_ONE_ELEMENT, order);
		if(!c.moveToFirst()){
			return null;
		}
		
		final Player player = new Player(c);
		
		c.close();
		
		return player;
	}
}
