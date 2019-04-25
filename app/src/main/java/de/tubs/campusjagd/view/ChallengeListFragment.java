package de.tubs.campusjagd.view;

import android.content.Intent;
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

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.barcode.BarcodeCaptureActivity;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.view.adapter.ChallengeListAdapter;

/**
 * Fragment for the challenge list
 * shows all challenges
 *
 * @author leon.brettin@tu-bs.de
 */
public class ChallengeListFragment extends Fragment {

    // Objects for the challenge lists
    RecyclerView mChallengeRecycleView;
    ChallengeListAdapter mAdapter;

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
    private void init(View view) {
        //TODO Change this mock
        Resources resources = Resources.getInstance(view.getContext());
        List<Challenge> challengeList = resources.getAllChallenges();
        // ------

        mChallengeRecycleView = view.findViewById(R.id.challenge_recyclerView);
        FloatingActionButton fab = view.findViewById(R.id.challenge_list_fab);

        // Set up recyclerview for challenges
        mChallengeRecycleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        mChallengeRecycleView.setLayoutManager(llm);

        // Set up adapter
        mAdapter = new ChallengeListAdapter(challengeList, view.getContext(), mChallengeRecycleView);
        // Bind adapter
        mChallengeRecycleView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.challenge_list_fab) {
                    // launch barcode activity.
                    Intent intent = new Intent(ChallengeListFragment.this.getActivity(), BarcodeCaptureActivity.class);

                    startActivityForResult(intent, RC_BARCODE_CAPTURE);
                }
            }
        });
    }
}
