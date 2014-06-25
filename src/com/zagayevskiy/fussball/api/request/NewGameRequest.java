package com.zagayevskiy.fussball.api.request;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;

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
		final HttpPost post = new HttpPost(Token.getInstance().tokenizeUrl(getApiService(), C.api.url.NEW_GAME));
		try {
			
			//TODO: mGame -> JSONObject 
			
			final String result = HttpHelper.syncHttpRequest(post);
			
			Log.i(TAG, result);
					
			notifyApiResult(SUCCESS);
		} catch (IOException e) {
			notifyApiResult(FAIL_NETWORK);
		}
	}

}
