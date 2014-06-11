package com.zagayevskiy.fussball;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;

import com.zagayevskiy.fussball.utils.C;

public class User {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_ID = "_id";
	public static final String FIELD_EMAIL = "email";	
	public static final String FIELD_RATING = "rating";
	
	public static final String CONTENT_TYPE = C.db.BASE_CONTENT_TYPE + ".user";

	public static final String CONTENT_ITEM_TYPE = C.db.BASE_CONTENT_TYPE + ".task";
	public static final Uri URI_BASE = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME + "/");
	public final static Uri URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + C.db.AUTHORITY + "/" + TABLE_NAME);
	
	public static final long INVALID_ID = -1L;
	
	public static final String[] FULL_PROJECTION = new String[]{
		FIELD_ID,
		FIELD_EMAIL,
		FIELD_RATING
	};
	
	private long mId;
	private String mEmail;
	private double mRating;
	
	public User(JSONObject json) throws JSONException{
		mId = INVALID_ID;
		mEmail = json.getString(FIELD_EMAIL);
		mRating = json.getDouble(FIELD_RATING);
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
	
	public static ContentValues[] jsonToDBContentValues(String jsonStr) throws JSONException{
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
}
