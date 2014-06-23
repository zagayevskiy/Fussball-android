package com.zagayevskiy.fussball.api.request;

import java.lang.ref.WeakReference;

import com.zagayevskiy.fussball.api.ApiService;

import android.os.Handler;
import android.os.Message;

public abstract class ApiBaseRequest implements Runnable {

	public static final int SUCCESS = 1;
	public static final int FAIL = 2;
	public static final int FAIL_NETWORK = 3;
	
	public interface ResultListener{
		void onApiResult(int requestCode, int resultCode);
	}
	
	private static class ApiResultHandler extends Handler{
		
		private final WeakReference<ResultListener> listenerRef;
		
		private int mRequestCode = 0;
		
		public ApiResultHandler(ResultListener listener) {
			listenerRef = new WeakReference<ResultListener>(listener);
		}
		
		@Override
		public void handleMessage(Message msg) {
			ResultListener listener = listenerRef.get();
			if(listener != null){
				listener.onApiResult(mRequestCode, msg.what);
			}
		}
	};
	
	private ApiService mApiService;
	private final ApiResultHandler mHandler;
	
	public ApiBaseRequest(ResultListener listener) {
		mHandler = new ApiResultHandler(listener);
	}
	
	public final void setApiService(ApiService service){
		mApiService = service;
	}
	
	public final void setRequestCode(int code){
		mHandler.mRequestCode = code;
	}
	
	public final ApiService getApiService(){
		return mApiService;
	}
	
	protected void notifyApiResult(int code){
		mHandler.sendEmptyMessage(code);
	}
}
