package de.tubs.campusjagd.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import de.tubs.campusjagd.Database.DatabaseHelperRoom;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.barcode.BarcodeCaptureActivity;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.gps.CJLocationManager;
import de.tubs.campusjagd.gps.GPSCalculator;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.GPS;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;
import de.tubs.campusjagd.view.adapter.ChallengeListAdapter;

import static android.support.constraint.Constraints.TAG;

/**
 * Fragment for the challenge list
 * shows all challenges
 *
 * @author leon.brettin@tu-bs.de
 */
public class ChallengeListFragment extends Fragment implements CJLocationManager.LocationCallback {

    // Objects for the challenge lists
    RecyclerView mChallengeRecycleView;
    ChallengeListAdapter mAdapter;
    // The popup button with our 2 options on verifying a found room
    PopupMenu mPopup;
    // Location manager to check for gps
    CJLocationManager mLocationManager;
    // Timer for the time challenges
    CountDownTimer mTimer;

    private static final int RC_BARCODE_CAPTURE = 9001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_list, container, false);

        this.init(view);

        return view;
    }

    /**
     * Initiates the view
     * @param view Holding view
     */
    private void init(final View view) {
        //TODO Change this mock
        Resources resources = Resources.getInstance(view.getContext());
        List<Challenge> challengeList = resources.getAllChallenges();

        List<Challenge> challengeListWithoutFullRooms = new LinkedList<>();
        for (Challenge challenge : challengeList) {
            if (!this.isFinishedChallenge(challenge)) {
                challengeListWithoutFullRooms.add(challenge);
            }
        }
        // ------

        mChallengeRecycleView = view.findViewById(R.id.challenge_recyclerView);
        FloatingActionButton fab = view.findViewById(R.id.challenge_list_fab);

        // Set up recyclerview for challenges
        mChallengeRecycleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        mChallengeRecycleView.setLayoutManager(llm);

        // Set up adapter
        mAdapter = new ChallengeListAdapter(challengeListWithoutFullRooms, view.getContext(), mChallengeRecycleView);
        // Bind adapter
        mChallengeRecycleView.setAdapter(mAdapter);

        // Set up location manager
        mLocationManager = new CJLocationManager();

        // Set popup menu
        mPopup = new PopupMenu(view.getContext(), fab);
        /* Reflection "hack" to show menu icons on the popup field
         * found on :
         * https://readyandroid.wordpress.com/popup-menu-with-icon/
         */
        try {
            Field[] fields = mPopup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(mPopup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            Logger.LogExeption(ChallengeListFragment.class.getSimpleName(), "Unable to init popup", e);
        }
        MenuInflater inflater = mPopup.getMenuInflater();
        inflater.inflate(R.menu.popup_button, mPopup.getMenu());

        mPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_start_location){
                    mLocationManager.getLocation(ChallengeListFragment.this.getActivity(), ChallengeListFragment.this);
                } else if (item.getItemId() == R.id.menu_start_QR){
                    // Launch QR-Activity
                    Intent intent = new Intent(ChallengeListFragment.this.getActivity(), BarcodeCaptureActivity.class);
                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                }
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.challenge_list_fab) {
                    // Show popup with options
                    mPopup.show();
                }
            }
        });

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    Toast.makeText(this.getContext(), R.string.barcode_success_toast,Toast.LENGTH_LONG).show();
                    //Toast.makeText(this.getContext(), "Barcode read: " + barcode.displayValue ,Toast.LENGTH_LONG).show();
                    // Give to Resources
                    Resources.getInstance(ChallengeListFragment.this.getContext()).handleBarcodeRead(barcode.displayValue);
                    mAdapter.updateChallenges(Resources.getInstance(ChallengeListFragment.this.getContext()).getAllChallenges());
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Logger.Log(TAG, "Error while parsing");
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();

        mTimer = new CountDownTimer(1000 * 60 /*seconds*/ * 60 /*minutes*/, 60 * 60 /* every minute*/) {
            @Override
            public void onTick(long millisUntilFinished) {
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                ChallengeListFragment.this.onResume();
            }
        };
        mTimer.start();

        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.menu_main);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }
    }

    @Override
    public void onNewLocationAvailable(GPS location) {
        Resources resources = Resources.getInstance(ChallengeListFragment.this.getContext());
        List<Room> nearbyRooms = GPSCalculator.checkPositionAgainstRoomList(location,
                resources.getAllRoomsNotFoundYet());

        // Add rooms depending on how many we found
        if (nearbyRooms.size() == 0) {
            Toast.makeText(this.getContext(), R.string.location_check_no_room_found,Toast.LENGTH_LONG).show();
        } else if (nearbyRooms.size() == 1) {
            Room room = nearbyRooms.get(0);

            resources.handleRoomFound(room);

            String answer = getString(R.string.location_check_one_room_found, room.getName());
            Toast.makeText(this.getContext(), answer,Toast.LENGTH_LONG).show();
            this.onResume();
        } else if (nearbyRooms.size() > 1) {
            String roomAnswer = "";

            for (Room room : nearbyRooms) {
                resources.handleRoomFound(room);
                roomAnswer += room.getName() + " ";
            }

            String answer = getString(R.string.location_check_more_rooms_found, roomAnswer);
            Toast.makeText(this.getContext(), answer,Toast.LENGTH_LONG).show();
            this.onResume();
        }
    }

    /**
     * Check if a challenge is already finished
     * @param challenge Challenge to check
     * @return True, if the challenge is already finished
     *         False, if not
     */
    private boolean isFinishedChallenge(Challenge challenge){
        for (Room room : challenge.getRoomList()) {
            if (!room.isRoomFound()) {
                return false;
            }
        }
        return true;
    }
}
