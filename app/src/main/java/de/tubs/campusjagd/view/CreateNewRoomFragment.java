package de.tubs.campusjagd.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    }

    /**
     * Creates a new room and saves it in the resources
     * @return True, when all values are good and the room is saved successfully.
     */
    private boolean createNewRoom() {
        boolean successFull = true;

        // Get roomName
        String roomName = mNameEditText.getText().toString();
        if (roomName.equals("")){
            successFull = false;
            Toast.makeText(this.getContext(), R.string.create_room_no_name_error, Toast.LENGTH_LONG).show();
        }

        // Get points
        try {
            int points = Integer.parseInt(mPointsEditText.getText().toString());

        } catch (NumberFormatException e) {

            // If there is an error with the points, give the user a toast and clear the edit text
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Error while parsing points", e);
            Toast.makeText(this.getContext(), R.string.create_room_parse_point_error, Toast.LENGTH_LONG).show();
            mPointsEditText.getText().clear();

            successFull = false;
        }



        return successFull;
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
}
