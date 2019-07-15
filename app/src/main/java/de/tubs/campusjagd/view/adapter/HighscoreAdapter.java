package de.tubs.campusjagd.view.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Player;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreViewHolder> {

    List<Player> playerList;

    // The name of the player of this device
    String userPlayername;

    int itemBackgroundResourceId;

    public HighscoreAdapter(List<Player> playerList, String userPlayername, int color) {
        this.playerList = playerList;
        this.userPlayername = userPlayername;
        this.itemBackgroundResourceId = color;
    }


    @NonNull
    @Override
    public HighscoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_highscore_player, parent, false);

        return new HighscoreViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HighscoreViewHolder holder, int i) {
        holder.playerName.setText(playerList.get(i).getName());
        holder.playerScore.setText(Integer.toString(playerList.get(i).getPoints()));

        if (!playerList.get(i).getName().equals(userPlayername)) {
            holder.layout.setBackgroundColor(itemBackgroundResourceId);
        }
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
    ConstraintLayout layout;

    public HighscoreViewHolder(@NonNull View itemView) {
        super(itemView);

        playerName = itemView.findViewById(R.id.highscore_player_name);
        playerScore = itemView.findViewById(R.id.highscore_player_points);
        layout = itemView.findViewById(R.id.item_highscore_layout);
    }


}