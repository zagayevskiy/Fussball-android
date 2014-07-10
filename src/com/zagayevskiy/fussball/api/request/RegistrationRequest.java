package com.zagayevskiy.fussball.api.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.util.Log;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class RegistrationRequest extends ApiBaseRequest {
	
	private static final String TAG = RegistrationRequest.class.getName();
	
	private final String mNick, mEmail, mPassword;
	
	public RegistrationRequest(ResultListener listener, String nick, String email, String password){
		super(listener);
		mNick = nick;
		mEmail = email;
		mPassword = password;
	}
	
	@Override
	public void run() {
		try{
			
			HttpPost registration = new HttpPost(C.api.url.REGISTRATION);
			JSONObject json = new JSONObject();
			json.put("nick", mNick);
			json.put("email", mEmail);
			json.put("password", mPassword);
			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			Log.i(TAG, json.toString(4));
			entity.setContentType("application/json");
			registration.setEntity(entity);
			
			String result = HttpHelper.syncHttpRequest(registration);
			json = new JSONObject(result);
			Log.i(TAG, result);
			
			JSONArray errors = json.getJSONArray(C.api.json.key.ERRORS);
			if(errors.length() > 0){
				notifyApiResult(FAIL);
				return;
			}

			final Player player = new Player(json.getJSONObject("player"));
			player.makeOwner();
			
			final String token = json.getString(C.api.json.key.ACCESS_TOKEN);
			
			ContentResolver resolver = getApiService().getContentResolver();
			resolver.insert(Player.URI, player.getDBContentValues());
			
			Token.getInstance().saveToken(getApiService(), token);
			
			notifyApiResult(SUCCESS);

		}catch(JSONException e){
			notifyApiResult(FAIL);
		} catch (UnsupportedEncodingException e) {
			notifyApiResult(FAIL);
		} catch (IOException e) {
			notifyApiResult(FAIL_NETWORK);
		}
	}
	
}
