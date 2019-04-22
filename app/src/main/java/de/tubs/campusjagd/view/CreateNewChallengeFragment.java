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

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.ResourceMock;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.RoomAdapter;
import de.tubs.campusjagd.view.adapter.RoomAdapter_SelectableCheckboxes;

public class CreateNewChallengeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_challenge, container, false);

        this.init(view);

        return view;
    }

    private void init(View view) {
        ResourceMock resourceMock = new ResourceMock(view.getContext());
        List<Room> roomList = resourceMock.getAllRooms();

        FloatingActionButton fab = view.findViewById(R.id.create_challenge_fab);
        RecyclerView recyclerView = view.findViewById(R.id.create_challenge_recyclerView);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(llm);

        RoomAdapter_SelectableCheckboxes roomAdapter = new RoomAdapter_SelectableCheckboxes(roomList);
        recyclerView.setAdapter(roomAdapter);
    }
}
