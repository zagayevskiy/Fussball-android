package com.zagayevskiy.fussball;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private SimpleCursorAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		String[] fromColumns = { Player.FIELD_NICK, Player.FIELD_RATING };
        int[] toViews = {android.R.id.text1, android.R.id.text2 };
		
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.players_list_item, null, fromColumns, toViews, 0);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this); 
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), Player.URI, Player.FULL_PROJECTION, null, null, null);
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
