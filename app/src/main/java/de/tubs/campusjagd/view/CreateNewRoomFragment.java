package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.tubs.campusjagd.MainActivity;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;

public class CreateNewRoomFragment extends Fragment {

    private EditText mPointsEditText;
    private EditText mNameEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_room, container, false);

        this.init(view);

        return view;
    }

    private void init(View view) {

        // Get Elements out of the view
        FloatingActionButton fab = view.findViewById(R.id.create_room_fab);
        mNameEditText = view.findViewById(R.id.create_room_name_edit_text);
        mPointsEditText = view.findViewById(R.id.create_room_points_edit_text);
        Button gpsButton = view.findViewById(R.id.create_room_set_gps_button);
        TextView gpsText = view.findViewById(R.id.create_room_gps_string);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new room and save it
                CreateNewRoomFragment.this.createNewRoom();

                // Go back to the previous fragment
                try {
                    CreateNewRoomFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                } catch (NullPointerException e) {
                    Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Error while going back to previous class", e);

                    // We dont want our app to get stuck so we start the main activity (should not happen but just to be sure)
                    MainActivity.startActivty(v.getContext());
                }
            }
        });
    }

    private void createNewRoom() {
        String roomName = mNameEditText.getText().toString();


    }
}
