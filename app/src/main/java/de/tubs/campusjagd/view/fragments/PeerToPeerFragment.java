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
        // Do something ;-)
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
