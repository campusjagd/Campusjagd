package de.tubs.campusjagd.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;

/**
 * Fragment to send a challenge to another app
 *
 */
public class PeerToPeerFragment extends Fragment {

    public static final String BUNDLEKEY = "BUNDLEKEY";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peer_to_peer, container, false);

        String challengeString = getArguments().getString(BUNDLEKEY);

        this.init(view, challengeString);

        return view;
    }

    private void init(View view, String challengeString) {
        /*TODO start new nfc activitiy from here, where the chellenge is send and received on the other end.
            The new activity gets the name of the challenge and all the rooms associated with it
            (possibly all the information for every room will be included in the send message, or
            the server will be used to get all the rooms information on the receiving end).
            The receiver then just inserts the received challenge in his local database. Maybe even upload it
            to the server.

          TODO add permissions for nfc to the android manifest.

          TODO add new activity
        */
        Logger.Log(PeerToPeerFragment.class.getSimpleName(), "Challenge String: " + challengeString);

    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.waiting_for_ptp_header);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }
    }
}
