package de.tubs.campusjagd.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.tubs.campusjagd.R;

public class ChallengeTransferFragment extends Fragment {

    /* TODO Figure out, if this Fragment is necessary

        May be obsolete, because the activity startet by the p2pFragment will, most likely, to be
        capable of sending and receiving messages. The only requirement for the receiving end
        is, that the phone has to be unlocked in order to receive messages
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_transfer, container, false);

        this.init(view);

        return view;
    }


    private void init(View view) {

    }
}
