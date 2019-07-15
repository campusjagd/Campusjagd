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

import java.util.Date;
import java.util.List;

import de.tubs.campusjagd.R;
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
    public void onBindViewHolder(@NonNull ChallengeCreateViewHolder holder, final int position) {
        final Challenge challenge = mChallenges.get(position);

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

        //Need to use the getName function of the challenge object to transfer the challenge,
        //otherwise all the p2pFragment gets is a memory address, with which no one can do
        //anything
        final String challengeStringRepresentation = mChallenges.get(position).getName();

        // Prepare sendFab to start peer to peer connection
        holder.sendFab.setOnClickListener(new View.OnClickListener() {
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

        holder.deleteFab.setOnClickListener(new View.OnClickListener() {
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
                                Resources.getInstance(mContext).deleteChallenge(challenge.getName());
                                ChallengeCreateListAdapter.this.mChallenges.remove(position);
                                ChallengeCreateListAdapter.this.notifyItemRemoved(position);
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        if (challenge.isTimedChallenge()) {
            Date challengeEndTime = new Date(challenge.getEndTimeStamp());

            holder.timeNeeded.setText(ChallengeListAdapter.hmsTimeFormatter(challengeEndTime.getTime()));
            holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else {
            holder.timeNeeded.setVisibility(View.GONE);
            holder.background.setBackgroundColor(mContext.getResources().getColor(R.color.white));
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
class ChallengeCreateViewHolder extends RecyclerView.ViewHolder{

    TextView challengeName;
    View hiddenView;
    RecyclerView recyclerView;
    FloatingActionButton sendFab;
    FloatingActionButton deleteFab;
    TextView timeNeeded;
    View background;

    public ChallengeCreateViewHolder(@NonNull View itemView) {
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
