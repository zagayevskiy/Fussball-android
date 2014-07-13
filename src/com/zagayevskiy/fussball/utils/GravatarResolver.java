package com.zagayevskiy.fussball.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class GravatarResolver {
	
	private static final String TAG = GravatarResolver.class.getName();
	
	public interface IOnResolveListener{
		void onResolve(Bitmap result, long resultId);
	}
	
	private static GravatarResolver mInstance = new GravatarResolver();
	
	private GravatarResolver(){};
	
	public static GravatarResolver getInstance(){
		return mInstance;
	}
	
	public void resolve(Context context, String emailHash, long resultId, IOnResolveListener listener){
		File extDir = context.getExternalFilesDir(null);
		
		final String imageUrl = "http://www.gravatar.com/avatar/" + emailHash + "?d=monsterid";
		
		Log.e(TAG, imageUrl);
		
		final String fileName = emailHash;
		File file = (extDir != null) ? new File(extDir, fileName) : null;
		
		if(file == null || !file.exists()){
			//Let's try load it from server 
			ImageDownloader downloader = new ImageDownloader(listener, resultId);
			downloader.execute(imageUrl, file != null ? file.getAbsolutePath() : null);
			
		}else{
			//Ok, we have file in ext storage
			if(listener != null){
				Bitmap result = BitmapFactory.decodeFile(file.getAbsolutePath());
				listener.onResolve(result, resultId);
			}
		}
	}
	
	/**
	 * 
	 * Simple implementation of IOnResolveListener. It use imageView.setImageBitmap(result); by default
	 *
	 */
	public static class SimpleOnResolveListener implements IOnResolveListener{
    	
    	private WeakReference<ImageView> imageViewRef;
    	
    	public SimpleOnResolveListener(ImageView imageView){
    		this.imageViewRef = new WeakReference<ImageView>(imageView);
    	}
    	
    	@Override
    	public void onResolve(Bitmap result, long resultId){
    		ImageView imageView = imageViewRef.get();
    		if(imageView != null){
    			imageView.setImageBitmap(result);
    		}
    	}
    }
	
	private static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

		private IOnResolveListener listener;
		private long resultId;
		
	    public ImageDownloader(IOnResolveListener listener, long resultId) {
	    	Log.i(TAG, "new ImageDownloader");
			this.listener = listener;
			this.resultId = resultId;
	    }

	    @Override
	    protected Bitmap doInBackground(String... urls) {
	        String url = urls[0];
	        String fileName = urls[1];
	        Bitmap result = null;
	        
	        try {
	            InputStream in = new java.net.URL(url).openStream();
	            result = BitmapFactory.decodeStream(in);
	            in.close();
	            
	            if(fileName != null){
		            FileOutputStream out = new FileOutputStream(fileName);
		            result.compress(Bitmap.CompressFormat.PNG, 90, out);
		            out.close();
	            }
	            
	        }catch(MalformedURLException e) {
	        	Log.e(TAG, "MalformedURLException", e);
	        }catch(IOException e){
	            Log.e(TAG, "IOException", e);
	        }catch(Exception e){
	        	Log.e(TAG, "Exception", e);
	        }
	        return result;
	    }

	    @Override
	    protected void onPostExecute(Bitmap result) {
	        if(listener != null){
	        	listener.onResolve(result, resultId);
	        }
	    }
	}
}
