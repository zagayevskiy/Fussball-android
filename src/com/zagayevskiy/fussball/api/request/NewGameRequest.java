package com.zagayevskiy.fussball.api.request;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.zagayevskiy.fussball.Game;
import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class NewGameRequest extends ApiBaseRequest {

	private static final String TAG = NewGameRequest.class.getName();
	
	private Game mGame;
	
	public NewGameRequest(ResultListener listener, Game game){
		super(listener);
		
		mGame = game;
	}
	
	@Override
	public void run() {
		String url;
		final HttpPost post = new HttpPost(url = Token.getInstance().tokenizeUrl(getApiService(), C.api.url.NEW_GAME));
		Log.e(TAG, url);
		try {
			
			final StringEntity entity = new StringEntity(mGame.toJson().toString(), "UTF-8");
			Log.e(TAG, mGame.toJson().toString());
			entity.setContentType("application/json");
			post.setEntity(entity);
			//TODO: post it
			final String result = HttpHelper.syncHttpRequest(post);
			Log.e(TAG, "after");
			
			Log.i(TAG, "Game result: " + result);
					
			notifyApiResult(SUCCESS);
		} catch (IOException e) {
			Log.e(TAG, "Fail", e);
			notifyApiResult(FAIL_NETWORK);
		} catch (JSONException e) {
			Log.e(TAG, "Fail", e);
			notifyApiResult(FAIL);
		}
	}

}
