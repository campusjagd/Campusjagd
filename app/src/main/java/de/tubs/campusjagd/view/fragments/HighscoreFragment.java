package de.tubs.campusjagd.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;

public class HighscoreFragment extends Fragment {

    TextView mOwnNameTextView;
    TextView mOwnPointsTextView;

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

        Resources resources = Resources.getInstance(view.getContext());

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
    }
}
