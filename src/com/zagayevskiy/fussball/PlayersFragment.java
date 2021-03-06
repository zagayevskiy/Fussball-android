package com.zagayevskiy.fussball;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;
import com.zagayevskiy.fussball.api.request.LoadPlayersRequest;

public class PlayersFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ResultListener, OnRefreshListener {
	
	private SimpleCursorAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View root = inflater.inflate(R.layout.players_list, container, false);
		
		String[] fromColumns = { Player.FIELD_NICK, Player.FIELD_RATING, Player.FIELD_TOTAL_PLAYED, Player.FIELD_TOTAL_WON, Player.FIELD_EMAIL_HASH };
        int[] toViews = { R.id.nick, R.id.rating, R.id.total_played, R.id.total_won, R.id.player_photo };
		
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
		return new CursorLoader(getActivity(), Player.URI, Player.FULL_PROJECTION, null, null, Player.ORDER_RATING_DESC);
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
	}

	@Override
	public void onApiResult(int requestCode, int resultCode) {
		mRefreshLayout.setRefreshing(false);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(getActivity(), ProfileActivity.class);
		Cursor c = (Cursor)mAdapter.getItem(position);
		intent.putExtra(ProfileActivity.KEY_PLAYER_NICK, (c.getString(c.getColumnIndex(Player.FIELD_NICK))));
		startActivity(intent);
	}
}
