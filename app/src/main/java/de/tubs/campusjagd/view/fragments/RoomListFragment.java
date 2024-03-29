package de.tubs.campusjagd.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    private ExtendedRoomAdapter mRoomAdapter;

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
        //Calls the class resources, which it self calls the databasehelper to get all
        //the rooms entered in the databse
        Resources resources = Resources.getInstance(view.getContext());
        mRoomList = resources.getAllRooms();

        // Setup room list
        RecyclerView roomRecyclerView = view.findViewById(R.id.roomfragment_recyclerview);
        roomRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        roomRecyclerView.setLayoutManager(llm);

        //Set up adapter
        // We reuse the adapter from the challenge list here. If we need more special styling we can create a new one.
        mRoomAdapter = new ExtendedRoomAdapter(mRoomList, RoomListFragment.this.getActivity());
        roomRecyclerView.setAdapter(mRoomAdapter);

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
                            .addToBackStack(CreateNewRoomFragment.class.getSimpleName())
                            .commit();

                } catch (NullPointerException e) {
                    Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Error while starting new Fragment", e);
                }
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        mRoomAdapter.notifyDataSetChanged();

        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.menu_roomlist);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }
    }
}
