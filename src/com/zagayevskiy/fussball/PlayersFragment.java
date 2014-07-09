package com.zagayevskiy.fussball;

import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlayersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ResultListener, OnRefreshListener {
	
	private SimpleCursorAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.players_list, container, false);
		
		String[] fromColumns = { Player.FIELD_NICK, Player.FIELD_RATING, Player.FIELD_EMAIL_HASH };
        int[] toViews = {android.R.id.text1, android.R.id.text2, R.id.player_photo };
		
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.players_list_item, null, fromColumns, toViews, 0);
        mAdapter.setViewBinder(new PlayersListViewBinder(getActivity()));
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        
        mRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.players_swipe_to_refresh);
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setColorSchemeResources(R.color.refresh1, R.color.refresh2, R.color.refresh3, R.color.refresh4);
        
		return root;
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

	@Override
	public void onRefresh() {
		((IApiManager) getActivity()).getApi().request(new LoadPlayersRequest(this), 0);
		Log.e("onRefresh", "onRefresh");
	}

	@Override
	public void onApiResult(int requestCode, int resultCode) {
		mRefreshLayout.setRefreshing(false);
		Log.e("onApiResult", "onApiResult");
	}
}
