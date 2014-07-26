package com.zagayevskiy.fussball;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zagayevskiy.fussball.utils.GravatarResolver;

public class PlayersListAdapter extends ArrayAdapter<Player>{

	private final GravatarResolver mGravatar = GravatarResolver.getInstance();
	private ArrayList<Player> mPlayers = new ArrayList<Player>();
	
	public PlayersListAdapter(Context context) {
		super(context, R.layout.players_list_item);
	}
	
	@Override
	public int getCount(){
		return mPlayers.size();
	}
	
	@Override
	public void clear() {
		super.clear();
		mPlayers.clear();
	}
	
	@Override
	public void add(Player player) {
		mPlayers.add(player);
	}
	
	@Override
	public void addAll(Collection<? extends Player> players) {
		mPlayers.addAll(players);
	}
	
	@Override
	public void addAll(Player... players) {
		for(Player p: players){
			mPlayers.add(p);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {			
		
		View resultView = convertView;
		ViewHolder holder;
		if(resultView == null){
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			resultView = inflater.inflate(R.layout.players_list_item, parent, false);
			holder = new ViewHolder(resultView);
			resultView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Player player = mPlayers.get(position);
		mGravatar.resolve(getContext(), player.getEmailHash(), 0, 
				new GravatarResolver.SimpleOnResolveListener(holder.playerPhoto));
		holder.playerNick.setText(String.valueOf(position + 1) + ". " + player.getNick());
		holder.rating.setText(String.valueOf(player.getRating()));
		holder.totalPlayed.setText(String.valueOf(player.getTotalPlayed()));
		holder.totalWon.setText(String.valueOf(player.getTotalWon()));
		
		return resultView;
	}
	
	private class ViewHolder{
		ImageView playerPhoto;
		TextView playerNick;
		TextView rating;
		TextView totalPlayed;
		TextView totalWon;
		
		public ViewHolder(View view) {
			playerPhoto = (ImageView) view.findViewById(R.id.player_photo);
			playerNick = (TextView) view.findViewById(R.id.nick);
			rating = (TextView) view.findViewById(R.id.rating);
			totalPlayed = (TextView) view.findViewById(R.id.total_played);
			totalWon = (TextView) view.findViewById(R.id.total_won);
			
		}
	}
}