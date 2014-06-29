package com.zagayevskiy.fussball.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.util.Log;

public class HttpHelper {

	public interface IHttpEventsListener {
		void onHttpProgressUpdate(int requestId, Integer... progress);

		void onHttpResponse(int requestId, String result);

		void onHttpRequestFail(int requestId, Exception ex);
	}

	private static final String TAG = HttpHelper.class.getName();

	public static class AsyncHttpTask extends AsyncTask<Void, Integer, String> {

		private final IHttpEventsListener mListener;
		private final HttpUriRequest mRequest;
		private final int mRequestId;

		private Exception executeException;

		public AsyncHttpTask(IHttpEventsListener listener,
				HttpUriRequest request, int requestId) {
			this.mListener = listener;
			this.mRequest = request;
			mRequestId = requestId;
		}

		@Override
		protected String doInBackground(Void... unused) {
			String result = null;
			try {
				result = syncHttpRequest(mRequest);
			} catch (ClientProtocolException e) {
				executeException = e;
			} catch (IOException e) {
				executeException = e;
			}
			return result;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			if (mListener != null) {
				mListener.onHttpProgressUpdate(mRequestId, progress);
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (mListener != null) {
				if (result == null && executeException != null) {
					mListener.onHttpRequestFail(mRequestId, executeException);
				} else {
					mListener.onHttpResponse(mRequestId, result);
					Log.i(TAG, result);
				}
			}
		}
	}
	
	private static final HttpParams CONNECTION_PARAMS = new BasicHttpParams();
	private static final int TIMEOUT = 10000;
	
	static{
		HttpConnectionParams.setConnectionTimeout(CONNECTION_PARAMS, TIMEOUT);
		HttpConnectionParams.setSoTimeout(CONNECTION_PARAMS, TIMEOUT);
	}

	public static final String syncHttpRequest(HttpUriRequest request)
			throws ClientProtocolException, IOException {
		StringBuffer resultBuffer = new StringBuffer("");
		BufferedReader bufferedReader = null;
		try {
			
			HttpClient httpClient = new DefaultHttpClient(CONNECTION_PARAMS);
			
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