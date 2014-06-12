package com.zagayevskiy.fussball.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpHelper extends AsyncTask<Void, Integer, String>{

	public interface IHttpEventsListener{
    	void onHttpProgressUpdate(int requestId, Integer... progress);
    	void onHttpResponse(int requestId, String result);
    	void onHttpRequestFail(int requestId, Exception ex);
    }
	
	private static final String TAG = AsyncHttpHelper.class.getName();

	private final IHttpEventsListener listener;
	private final HttpUriRequest request;
	private final int requestId;
	
	private Exception executeException;
	
	public AsyncHttpHelper(IHttpEventsListener listener, HttpUriRequest request, int requestId){
		this.listener = listener;
		this.request = request;
		this.requestId = requestId;
	}
	
	@Override
	protected String doInBackground(Void... unused) {
		
		String result;
		
        try {
           result = httpRequest(request);
        } catch (IOException e) {
        	Log.e(TAG, "Network exception", e);
        	executeException = e;
        	return null;
        }
        
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress){
		if(listener != null){
			listener.onHttpProgressUpdate(requestId, progress);
		}
	}
	
	@Override 
	protected void onPostExecute(String result){
		if(listener != null){
			if(result == null && executeException != null){
				listener.onHttpRequestFail(requestId, executeException);
			}else{
				listener.onHttpResponse(requestId, result);
			}
		}
	}
	
	public static final String httpRequest(HttpUriRequest request) throws IOException{
		StringBuffer resultBuffer = new StringBuffer("");
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();                 

            HttpResponse httpResponse = httpClient.execute(request);
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                resultBuffer.append(readLine);
                resultBuffer.append("\n");
                readLine = bufferedReader.readLine();
            }
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                	Log.e(TAG, "I/O exception", e);                	
                }
            }
        }
        
		return resultBuffer.toString();
	}
}
