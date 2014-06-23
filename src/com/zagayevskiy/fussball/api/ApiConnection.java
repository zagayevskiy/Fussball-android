package com.zagayevskiy.fussball.api;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.api.ApiService.HttpCacheServiceBinder;
import com.zagayevskiy.fussball.api.request.ApiRequest;
import com.zagayevskiy.fussball.utils.HttpHelper.IHttpEventsListener;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class ApiConnection implements ServiceConnection {

	private ApiService mService;
	private boolean mBound = false;
	private Context mContext;
	private List<IBindUnbindListener> mBindUnbindListenerList = new LinkedList<IBindUnbindListener>();
	
	public interface IBindUnbindListener{
		void onApiBind();
		void onApiUnbind();
	}
	
	public interface IConnectionManager{
		ApiConnection getConnection();
	}
	
	public ApiConnection(IBindUnbindListener bindUnbindListener, Context context) {
		this.mContext = context; 
		this.mBindUnbindListenerList.add(bindUnbindListener);
	}
	
	public boolean isBound(){
		return mBound;
	}
	
	public void addBindUnbindListener(IBindUnbindListener listener){
		mBindUnbindListenerList.add(listener);
	}
	
	public void removeBindInbindListener(IBindUnbindListener listener){
		mBindUnbindListenerList.remove(listener);
	}
	
	@Override
	public void onServiceConnected(ComponentName name, IBinder binder) {
		mService = ((HttpCacheServiceBinder) binder).getService();
		mBound = true;
		for(IBindUnbindListener listener: mBindUnbindListenerList){
			if(listener != null){
				listener.onApiBind();
			}
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		mBound = false;
		mService = null;
		for(IBindUnbindListener listener: mBindUnbindListenerList){
			if(listener != null){
				listener.onApiUnbind();
			}
		}
	}

	public void bind(){
		Log.i("Api", "bind");
		mContext.bindService(new Intent(mContext, ApiService.class), this, Context.BIND_AUTO_CREATE);
	}
	
	public void unbind(){
		if(mBound){
			mBound = false;
			mContext.unbindService(this);
		}
	}
	
	public void newGame(Player player1, Player player2, int score1, int score2){
		if(mBound){
			mService.newGame(player1, player2, score1, score2);
		}
	}
	
	public void httpRequest(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		if(mBound){
			mService.httpRequest(listener, request, requestId);
		}
	}
	
	public void httpRequestWithoutCache(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		if(mBound){
			mService.httpRequestWithoutCache(listener, request, requestId);
		}
	}
	
	public void request(ApiRequest request, int requestCode){
		if(mBound){
			mService.request(request, requestCode);
		}
	}
}
