package de.tubs.campusjagd.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import de.tubs.campusjagd.MainActivity;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.gps.CJLocationManager;
import de.tubs.campusjagd.model.GPS;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;

/**
 * Fragment to create a new room
 *
 * @author leon.brettin@tu-bs.de
 */
public class CreateNewRoomFragment extends Fragment {

    // Edit text holding the name of the room
    private EditText mNameEditText;
    // Text showing the
    private TextView mGPSText;
    // Actual found gps position
    private GPS mGPSPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_new_room, container, false);

        this.init(view);

        return view;
    }

    /**
     * Inits all elements of the fragment
     * @param view The holding {@link View}
     */
    private void init(View view) {

        // Get Elements out of the view
        FloatingActionButton fab = view.findViewById(R.id.create_room_fab);
        mNameEditText = view.findViewById(R.id.create_room_name_edit_text);
        Button gpsButton = view.findViewById(R.id.create_room_set_gps_button);
        mGPSText = view.findViewById(R.id.create_room_gps_string);

        // Set up the button to add a new room
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new room and save it
                if (CreateNewRoomFragment.this.createNewRoom()) {

                    // Go back to the previous fragment
                    try {
                        hideKeyboard(CreateNewRoomFragment.this.getActivity());

                        CreateNewRoomFragment.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                    } catch (NullPointerException e) {
                        Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Error while going back to previous class", e);

                        // We dont want our app to get stuck so we start the main activity (should not happen but just to be sure)
                        MainActivity.startActivty(v.getContext());
                    }
                }
            }
        });

        // Set up the GPS button
        gpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CJLocationManager locationManager = new CJLocationManager();
                locationManager.getLocation(CreateNewRoomFragment.this.getActivity(), new CJLocationManager.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(GPS location) {
                        mGPSPosition = location;

                        mGPSText.setText(mGPSPosition.toString());
                        mGPSText.setVisibility(View.VISIBLE);
                    }
                });

            }
        });
    }

    /**
     * Creates a new room and saves it in the resources
     * @return True, when all values are good and the room is saved successfully.
     */
    private boolean createNewRoom() {
        // Get roomName
        String roomName = mNameEditText.getText().toString();
        if (roomName.equals("")){
            Toast.makeText(this.getContext(), R.string.create_room_no_name_error, Toast.LENGTH_LONG).show();

            return false;
        }

        // Get points
        int points = 5;
        // We removed the point functionality to 5 points per room

        if (mGPSPosition == null) {
            Toast.makeText(this.getContext(), R.string.create_room_no_gps_error, Toast.LENGTH_LONG).show();

            return false;
        }

        // Every value is set up correctly
        Room room = new Room(null, mGPSPosition, roomName, points, Calendar.getInstance().getTime().toString(), false);
        Resources.getInstance(this.getContext()).saveRoom(room);

        return true;
    }

    /**
     * Method from:
     * https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
     * to hide the keyboard
     * @param activity The current activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.create_new_room_header);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }
    }
}
