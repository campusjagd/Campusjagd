package de.tubs.campusjagd.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;

public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeViewHolder> {

    /**
     * List of all challenges which should be displayed
     */
    private List<Challenge> mChallenges;

    /**
     * The {@link Context}
     */
    private Context mContext;

    /**
     * The holding recyclerView
     */
    private RecyclerView mRecyclerView;

    /**
     * The expanded position
     */
    protected int mExpandedPosition = -1;

    /**
     * Constructor for the adaper
     *
     * @param challengesToDisplay All Challenges which should be displayed
     * @param context The context of the holding activity/fragment
     * @param holdingRecyclerView The holding recyclerview
     */
    public ChallengeListAdapter(List <Challenge> challengesToDisplay, Context context, RecyclerView holdingRecyclerView) {
        mChallenges = challengesToDisplay;
        mContext = context;
        mRecyclerView = holdingRecyclerView;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_card, parent, false);

        return new ChallengeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, final int position) {
        Challenge challenge = mChallenges.get(position);

        // Set name for challenge in view
        holder.challengeName.setText(challenge.getName());

        // Expansion of challenge when you click on it
        final boolean isExpanded = position == mExpandedPosition;
        holder.hiddenView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;

                // You can add a transition later with (But you have to implement stuff here later
                //TransitionManager.beginDelayedTransition(mRecyclerView);

                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mChallenges.size();
    }
}

/**
 * Viewholder class for the challenge
 */
class ChallengeViewHolder extends RecyclerView.ViewHolder {

    // Name of the challenge
    TextView challengeName;

    // Expandable block
    View hiddenView;

    /**
     * Viewholder for a challenge item
     * @param itemView View holding the item
     */
    ChallengeViewHolder(@NonNull View itemView) {
        super(itemView);

        challengeName = itemView.findViewById(R.id.challenge_name);
        hiddenView = itemView.findViewById(R.id.hiddenView);
    }
}