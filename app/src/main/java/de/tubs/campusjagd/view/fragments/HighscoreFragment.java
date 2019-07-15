package de.tubs.campusjagd.view.fragments;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Player;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.HighscoreAdapter;

public class HighscoreFragment extends Fragment {

    TextView mOwnNameTextView;
    TextView mOwnPointsTextView;
    RecyclerView mHighscoreRecyclerView;
    HighscoreAdapter mHighscoreAdapter;
    ConstraintLayout mSeperationBar;
    ConstraintLayout mOwnPlayerBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_highscore, container, false);

        this.init(view);

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init(View view) {
        mOwnNameTextView = view.findViewById(R.id.highscore_own_name);
        mOwnPointsTextView = view.findViewById(R.id.highscore_own_points);
        mSeperationBar = view.findViewById(R.id.highscore_seperation_bar);
        mOwnPlayerBar = view.findViewById(R.id.highscore_own_player_bar);

        Resources resources = Resources.getInstance(view.getContext());
        List<Player> highscorePlayer = new ArrayList<>();
        try{
            highscorePlayer = Resources.getInstance(view.getContext()).getTopTenPlayers();
        } catch (SQLiteException exception) {
            Logger.LogExeption(HighscoreFragment.class.getSimpleName(), "Unable to initialize playerlist", exception);
        }

        // Dont show the highscore list at all
        if (highscorePlayer.isEmpty()) {
            mSeperationBar.setVisibility(View.GONE);
            this.setOfflineHighscore(resources);
            return;
        }

        boolean userIsInHighscore = false;
        for (Player player : highscorePlayer) {
            if (player.getName().equals(resources.getUserName())) {
                userIsInHighscore = true;
            }
        }


        if (userIsInHighscore) {
            mSeperationBar.setVisibility(View.GONE);
            mOwnPlayerBar.setVisibility(View.GONE);
            this.setOnlineHighscore(view, highscorePlayer, resources);

        } else {
            this.setOfflineHighscore(resources);
            this.setOnlineHighscore(view,highscorePlayer,resources);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.menu_highscore);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }

        // Refresh the playerlist
        try {
            if (mHighscoreAdapter != null) {
                mHighscoreAdapter.updatePlayerList(Resources.getInstance(this.getContext()).getTopTenPlayers());

            }
        } catch (SQLiteException exception) {
            Logger.LogExeption(HighscoreFragment.class.getSimpleName(), "Unable to update playerlist", exception);
        }
    }

    private void setOfflineHighscore(Resources resources){
        mOwnNameTextView.setText(resources.getUserName());

        int points = 0;
        for (Room room : resources.getAllRooms()) {
            if (room.isRoomFound()) {
                points += room.getPoints();
            }
        }
        resources.setUserScore(points);
        mOwnPointsTextView.setText(Integer.toString(points));
    }

    private void setOnlineHighscore(View view, List<Player> highscorePlayer, Resources resources){
        // Start adapter
        mHighscoreRecyclerView = view.findViewById(R.id.highscore_all_points);
        mHighscoreRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        mHighscoreRecyclerView.setLayoutManager(llm);



        mHighscoreAdapter = new HighscoreAdapter(highscorePlayer, resources.getUserName(), getContext().getResources().getColor(R.color.white));
        mHighscoreRecyclerView.setAdapter(mHighscoreAdapter);
    }
}
