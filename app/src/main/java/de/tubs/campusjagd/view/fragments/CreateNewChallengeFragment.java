package de.tubs.campusjagd.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.tubs.campusjagd.MainActivity;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.RoomAdapter_SelectableCheckboxes;

/**
 * Fragment to create a new challenge by selecting rooms
 *
 * @author leon.brettin@tu-bs.de
 */
public class CreateNewChallengeFragment extends Fragment {

    //Resources to save and get data from
    Resources mResources;

    // EditText view holding the challenge name
    TextView mChallengeNameEditText;
    // Room Adapter for the selectable rooms
    RoomAdapter_SelectableCheckboxes mRoomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_challenge, container, false);

        this.init(view);

        return view;
    }

    /**
     * Inits the view
     * @param view The holding view
     */
    private void init(View view) {
        // Init Resources
        mResources = Resources.getInstance(view.getContext());
        List<Room> roomList = mResources.getAllRooms();

        // Get Fab and Recyclerview
        FloatingActionButton fab = view.findViewById(R.id.create_challenge_fab);
        RecyclerView recyclerView = view.findViewById(R.id.create_challenge_recyclerView);
        mChallengeNameEditText = view.findViewById(R.id.create_challenge_editText);

        // Set up Recyclerview
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(llm);
        mRoomAdapter = new RoomAdapter_SelectableCheckboxes(roomList);
        recyclerView.setAdapter(mRoomAdapter);

        // Set up fab
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mResources.checkIfChallengeNameAvailable(mChallengeNameEditText.getText().toString())) {
                    Toast.makeText(CreateNewChallengeFragment.this.getContext(), "Challenge Name schon vorhanden.", Toast.LENGTH_LONG).show();
                    return;
                }
                // Create a new challenge and save it
                CreateNewChallengeFragment.this.createNewChallenge();

                // Go back to the previous fragment
                try {
                    CreateNewChallengeFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                } catch (NullPointerException e) {
                    Logger.LogExeption(CreateNewChallengeFragment.class.getSimpleName(), "Error while going back to previous class", e);

                    // We dont want our app to get stuck so we start the main activity (should not happen but just to be sure)
                    MainActivity.startActivty(v.getContext());
                }

            }
        });
    }

    /**
     * Saves a new challenge into the resources
     */
    private void createNewChallenge() {
        // Get values
        String challengeName = mChallengeNameEditText.getText().toString();
        List<Room> roomList = new ArrayList<>(mRoomAdapter.getSelectedRooms());
        Challenge createdChallenge =  new Challenge(challengeName, roomList, Calendar.getInstance().getTime().toString(), false);

        mResources.saveChallenge(createdChallenge);

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.create_new_challenge_header);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }
    }
}
