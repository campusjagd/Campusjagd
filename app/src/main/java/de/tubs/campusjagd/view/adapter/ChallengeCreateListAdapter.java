package de.tubs.campusjagd.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.view.fragments.PeerToPeerFragment;

/**
 * Adapter holding all challenges which should be created.
 * I used a own adapter instead of extending it from {@link ChallengeListAdapter} because the
 * functionality differs to much.
 *
 * @author leon.brettin@tu-bs.de
 */
public class ChallengeCreateListAdapter extends RecyclerView.Adapter<ChallengeCreateViewHolder> {

    /**
     * List of all challenges
     */
    private List<Challenge> mChallenges;

    /**
     * The context
     */
    private Context mContext;

    /**
     * The expanded position
     */
    private int mExpandedPosition = -1;

    /**
     * The holding activity
     */
    private Fragment mFragment;

    /**
     * Basic constructor
     * @param challenges List of challenges to be shown
     * @param fragment The {@link Activity}
     */
    public ChallengeCreateListAdapter (List<Challenge> challenges, Fragment fragment) {
        mChallenges = challenges;
        mContext = fragment.getContext();
        mFragment = fragment;
    }

    /**
     * Adds a new list of challenges to the list
     * The old list will be overwritten
     * @param challenges New list of challenges to add
     */
    public void exchangeAllChallenges(List<Challenge> challenges){
        mChallenges = challenges;
        this.notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ChallengeCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_create_card_item, parent, false);

        return new ChallengeCreateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeCreateViewHolder holder, int position) {
        Challenge challenge = mChallenges.get(position);

        holder.challengeName.setText(challenge.getName());

        //Add rooms to roomlist
        // Set up recyclerview for rooms
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        holder.recyclerView.setLayoutManager(llm);
        holder.recyclerView.setHasFixedSize(false);
        // Bind adapter
        holder.recyclerView.setAdapter(new RoomAdapter_NoCheckbox(challenge.getRoomList()));

        // Expansion of challenge when you click on it
        final boolean isExpanded = position == mExpandedPosition;
        holder.hiddenView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        final int itemposition = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : itemposition;

                // You can add a transition later with (But you have to implement stuff here later
                //TransitionManager.beginDelayedTransition(mRecyclerView);

                notifyItemChanged(itemposition);
            }
        });

        final String challengeStringRepresentation = mChallenges.get(position).toString();

        // Prepare fab to start peer to peer connection
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new fragment
                PeerToPeerFragment replaceFragment = new PeerToPeerFragment();

                // Give the fragment the challenge as bundle
                Bundle bundle = new Bundle();
                bundle.putString(PeerToPeerFragment.BUNDLEKEY, challengeStringRepresentation);
                replaceFragment.setArguments(bundle);

                // Start new fragment
                FragmentTransaction transaction = mFragment.getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contentHolder, replaceFragment)
                        .addToBackStack(PeerToPeerFragment.class.getSimpleName())
                        .commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mChallenges.size();
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
