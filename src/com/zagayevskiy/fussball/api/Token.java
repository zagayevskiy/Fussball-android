package com.zagayevskiy.fussball.api;

import com.zagayevskiy.fussball.utils.C;

import android.content.Context;

public class Token {
	
	protected Token(){};
	
	private static Token mInstance = new Token();
	private static final String TOKEN_KEY = Token.class.getName() + "_token_key";
	
	private String mToken;
	
	public static Token getInstance(){
		return mInstance;
	}
	
	public void saveToken(Context context, String token){
		mToken = token;
		context.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE)
			.edit()
			.putString(TOKEN_KEY, mToken)
			.commit();
	}
	
	public boolean hasToken(Context context){
		final String token = getToken(context); 
		return !token.isEmpty();
	}
	
	public String getToken(Context context){
		if(mToken == null){
			mToken = context.getSharedPreferences(C.prefs.NAME, Context.MODE_PRIVATE).getString(TOKEN_KEY, "");
		}
		return mToken;
	}
	
	public String tokenizeUrl(Context context, String url){
		final String token = getToken(context);
		if(token.isEmpty()){
			return url;
		}
		
		if(url.contains("?")){
			return url + "&token=" + token;
		}
		
		return url + "?token=" + token;
	}
	
}

