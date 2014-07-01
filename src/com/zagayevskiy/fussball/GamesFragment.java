package com.zagayevskiy.fussball;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;

public class GamesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {
	
	private static final String TAG = GamesFragment.class.getName();
	
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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {		
        
        setHasOptionsMenu(true);
		
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.games_list_item, null, FROM_COLUMNS, TO_VIEWS, 0);    
        mAdapter.setViewBinder(new GamesListViewBinder(getActivity()));
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        
		return super.onCreateView(inflater, container, savedInstanceState);
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
		return new CursorLoader(getActivity(), Game.URI, Game.FULL_PROJECTION, null, null, null);
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
