package com.zagayevskiy.fussball.api;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

public abstract class ApiRequest implements Runnable {

	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	public static final int FAIL_NETWORK = 3;
	
	public interface ResultListener{
		void onApiResult(int code);
	}
	
	private static class ApiResultHandler extends Handler{
		
		private final WeakReference<ResultListener> listenerRef;
		
		public ApiResultHandler(ResultListener listener) {
			listenerRef = new WeakReference<Registration.ResultListener>(listener);
		}
		
		@Override
		public void handleMessage(Message msg) {
			ResultListener listener = listenerRef.get();
			if(listener != null){
				listener.onApiResult(msg.what);
			}
		}
	};
	
	private ApiService mApiService;
	private final Handler mHandler;
	
	public ApiRequest(ResultListener listener) {
		mHandler = new ApiResultHandler(listener);
	}
	
	public final void setApiService(ApiService service){
		mApiService = service;
	}
	
	public final ApiService getApiService(){
		return mApiService;
	}
	
	protected void notifyApiResult(int code){
		mHandler.sendEmptyMessage(code);
	}
}
