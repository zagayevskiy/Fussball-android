package com.zagayevskiy.fussball.api.request;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.util.Log;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class AuthRequest extends ApiBaseRequest{
	
	private final String mEmail, mPassword;
	
	public AuthRequest(ResultListener listener, String email, String password) {
		super(listener);
		mEmail = email;
		mPassword = password;
	}

	@Override
	public void run() {
		HttpGet get = new HttpGet(C.api.url.LOGIN);
		final Header authHeader = BasicScheme.authenticate(new UsernamePasswordCredentials(mEmail, mPassword), HTTP.UTF_8, false);
		get.addHeader(authHeader);
		
		try{
			String result = HttpHelper.syncHttpRequest(get);
			
			Log.e("auth", "result:" + result);
			
			JSONObject json = new JSONObject(result);
			if(!json.has(C.api.json.key.ACCESS_TOKEN)){
				//TODO: get more info and notify more codes!				
				notifyApiResult(FAIL);
				return;
			}
			
			final String token = json.getString(C.api.json.key.ACCESS_TOKEN);
			
			Player player = new Player(json.getJSONObject("player"));
			player.makeOwner();
			
			ContentResolver resolver = getApiService().getContentResolver();
			resolver.insert(Player.URI, player.getDBContentValues());
			

			Token.getInstance().saveToken(getApiService(), token);
			
			notifyApiResult(SUCCESS);
			
		}catch (IOException e){
			notifyApiResult(FAIL_NETWORK);
		}catch (JSONException e) {
			notifyApiResult(FAIL);
		}
	}

}
