package com.zagayevskiy.fussball.api.request;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;

import android.util.Log;

import com.zagayevskiy.fussball.Game;
import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;

public class NewGameRequest extends ApiBaseRequest {

	private static final String TAG = NewGameRequest.class.getName();
	
	private Game mGame;
	
	public NewGameRequest(ResultListener listener, Game game){
		super(listener);
		
		mGame = game;
	}
	
	@Override
	public void run() {
		final HttpPost post = new HttpPost(Token.getInstance().tokenizeUrl(getApiService(), C.api.url.NEW_GAME));
		try {
			
			final StringEntity entity = new StringEntity(mGame.toJson().toString(), "UTF-8");
			Log.e(TAG, mGame.toJson().toString(4));
			entity.setContentType("application/json");
			post.setEntity(entity);
			//TODO: post it
//			final String result = HttpHelper.syncHttpRequest(post);
			
//			Log.i(TAG, "Game result: " + result);
					
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