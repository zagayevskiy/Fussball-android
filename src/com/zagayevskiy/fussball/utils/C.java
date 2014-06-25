
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
			public static final String OWNER_EMAIL = TAG + "_owner_email";
			
		}
	}
	
	public static final class api{
		
		public static final class url{
			public static final String HOST = "http://212.154.168.144:5050/api/";
			public static final String LOGIN = HOST + "login";
			public static final String REGISTRATION = HOST + "players";
			public static final String PLAYERS = HOST + "players?token=";
			public static final String GAMES = HOST + "games";
			public static final String NEW_GAME = HOST + "games";
			
		}
		public static final class json{
			public static final class key{
				public static final String ACCESS_TOKEN = "accessToken";
				public static final String MESSAGE = "message";
			}
		}
	}
}
