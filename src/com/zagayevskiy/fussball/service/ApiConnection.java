package com.zagayevskiy.fussball.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.zagayevskiy.fussball.service.HttpCacheService.HttpCacheServiceBinder;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ApiConnection implements ServiceConnection {

	private HttpCacheService service;
	private boolean bound = false;
	private Context context;
	private List<IBindUnbindListener> bindUnbindListenerList = new LinkedList<IBindUnbindListener>();
	
	public interface IBindUnbindListener{
		void onApiBind();
		void onApiUnbind();
	}
	
	public interface IConnectionManager{
		ApiConnection getConnection();
	}
	
	public ApiConnection(IBindUnbindListener bindUnbindListener, Context context) {
		this.context = context; 
		this.bindUnbindListenerList.add(bindUnbindListener);
	}
	
	public boolean isBound(){
		return bound;
	}
	
	public void addBindUnbindListener(IBindUnbindListener listener){
		bindUnbindListenerList.add(listener);
	}
	
	public void removeBindInbindListener(IBindUnbindListener listener){
		bindUnbindListenerList.remove(listener);
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		service = ((HttpCacheServiceBinder) binder).getService();
		bound = true;
		for(IBindUnbindListener listener: bindUnbindListenerList){
			if(listener != null){
				listener.onApiBind();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		bound = false;
		service = null;
		for(IBindUnbindListener listener: bindUnbindListenerList){
			if(listener != null){
				listener.onApiUnbind();
			}
		}
	}

	public void bind(){
		Log.i("Api", "bind");
		context.bindService(new Intent(context, HttpCacheService.class), this, Context.BIND_AUTO_CREATE);
	}
	
	public void unbind(){
		if(bound){
			bound = false;
			context.unbindService(this);
		}
	}
	
	public void httpRequest(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		if(bound){
			service.httpRequest(listener, request, requestId);
		}
	}
	
	public void httpRequestWithoutCache(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		if(bound){
			service.httpRequestWithoutCache(listener, request, requestId);
		}
	}
}
