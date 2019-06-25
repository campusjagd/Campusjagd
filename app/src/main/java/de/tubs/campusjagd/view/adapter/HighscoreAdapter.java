package de.tubs.campusjagd.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Player;
import de.tubs.campusjagd.model.Resources;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreViewHolder> {

    List<Player> playerList;

    public HighscoreAdapter(List<Player> playerList) {
        this.playerList = playerList;
    }


    @NonNull
    @Override
    public HighscoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_highscore_player, parent, false);

        return new HighscoreViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HighscoreViewHolder holder, int i) {
        holder.playerName.setText(playerList.get(i).getName());
        holder.playerScore.setText(playerList.get(i).getPoints());
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void updatePlayerList(List<Player> newPlayerList) {
        playerList = newPlayerList;
        this.notifyDataSetChanged();
    }
}

class HighscoreViewHolder extends RecyclerView.ViewHolder{

    TextView playerName;
    TextView playerScore;

    public HighscoreViewHolder(@NonNull View itemView) {
        super(itemView);

        playerName = itemView.findViewById(R.id.highscore_player_name);
        playerScore = itemView.findViewById(R.id.highscore_player_points);
    }


}