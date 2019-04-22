package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.ResourceMock;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.RoomAdapter;
import de.tubs.campusjagd.view.adapter.RoomAdapter_SelectableCheckboxes;

/**
 * Fragment to create a new challenge by selecting rooms
 */
public class CreateNewChallengeFragment extends Fragment {

    //Resources to save and get data from
    ResourceMock mResources;

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
        mResources = new ResourceMock(view.getContext());
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
                CreateNewChallengeFragment.this.createNewChallenge();

                CreateNewChallengeFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
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
        Challenge createdChallenge =  new Challenge(challengeName, roomList);

        // Save to resources
        mResources.saveChallenge(createdChallenge);
    }
}
