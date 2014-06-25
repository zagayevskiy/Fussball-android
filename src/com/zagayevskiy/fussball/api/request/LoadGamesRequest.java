package com.zagayevskiy.fussball.api.request;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class LoadGamesRequest extends ApiBaseRequest {

	public static final String TAG = LoadGamesRequest.class.getName();
	
	public LoadGamesRequest(ResultListener listener){
		super(listener);
	}
	
	@Override
	public void run() {
		final HttpGet get = new HttpGet(Token.getInstance().tokenizeUrl(getApiService(), C.api.url.GAMES));
		
		try{
			final String result = HttpHelper.syncHttpRequest(get);
			Log.i(TAG, result);
			
			final JSONObject json = new JSONObject(result);
			
			notifyApiResult(SUCCESS);
		}catch (IOException e) {
			notifyApiResult(FAIL_NETWORK);
			Log.e(TAG, "Fail", e);
		} catch (JSONException e) {
			notifyApiResult(FAIL);
			Log.e(TAG, "Fail", e);
		}
		
	}

}
