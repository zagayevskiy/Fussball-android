package com.zagayevskiy.fussball.utils;

/**
 * 
 * project constants
 *
 */
public final class C {
	
	public static final String TAG = C.class.getName();
	
	public static final class db{
		public static final String AUTHORITY = "com.zagayevskiy.fussball.db";
		public static final String BASE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.zagayevskiy.fussball"; 
	}
	
	public static final class prefs{
		public static final String NAME = TAG + "_prefs";
		
		public static final class key{
			public static final String ACCESS_TOKEN = TAG + "_access_token";
		}
	}
	
	public static final class json{
		public static final class key{
			public static final String ACCESS_TOKEN = "accessToken";
			public static final String MESSAGE = "message";
			
		}
	}

}
