package de.tubs.campusjagd.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.view.fragments.ChallengeListFragment;

/**
 * Adapter holding our challenges
 *
 * @author leon.brettin@tu-bs.de
 */
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
    private int mExpandedPosition = -1;

    /**
     * The timer string to insert
     */
    private String mTimerString;

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

    /**
     * Method which will be called when the view should be created.
     *
     * @param parent Parent view
     * @param i We dont need this but its necessary for the API
     * @return Inflated view
     */
    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_card, parent, false);

        return new ChallengeViewHolder(v);
    }

    /**
     * Binds the values of our challenge list with our logic.
     * We are binding attributes here and implementing the popup logic
     *
     * @param holder View holding our items
     * @param position Position of the view in our list
     */
    @Override
    public void onBindViewHolder(@NonNull final ChallengeViewHolder holder, final int position) {
        final Challenge challenge = mChallenges.get(position);

        // Set name for challenge in view
        holder.challengeName.setText(challenge.getName());

        //Add rooms to roomlist
        // Set up recyclerview for rooms
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        holder.challengeRooms.setLayoutManager(llm);
        // Bind adapter

        holder.background.setBackgroundColor(mContext.getResources().getColor(
                challenge.isTimedChallenge()
                        ?   R.color.colorPrimary
                        :   R.color.white
                ));


        holder.challengeRooms.setAdapter(new RoomAdapter(challenge.getRoomList()));


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

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
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
                                ChallengeListAdapter.this.mChallenges.remove(position);
                                ChallengeListAdapter.this.notifyItemRemoved(position);
                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();

            }
        });

        holder.redoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(challenge.isTimedChallenge() ?
                        R.string.challenge_list_redo_button_pressed_is_already_time_challenge
                        : R.string.challenge_list_redo_button_pressed)
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        })
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Resources.getInstance(mContext).setChallengeTimedOrUntimed(challenge, !challenge.isTimedChallenge());

                                ChallengeListAdapter.this.mChallenges.get(position).setTimedChallenge(!challenge.isTimedChallenge());
                                ChallengeListAdapter.this.notifyItemChanged(position);

                                challenge.setTimedChallenge(!challenge.isTimedChallenge());

                            }
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
            }
        });

        if (!challenge.isTimedChallenge()) {
            holder.timerTextView.setVisibility(View.GONE);
        } else {
            updateTime(challenge);
            holder.timerTextView.setText(mTimerString);
        }

    }

    @Override
    public int getItemCount() {
        return mChallenges.size();
    }

    private void updateTime(Challenge challenge) {

            Date challengeStartDate = new Date(challenge.getTimestamp());
            Date now = new Date();
            long timeDifference_in_millis = now.getTime() - challengeStartDate.getTime();
            long timeDifference_in_seconds = timeDifference_in_millis / 1000;

            long timeToWorkWith = timeDifference_in_seconds;
            long seconds = timeToWorkWith % 60;
            timeToWorkWith/= 60;
            long minutes = timeToWorkWith % 60;
            timeToWorkWith/= 60;
            long hours = timeToWorkWith % 24;
            timeToWorkWith /= 24;
            long days = timeToWorkWith;

            mTimerString = hmsTimeFormatter(timeDifference_in_millis);
    }

    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    @SuppressLint("DefaultLocale")
    private String hmsTimeFormatter(long milliSeconds) {

        String hms;

        long days = TimeUnit.MILLISECONDS.toDays(milliSeconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliSeconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliSeconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds));

        if (days != 0) {
            hms = String.format("%02d:%02d:%02d",
                    days,
                    hours,
                    minutes);
        } else if (hours != 0) {
            hms = String.format("%02d:%02d",
                    hours,
                    minutes);
        } else {
            hms = String.format("%02d",
                    minutes) + " Minuten";
        }

        return hms;


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

    // The RecyclerView holding all rooms
    RecyclerView challengeRooms;

    // Delete Button
    ImageView deleteButton;

    // Redo Button
    ImageView redoButton;

    // The timer
    TextView timerTextView;

    // The background
    View background;

    /**
     * Viewholder for a challenge item
     * @param itemView View holding the item
     */
    ChallengeViewHolder(@NonNull View itemView) {
        super(itemView);

        challengeName = itemView.findViewById(R.id.challenge_name);
        hiddenView = itemView.findViewById(R.id.hiddenView);
        challengeRooms = itemView.findViewById(R.id.challenge_item_recyclerView);
        deleteButton = itemView.findViewById(R.id.challenge_card_delete);
        redoButton = itemView.findViewById(R.id.challenge_card_redo);
        timerTextView = itemView.findViewById(R.id.challenge_card_timer);
        background = itemView.findViewById(R.id.challenge_card_layout);
    }

}