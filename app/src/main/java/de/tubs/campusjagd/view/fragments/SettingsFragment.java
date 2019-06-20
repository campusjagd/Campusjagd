package de.tubs.campusjagd.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Resources;

/**
 * Settings fragment to set settings like the username etc.
 *
 * @author l.brettin@tu-bs.de
 */
public class SettingsFragment extends Fragment {

    private Button mCheckUsernameButton;
    private EditText mUsernameEditText;

    private String mUsername;
    Resources mResources;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        this.init(view);

        return view;
    }

    /**
     * Inits the view
     * @param view The View to init
     */
    private void init(View view) {
        mResources = Resources.getInstance(view.getContext());

        mCheckUsernameButton = view.findViewById(R.id.check_username_button);
        mUsernameEditText = view.findViewById(R.id.username_edit_text);
        mUsername = mResources.getUserName();

        mCheckUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Remove Keyboard
                try {
                    InputMethodManager inputManager = (InputMethodManager) SettingsFragment.this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(SettingsFragment.this.getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                } catch (NullPointerException e) {
                    Logger.LogExeption(SettingsFragment.class.getSimpleName(), "Unable to remove keyboard", e);

                }

                mUsername = mUsernameEditText.getText().toString();
                if (mResources.isUsernamePossible(mUsername)) {
                    mResources.updateUsername(mUsername);

                    Toast.makeText(SettingsFragment.this.getContext(), R.string.settings_check_username_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SettingsFragment.this.getContext(), R.string.settings_check_username_failure, Toast.LENGTH_LONG).show();

                }
            }
        });

        mUsernameEditText.setText(mUsername);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            // Set toolbar
            Toolbar actionBarToolbar = this.getActivity().findViewById(R.id.toolbar);
            actionBarToolbar.setTitle(R.string.menu_settings);

        } catch (NullPointerException e) {
            Logger.LogExeption(CreateNewRoomFragment.class.getSimpleName(), "Unable to set toolbar", e);
        }

        mResources = Resources.getInstance(this.getActivity());
    }
}
