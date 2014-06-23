package com.zagayevskiy.fussball.api.request;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.util.Log;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class Registration extends ApiRequest {
	
	private static final String TAG = Registration.class.getName();
	
	private final String mNickname, mEmail, mPassword;
	
	public Registration(ResultListener listener, String nickname, String email, String password){
		super(listener);
		mNickname = nickname;
		mEmail = email;
		mPassword = password;
	}
	
	@Override
	public void run() {
		try{
			
			HttpPost registration = new HttpPost(C.api.url.REGISTRATION);
			JSONObject json = new JSONObject();
			json.put("nickName", mNickname);
			json.put("email", mEmail);
			json.put("password", mPassword);
			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			Log.i(TAG, json.toString(4));
			entity.setContentType("application/json");
			registration.setEntity(entity);
			
			String result = HttpHelper.syncHttpRequest(registration);
			json = new JSONObject(result);
			Log.i(TAG, result);
			if(json.getBoolean("error")){
				notifyApiResult(FAIL);
				return;
			}

			Player player = new Player(json.getJSONObject("player"));
			player.makeOwner();
			ContentResolver resolver = getApiService().getContentResolver();
			resolver.insert(Player.URI, player.getDBContentValues());
			
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
