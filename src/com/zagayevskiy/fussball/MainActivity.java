package com.zagayevskiy.fussball;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;

import com.zagayevskiy.fussball.service.ApiConnection;
import com.zagayevskiy.fussball.service.ApiConnection.IBindUnbindListener;

public class MainActivity extends ListActivity implements IBindUnbindListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	private ApiConnection api;
	
	private SimpleCursorAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String[] fromColumns = { User.FIELD_EMAIL, User.FIELD_RATING };
        int[] toViews = {android.R.id.text1, android.R.id.text2 };
		
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);  
        
	}

	@Override
	protected void onStart() {
		super.onStart();
		api = new ApiConnection(this, this);
		api.bind();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		api.unbind();
	}
	
	@Override
	public void onApiBind() {
		api.loadUsers();	
	}

	@Override
	public void onApiUnbind() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, User.URI, User.FULL_PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
}
