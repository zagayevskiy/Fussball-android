package com.zagayevskiy.fussball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class NewGameFragment extends Fragment implements View.OnClickListener {

	public static final String TAG = NewGameFragment.class.getName();
	
	private static final int REQUEST_SELECT_PLAYER1 = 1;
	private static final int REQUEST_SELECT_PLAYER2 = 2;
	
	private Button selectPlayer1, selectPlayer2, buttonOk;
	private EditText score1, score2;
	
	private Player player1, player2;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.new_game_fragment, container, false);
		
		selectPlayer1 = (Button) v.findViewById(R.id.select_player1);
		selectPlayer2 = (Button) v.findViewById(R.id.select_player2);
		buttonOk = (Button) v.findViewById(R.id.auth_ok);
		
		score1 = (EditText) v.findViewById(R.id.player1_score);
		score2 = (EditText) v.findViewById(R.id.player2_score);
		
		selectPlayer1.setOnClickListener(this);
		selectPlayer2.setOnClickListener(this);
		buttonOk.setOnClickListener(this);
		
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
					if(player2 != null){
						intent.putExtra(SearchPlayerActivity.KEY_EXCLUDE_PLAYER_IDS, new String[]{ String.valueOf(player2.getId()) });
					}
				break;
				
				case R.id.select_player2:
					requestCode = REQUEST_SELECT_PLAYER2;
					if(player1 != null){
						intent.putExtra(SearchPlayerActivity.KEY_EXCLUDE_PLAYER_IDS, new String[]{ String.valueOf(player1.getId()) });
					}
				break;
			}
			startActivityForResult(intent, requestCode);
		}else if(id == R.id.auth_ok){
			final int s1 = Integer.parseInt(score1.getText().toString());
			final int s2 = Integer.parseInt(score2.getText().toString());
			((MainActivity) getActivity()).getApi().newGame(player1, player2, s1, s2);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == REQUEST_SELECT_PLAYER1 || requestCode == REQUEST_SELECT_PLAYER2){
				final String playerId = String.valueOf(data.getLongExtra(SearchPlayerActivity.RESULT_KEY_PLAYER_ID, Player.INVALID_ID));

				final Player player = Player.getSingle(getActivity(), Player.WHERE_ID, playerId, null);
				
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
		player1 = player;
		selectPlayer1.setText(player.getEmail());
	}
	
	private void setPlayer2(Player player){
		player2 = player;
		selectPlayer2.setText(player.getEmail());
	}
}
