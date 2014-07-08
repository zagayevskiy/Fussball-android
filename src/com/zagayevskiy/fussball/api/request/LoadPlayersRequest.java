package com.zagayevskiy.fussball.api.request;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.zagayevskiy.fussball.Player;
import com.zagayevskiy.fussball.api.Token;
import com.zagayevskiy.fussball.utils.C;
import com.zagayevskiy.fussball.utils.HttpHelper;

public class LoadPlayersRequest extends ApiBaseRequest {

	public LoadPlayersRequest(ResultListener listener){
		super(listener);
	}
	
	@Override
	public void run() {
		
		final HttpGet get = new HttpGet(
			C.api.url.PLAYERS
			+ Token.getInstance().getToken(getApiService())
		);

		try {
			final String result = HttpHelper.syncHttpRequest(get);
			Log.i("players", result);
			
			final JSONObject json = new JSONObject(result);
			
			final JSONArray errors = json.getJSONArray(C.api.json.key.ERRORS);
			if(errors.length() > 0){
				notifyApiResult(FAIL);
				return;
			}
			
			ContentValues[] values = Player.jsonToDBContentValues(json.getJSONArray("players"));
			
			final Player owner = Player.getOwner(getApiService());
			
			ContentResolver resolver = getApiService().getContentResolver();
			
			ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			operations.add(ContentProviderOperation.newDelete(Player.URI).build());
			
			for(ContentValues v : values){
				if(v.getAsString(Player.FIELD_NICK).equals(owner.getNick())){
					v.put(Player.FIELD_IS_OWNER, Player.OWNER);
				}
				operations.add(ContentProviderOperation.newInsert(Player.URI).withValues(v).build());
			}				
			resolver.applyBatch(C.db.AUTHORITY, operations);
		
		} catch (IOException e) {
			notifyApiResult(FAIL_NETWORK);
			Log.e("Fail", "Ex", e);
		} catch (JSONException e) {
			notifyApiResult(FAIL);
			Log.e("Fail", "Ex", e);
		} catch (RemoteException e) {
			notifyApiResult(FAIL_NETWORK);
			Log.e("Fail", "Ex", e);
		} catch (OperationApplicationException e) {
			Log.e("Fail", "Ex", e);
			notifyApiResult(FAIL);
		}
	}

}
