package de.tubs.campusjagd.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Challenge;
import de.tubs.campusjagd.model.ResourceMock;
import de.tubs.campusjagd.view.adapter.ChallengeCreateListAdapter;
import de.tubs.campusjagd.view.adapter.ChallengeListAdapter;

public class ChallengeCreateListFragment extends Fragment {

    private Fragment mCreateNewChallengeFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge_create_list, container, false);

        this.init(view);

        return view;
    }

    private void init(final View view) {
        //TODO Change this mock
        ResourceMock resources = new ResourceMock(view.getContext());
        List<Challenge> challengeList = resources.getAllChallenges();
        // ------

        RecyclerView challengeRecycleView = view.findViewById(R.id.challenge_create_recyclerview);

        // Set up recyclerview for challenges
        challengeRecycleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(view.getContext());
        challengeRecycleView.setLayoutManager(llm);

        // Set up adapter
        ChallengeCreateListAdapter adapter = new ChallengeCreateListAdapter(challengeList, view.getContext());
        // Bind adapter
        challengeRecycleView.setAdapter(adapter);

        mCreateNewChallengeFragment = new CreateNewChallengeFragment();

        // Set up fab
        FloatingActionButton fab = view.findViewById(R.id.challenge_create_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FragmentTransaction transaction = ChallengeCreateListFragment.this.getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contentHolder, mCreateNewChallengeFragment).commit();

                } catch (NullPointerException e) {
                    Logger.LogExeption(ChallengeCreateListFragment.class.getSimpleName(), "Error while starting new Fragment", e);
                }

            }
        });
    }
}
