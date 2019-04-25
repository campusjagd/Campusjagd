package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.ExtendedRoomAdapter;

/**
 * Fragment for the List of rooms.
 *
 * @author leon.brettin@tu-bs.de
 */
public class RoomListFragment extends Fragment {

    // List of all rooms which should be displayed
    List<Room> mRoomList;

    //Fragment to start when a new room should be created
    private Fragment mCreateNewRoomFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roomlist, container, false);

        this.init(view);

        return view;
    }

    /**
     * Initiates the fragment by adding values to the recyclerview
     *
     * @param view View holding the fragment
     */
    private void init(View view) {
        //TODO replace this later
        Resources resources = Resources.getInstance(view.getContext());
        mRoomList = resources.getAllRooms();
        //-----

        // Setup room list
        RecyclerView roomRecyclerView = view.findViewById(R.id.roomfragment_recyclerview);
        roomRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        roomRecyclerView.setLayoutManager(llm);

        //Set up adapter
        // We reuse the adapter from the challenge list here. If we need more special styling we can create a new one.
        ExtendedRoomAdapter roomAdapter = new ExtendedRoomAdapter(mRoomList, RoomListFragment.this.getActivity());
        roomRecyclerView.setAdapter(roomAdapter);

        // Set up fragment
        mCreateNewRoomFragment = new CreateNewRoomFragment();

        //Set up fab
        FloatingActionButton fab = view.findViewById(R.id.roomfragment_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FragmentTransaction transaction = RoomListFragment.this.getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contentHolder, mCreateNewRoomFragment)
                            .addToBackStack(ChallengeCreateListFragment.class.getSimpleName())
                            .commit();

                } catch (NullPointerException e) {
                    Logger.LogExeption(ChallengeCreateListFragment.class.getSimpleName(), "Error while starting new Fragment", e);
                }
            }
        });


    }
}
