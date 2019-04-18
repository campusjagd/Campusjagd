package de.tubs.campusjagd.view.adapter;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tubs.campusjagd.R;

public class ChallengeCreateListAdapter extends RecyclerView.Adapter<ChallengeCreateViewHolder> {

    @NonNull
    @Override
    public ChallengeCreateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeCreateViewHolder challengeCreateViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

/**
 * Viewholder for the challenge create fragment
 */
class ChallengeCreateViewHolder extends RecyclerView.ViewHolder{

    TextView challengeName;
    View hiddenView;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    public ChallengeCreateViewHolder(@NonNull View itemView) {
        super(itemView);

        challengeName = itemView.findViewById(R.id.challenge_create_challenge_name);
        hiddenView = itemView.findViewById(R.id.challenge_create_hiddenView);
        recyclerView = itemView.findViewById(R.id.challenge_create_recyclerview);
        fab = itemView.findViewById(R.id.challenge_create_fab);
    }
}
