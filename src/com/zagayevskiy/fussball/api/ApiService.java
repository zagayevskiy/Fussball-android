package com.zagayevskiy.fussball.api;

import org.apache.http.client.methods.HttpUriRequest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import com.zagayevskiy.fussball.api.request.ApiBaseRequest;
import com.zagayevskiy.fussball.utils.HttpHelper.AsyncHttpTask;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

public class ApiService extends Service {

	public static final String TAG = ApiService.class.getName();

	public static class ApiServiceBinder extends Binder{
		
		private final ApiService service;
		
		public ApiServiceBinder(ApiService service){
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
		return new ApiServiceBinder(this);
	}
	
	public void httpRequest(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		new AsyncHttpTask(listener, request, requestId).execute();
	}
	
	public void httpRequestWithoutCache(IHttpEventsListener listener, HttpUriRequest request, int requestId) {
		new AsyncHttpTask(listener, request, requestId).execute();
	}
	
	public void request(ApiBaseRequest request, int requestCode){
		request.setApiService(this);
		request.setRequestCode(requestCode);
		mWorkerHandler.post(request);
	}
}
