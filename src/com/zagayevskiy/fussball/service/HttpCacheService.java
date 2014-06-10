package com.zagayevskiy.fussball.service;

import java.lang.ref.WeakReference;

import org.apache.http.client.methods.HttpUriRequest;

import com.zagayevskiy.fussball.utils.HttpHelper;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.util.LruCache;

public class HttpCacheService extends Service {

	public static final String TAG = HttpCacheService.class.getName();
	
	/*
	 * Use 1/16th of the available memory for this memory cache.
	 */
	private static final int MAX_MEMORY_KB = (int) (Runtime.getRuntime().maxMemory() / 1024);
	private static final int MAX_CACHE_SIZE_KB = MAX_MEMORY_KB / 16;
	private LruCache<String, String> memoryCache = new LruCache<String, String>(MAX_CACHE_SIZE_KB){
		protected int sizeOf(String key, String value) {
			/*
			 * Magic formula to compute size of used memory in Kb;
			 */
			final int size = 8 * (int) ((((value.length()) * 2) + 45) / 8) / 1024; 
			return size == 0 ? 1 : size;
		};
	};
	
	public static class HttpCacheServiceBinder extends Binder{
		
		private final HttpCacheService service;
		
		public HttpCacheServiceBinder(HttpCacheService service){
			this.service = service;
		}
		
		public HttpCacheService getService(){
			return service;
		}
	}
	
	private class HttpListener implements IHttpEventsListener{

		private final WeakReference<IHttpEventsListener> outListenerRef;
		private final HttpUriRequest request;
		
		public HttpListener(IHttpEventsListener outListener, HttpUriRequest request){
			outListenerRef = new WeakReference<IHttpEventsListener>(outListener);
			this.request = request;
		}
		
		@Override
		public void onHttpProgressUpdate(int requestId, Integer... progress) {
			IHttpEventsListener l = outListenerRef.get();
			if(l != null){
				l.onHttpProgressUpdate(requestId, progress);
			}
		}

		@Override
		public void onHttpResponse(int requestId, String result) {
			putToCache(request, result);
			IHttpEventsListener l = outListenerRef.get();
			if(l != null){
				l.onHttpResponse(requestId, result);
			}
		}

		@Override
		public void onHttpRequestFail(int requestId, Exception ex) {
			IHttpEventsListener l = outListenerRef.get();
			if(l != null){
				l.onHttpRequestFail(requestId, ex);
			}
		}
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
	
	public void httpRequest(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		String response = getFromCache(request);
		if(response != null){
			listener.onHttpResponse(requestId, response);
			return;
		}
		
		new HttpHelper(new HttpListener(listener, request), request, requestId).execute();
	}
	
	public void httpRequestWithoutCache(IHttpEventsListener listener, HttpUriRequest request, int requestId) {
		new HttpHelper(listener, request, requestId).execute();
	}
	
	private void putToCache(HttpUriRequest request, String response){
		final String key = getKey(request);
		if(memoryCache.get(key) != null){
			memoryCache.remove(key);
		}
		memoryCache.put(key, response);
	}
	
	private String getFromCache(HttpUriRequest request){
		final String key = getKey(request);
		return memoryCache.get(key);
	}
	
	private static String getKey(HttpUriRequest request){
		return request.getMethod() + request.getURI().toString();
	}	
}
