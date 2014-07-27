package com.zagayevskiy.fussball;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchPlayerActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

	public static final String TAG = SearchPlayerActivity.class.getName();
	
	public static final String KEY_EXCLUDE_PLAYER_IDS = TAG + "_exclude_user_id";
	
	public static final String RESULT_KEY_PLAYER_ID = TAG + "_user_id";
	
	private static final String[] from = new String[]{ Player.FIELD_NICK };
	private static final int[] to = new int[] { android.R.id.text1 };
	
	private SimpleCursorAdapter mAdapter;
	
	private String selection = null;
	private String[] selectionArgs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		buildSelection();
		
		mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null, from, to, 0);
		setListAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.select_player);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		getListView().setOnItemClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			goUp();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, Player.URI, Player.SEARCH_PROJECTION, selection, selectionArgs, Player.ORDER_NICK_ASC);
	} 

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Cursor c = (Cursor) parent.getItemAtPosition(position);
		final long userId = c.getLong(c.getColumnIndex(Player.FIELD_ID));
		Intent result = new Intent();
		result.putExtra(RESULT_KEY_PLAYER_ID, userId);
		setResult(RESULT_OK, result);
		finish();
	}

	private void buildSelection(){
		Intent intent = getIntent();
		if(intent.hasExtra(KEY_EXCLUDE_PLAYER_IDS)){
			
			String[] ids = intent.getStringArrayExtra(KEY_EXCLUDE_PLAYER_IDS);
			StringBuilder builder = new StringBuilder();
			for(int i = 0; i < ids.length; ++i){
				builder.append(ids[i]);
				if(i != ids.length - 1){
					builder.append(",");
				}
			}
			
			selection = String.format(Player.WHERE_ID_NOT_IN_FMT, builder.toString());
		}		
	}
	
	private void goUp(){
		finish();
	}
}
