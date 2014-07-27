package com.zagayevskiy.fussball;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zagayevskiy.fussball.api.IApiManager;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest;
import com.zagayevskiy.fussball.api.request.ApiBaseRequest.ResultListener;
import com.zagayevskiy.fussball.api.request.NewGameRequest;
import com.zagayevskiy.fussball.utils.GravatarResolver;
import com.zagayevskiy.fussball.utils.GravatarResolver.SimpleOnResolveListener;

public class NewGameFragment extends Fragment implements View.OnClickListener, ResultListener {

	public static final String TAG = NewGameFragment.class.getName();
	
	private static final int REQUEST_SELECT_PLAYER1 = 1;
	private static final int REQUEST_SELECT_PLAYER2 = 2;
	
	private Button mButtonOk;
	private TextView mPlayer1Nick, mPlayer2Nick;
	private ImageView mPlayer1Photo, mPlayer2Photo;
	private EditText mScore1, mScore2;
	private ProgressDialog mProgressDialog;
	
	private Player mPlayer1, mPlayer2;
	
	private GravatarResolver mGravatar = GravatarResolver.getInstance();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.new_game_fragment, container, false);
		
		mPlayer1Nick = (TextView) v.findViewById(R.id.player1_nick);
		mPlayer2Nick = (TextView) v.findViewById(R.id.player2_nick);	
		
		mPlayer1Photo = (ImageView) v.findViewById(R.id.player1_photo);
		mPlayer2Photo = (ImageView) v.findViewById(R.id.player2_photo);
		
		mScore1 = (EditText) v.findViewById(R.id.player1_score);
		mScore2 = (EditText) v.findViewById(R.id.player2_score);
		
		mButtonOk = (Button) v.findViewById(R.id.ok);
		
		v.findViewById(R.id.select_player1).setOnClickListener(this);
		v.findViewById(R.id.select_player2).setOnClickListener(this);
		mButtonOk.setOnClickListener(this);
		
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setCancelable(false);
		mProgressDialog.setTitle(R.string.new_game_in_progress);
		
		Intent intent = getActivity().getIntent();
		if(intent == null || !intent.hasExtra(NewGameActivity.KEY_PLAYER1_NICK)){
			//Has no wanted players => set by default
			setPlayer1(Player.getOwner(getActivity()));
		}else{
			//Has player1 => set him
			final String nick1 = intent.getStringExtra(NewGameActivity.KEY_PLAYER1_NICK);
			setPlayer1(Player.getSingle(getActivity(), Player.WHERE_NICK, nick1, null));
			
			if(intent.hasExtra(NewGameActivity.KEY_PLAYER2_NICK)){
				//Has player 2 => set him
				final String nick2 = intent.getStringExtra(NewGameActivity.KEY_PLAYER2_NICK);
				setPlayer2(Player.getSingle(getActivity(), Player.WHERE_NICK, nick2, null));
			}else{
				//Has no player 2 => set by default
				setPlayer2(Player.getOwner(getActivity()));
			}
		}
		setRetainInstance(true);
		
		return v;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if(id == R.id.select_player1 || id == R.id.select_player2){
			int requestCode = 0;
			
			final Intent intent = new Intent(getActivity(), SearchPlayerActivity.class);
			
			switch(id){
				case R.id.select_player1:
					requestCode = REQUEST_SELECT_PLAYER1;
					if(mPlayer2 != null){
						intent.putExtra(SearchPlayerActivity.KEY_EXCLUDE_PLAYER_IDS, new String[]{ String.valueOf(mPlayer2.getId()) });
					}
				break;
				
				case R.id.select_player2:
					requestCode = REQUEST_SELECT_PLAYER2;
					if(mPlayer1 != null){
						intent.putExtra(SearchPlayerActivity.KEY_EXCLUDE_PLAYER_IDS, new String[]{ String.valueOf(mPlayer1.getId()) });
					}
				break;
			}
			startActivityForResult(intent, requestCode);
		}else if(id == R.id.ok){
			
			//TODO: FIXME it's not okay, just simple bug fix
			if(mScore1.getText().length() == 0 
				||mScore2.getText().length() == 0
				|| mPlayer1 == null 
				|| mPlayer2 == null
				|| mScore1.getText().toString().equals(mScore2.getText().toString())){
				Toast.makeText(getActivity(), "Something wrong in your data, yo!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			final int s1 = Integer.parseInt(mScore1.getText().toString());
			final int s2 = Integer.parseInt(mScore2.getText().toString());
			
			mProgressDialog.show();
			
			Game game = new Game(mPlayer1.getNick(), mPlayer2.getNick(), s1, s2);
			((IApiManager) getActivity()).getApi().request(new NewGameRequest(this, game), 0);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == REQUEST_SELECT_PLAYER1 || requestCode == REQUEST_SELECT_PLAYER2){
				final String playerId = String.valueOf(data.getLongExtra(SearchPlayerActivity.RESULT_KEY_PLAYER_ID, Player.INVALID_ID));

				final Player player = Player.getSingle(getActivity(), Player.WHERE_ID, playerId, null);
				Log.i(TAG, playerId);
				
				switch(requestCode){
					case REQUEST_SELECT_PLAYER1:
						setPlayer1(player);
					break;
				
					case REQUEST_SELECT_PLAYER2:
						setPlayer2(player);
					break;				
				}
				
			}else{
				super.onActivityResult(requestCode, resultCode, data);
			}
		}else{
			super.onActivityResult(requestCode, resultCode, data);
		}
				
	}
	
	private void setPlayer1(Player player){
		if(player == null || player.equals(mPlayer2)){
			return;
		}
		mPlayer1 = player;
		mPlayer1Nick.setText(player.getNick());
		mGravatar.resolve(getActivity(), player.getEmailHash(), 0, 
			new SimpleOnResolveListener(mPlayer1Photo));
		
	}
	
	private void setPlayer2(Player player){
		if(player == null || player.equals(mPlayer1)){
			return; 
		}
		mPlayer2 = player;
		mPlayer2Nick.setText(player.getNick());
		mGravatar.resolve(getActivity(), player.getEmailHash(), 1, 
				new SimpleOnResolveListener(mPlayer2Photo));
	}

	@Override
	public void onApiResult(int requestCode, int resultCode) {
		mProgressDialog.cancel();
		if(resultCode == ApiBaseRequest.SUCCESS){
			getActivity().finish();
		}else if(resultCode == ApiBaseRequest.FAIL_NETWORK){
			Toast.makeText(getActivity(), "Can't save game: network error", Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getActivity(), "Can't save game", Toast.LENGTH_LONG).show();
		}
	}
}
