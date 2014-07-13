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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;

import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;
import com.zagayevskiy.fussball.api.request.LoadGamesRequest;

public class GamesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>, ResultListener, OnRefreshListener  {
	
	private static final String TAG = GamesFragment.class.getName();
	
	public static final String KEY_WHERE_CLAUSE = TAG + "_where_clause";
	public static final String KEY_WHERE_ARGUMENTS = TAG + "_where_args";
	
	private final static String[] FROM_COLUMNS = {
		Game.FIELD_PLAYER1_NICK, Game.FIELD_PLAYER2_NICK,		
		Game.FIELD_SCORE1, Game.FIELD_SCORE2,
		Game.FIELD_PLAYER1_RATING_DELTA, Game.FIELD_PLAYER2_RATING_DELTA
	};
    
	private final static int[] TO_VIEWS = {
		R.id.player1_nick, R.id.player2_nick,
		R.id.player1_score, R.id.player2_score,
		R.id.player1_rating_delta, R.id.player2_rating_delta 
	};
	
	private SimpleCursorAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;
	
	private String mWhere;
	private String[] mWhereArgs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle arguments = getArguments();
		if(arguments != null){
			fillWhere(arguments);		
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
        
		View root = inflater.inflate(R.layout.games_list, container, false);
		
		mRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.games_swipe_to_refresh);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setColorSchemeResources(R.color.refresh1, R.color.refresh2, R.color.refresh3, R.color.refresh4);
		
        setHasOptionsMenu(true);
		
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.games_list_item, null, FROM_COLUMNS, TO_VIEWS, 0);    
        mAdapter.setViewBinder(new GamesListViewBinder(getActivity()));
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        
		return root;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem item = menu.add(R.string.menu_new_game);
		item.setIcon(R.drawable.ic_action_new);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				
				startActivity(new Intent(getActivity(), NewGameActivity.class));
				
				return true;
			}
		});
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), Game.URI, Game.FULL_PROJECTION, mWhere, mWhereArgs, Game.ORDER_DATE_DESC);
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
	public void onApiResult(int requestCode, int resultCode) {
		mRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onRefresh() {
		((IApiManager) getActivity()).getApi().request(new LoadGamesRequest(this), 0);
	}
	
	private void fillWhere(Bundle arguments){
		
		mWhere = arguments.getString(KEY_WHERE_CLAUSE);
		mWhereArgs = arguments.getStringArray(KEY_WHERE_ARGUMENTS);
		
	}
}
