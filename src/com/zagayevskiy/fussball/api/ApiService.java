package com.zagayevskiy.fussball.api;

import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.api.request.ApiRequest;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper.AsyncHttpTask;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

public class ApiService extends Service {

	public static final String TAG = ApiService.class.getName();

	public static class HttpCacheServiceBinder extends Binder{
		
		private final ApiService service;
		
		public HttpCacheServiceBinder(ApiService service){
			this.service = service;
		}
		
		public ApiService getService(){
			return service;
		}
	}
	
	private HandlerThread mWorkerThread;
	private Handler mWorkerHandler;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mWorkerThread = new HandlerThread(TAG);
		mWorkerThread.start();
		mWorkerHandler = new Handler(mWorkerThread.getLooper());		
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return START_STICKY;
	}
	
	public void onDestroy() {
	    super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return new HttpCacheServiceBinder(this);
	}
	
	public void newGame(Player player1, Player player2, int score1, int score2){
		final SharedPreferences prefs = getSharedPreferences(C.prefs.NAME, MODE_PRIVATE);
		final String url = C.api.url.NEW_GAME + prefs.getString(C.prefs.key.ACCESS_TOKEN, "");
		
		try{
			JSONObject json = new JSONObject()
				.put("date", System.currentTimeMillis())
				.put("player1", player1.getEmail())
				.put("player2", player2.getEmail())
			    .put("scoreOfPlayer1", score1)
			    .put("scoreOfPlayer2", score2);		   
	
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(json.toString(), "UTF-8");
			Log.i(TAG, json.toString(4));
			entity.setContentType("application/json");
			post.setEntity(entity);
			httpRequestWithoutCache(null, post, 0);
		}catch(JSONException ignored){			
		}catch(UnsupportedEncodingException ignored) {
		}
	}
	
	public void httpRequest(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		new AsyncHttpTask(listener, request, requestId).execute();
	}
	
	public void httpRequestWithoutCache(IHttpEventsListener listener, HttpUriRequest request, int requestId) {
		new AsyncHttpTask(listener, request, requestId).execute();
	}
	
	public void request(ApiRequest request, int requestCode){
		request.setApiService(this);
		request.setRequestCode(requestCode);
		mWorkerHandler.post(request);
	}
}
