package com.zagayevskiy.fussball.utils;

/**
 * 
 * project constants
 *
 */
public final class C {
	
	public static final String TAG = C.class.getName();
	
	public static final class prefs{
		public static final String NAME = TAG + "_prefs";
		
		public static final class key{
			public static final String ACCESS_TOKEN = TAG + "_access_token";
		}
	}
	
	public static final class json{
		public static final class key{
			public static final String ACCESS_TOKEN = "authToken";
			public static final String MESSAGE = "message";
			
		}
	}

}
