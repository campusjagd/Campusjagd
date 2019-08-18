package de.tubs.campusjagd.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.nfc.SenderActivity;

/**
 * Adapter holding all challenges which should be created.
 * I used a own adapter instead of extending it from {@link ChallengeListAdapter} because the
 * functionality differs to much.
 *
 * @author leon.brettin@tu-bs.de
 */
public class ChallengeCreateListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    @Override
    public int getItemViewType(int position) {
        return mChallenges.get(position).isTimedChallenge() ? 1 : 0;
    }

    private int getItemPosition(Challenge challenge) {
        return mChallenges.indexOf(challenge);
    }


    @NonNull
    @Override
    public ChallengeCreateViewHolderNoTimeChallenge onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_create_card_item, parent, false);

        switch (viewtype){
            case 0:
                return new ChallengeCreateViewHolderNoTimeChallenge(v);
            case 1:
                return new ChallengeCreateViewHolderTimeChallenge(v);
        }

        throw new Error("You forgot to implement the new type of viewholder here");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Challenge challenge = mChallenges.get(position);
        ChallengeCreateViewHolderNoTimeChallenge viewHolder;

        switch (holder.getItemViewType()) {
            case 0:
                 viewHolder = (ChallengeCreateViewHolderNoTimeChallenge)holder;
                 viewHolder.timeNeeded.setVisibility(View.GONE);
                 viewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                 break;
            default:
                viewHolder = (ChallengeCreateViewHolderTimeChallenge)holder;
                viewHolder.timeNeeded.setText(ChallengeListAdapter.formatTime(challenge));
                viewHolder.background.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        }

        viewHolder.challengeName.setText(challenge.getName());

        //Add rooms to roomlist
        // Set up recyclerview for rooms
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        viewHolder.recyclerView.setLayoutManager(llm);
        viewHolder.recyclerView.setHasFixedSize(false);
        // Bind adapter
        viewHolder.recyclerView.setAdapter(new RoomAdapter_NoCheckbox(challenge.getRoomList()));

        // Expansion of challenge when you click on it
        final boolean isExpanded = position == mExpandedPosition;
        viewHolder.hiddenView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        final Challenge itemChallenge = challenge;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : getItemPosition(challenge);

                // You can add a transition later with (But you have to implement stuff here later
                //TransitionManager.beginDelayedTransition(mRecyclerView);
                notifyItemChanged(getItemPosition(challenge));
            }
        });

        //Need to use the getName function of the challenge object to transfer the challenge,
        //otherwise all the p2pFragment gets is a memory address, with which no one can do
        //anything
        final String challengeStringRepresentation = mChallenges.get(position).getName();

        // Prepare sendFab to start peer to peer connection
        viewHolder.sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
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
                        */

                //starting the sender activity, to send a challenge via nfc to a peer
                Intent intent = new Intent(mContext, SenderActivity.class);
                intent.putExtra("ChallengeName", challengeStringRepresentation);
                mContext.startActivity(intent);
            }
        });

        viewHolder.deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.challenge_list_delete_button_pressed)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Resources.getInstance(mContext).deleteChallenge(challenge.getName());
                                    ChallengeCreateListAdapter.this.mChallenges.remove(position);
                                    ChallengeCreateListAdapter.this.notifyItemRemoved(position);
                                } catch (IndexOutOfBoundsException e) {
                                    Logger.LogExeption(ChallengeCreateListAdapter.class.getSimpleName(), "Unable to remove last element", e);
                                }

                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        if (challenge.isTimedChallenge()) {

        } else {

        }
    }


    @Override
    public int getItemCount() {
        return mChallenges.size();
    }
}

/**
 * Viewholder for the challenge create fragment
 */
class ChallengeCreateViewHolderNoTimeChallenge extends RecyclerView.ViewHolder{

    TextView challengeName;
    View hiddenView;
    RecyclerView recyclerView;
    FloatingActionButton sendFab;
    FloatingActionButton deleteFab;
    TextView timeNeeded;
    View background;

    public ChallengeCreateViewHolderNoTimeChallenge(@NonNull View itemView) {
        super(itemView);

        challengeName = itemView.findViewById(R.id.challenge_create_challenge_name);
        hiddenView = itemView.findViewById(R.id.challenge_create_hiddenView);
        recyclerView = itemView.findViewById(R.id.challenge_create_recyclerview);
        sendFab = itemView.findViewById(R.id.challenge_create_fab);
        deleteFab = itemView.findViewById(R.id.challenge_delete_fab);
        timeNeeded = itemView.findViewById(R.id.challenge_create_challenge_time);
        background = itemView.findViewById(R.id.challenge_create_challenge_item_background);
    }
}

class ChallengeCreateViewHolderTimeChallenge extends ChallengeCreateViewHolderNoTimeChallenge {

    public ChallengeCreateViewHolderTimeChallenge(@NonNull View itemView) {
        super(itemView);
    }
}
