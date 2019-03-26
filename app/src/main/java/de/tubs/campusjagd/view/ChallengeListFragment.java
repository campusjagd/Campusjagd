package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.ResourceMock;

/**
 * Fragment for the challenge list
 */
public class ChallengeListFragment extends Fragment {

    // Objects for the challenge lists
    RecyclerView mChallengeRecycleView;
    ChallengeListAdapter mAdapter;

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
        ResourceMock resources = new ResourceMock();
        List<Challenge> challengeList = resources.getAllChallenges();
        // ------

        mChallengeRecycleView = view.findViewById(R.id.challenge_recyclerView);

        // Set up recyclerview for challenges
        mChallengeRecycleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        mChallengeRecycleView.setLayoutManager(llm);

        // Set up adapter
        mAdapter = new ChallengeListAdapter(challengeList, view.getContext(), mChallengeRecycleView);
        // Bind adapter
        mChallengeRecycleView.setAdapter(mAdapter);
    }
}
