package de.tubs.campusjagd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import de.tubs.campusjagd.etc.PermissionManager;
import de.tubs.campusjagd.nfc.ReceiverActivity;
import de.tubs.campusjagd.view.fragments.ChallengeCreateListFragment;
import de.tubs.campusjagd.view.fragments.ChallengeListFragment;
import de.tubs.campusjagd.view.fragments.ChallengeTransferFragment;
import de.tubs.campusjagd.view.fragments.HighscoreFragment;
import de.tubs.campusjagd.view.fragments.RoomListFragment;
import de.tubs.campusjagd.view.fragments.SettingsFragment;
import de.tubs.campusjagd.view.fragments.StatsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // All existing fragments
    ChallengeListFragment mChallengeListFragment;
    ChallengeCreateListFragment mChallengeCreateListFragment;
    RoomListFragment mRoomListFragment;
    StatsFragment mStatsFragment;
    ChallengeTransferFragment mChallengeTransfer;
    SettingsFragment mSettingsFragment;
    HighscoreFragment mHighScoreFragment;

    /**
     * Starts this activity
     *
     * @param context The {@link Context}
     */
    public static void startActivty(Context context) {
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //-------------------------------------------
        this.init();

    }

    /**
     * Init special views
     */
    private void init() {
        // Initialize fragments
        mChallengeListFragment = new ChallengeListFragment();
        mChallengeCreateListFragment = new ChallengeCreateListFragment();
        mRoomListFragment = new RoomListFragment();
        mStatsFragment = new StatsFragment();
        mChallengeTransfer = new ChallengeTransferFragment();
        mSettingsFragment = new SettingsFragment();
        mHighScoreFragment = new HighscoreFragment();

        // Add first fragment to the content holder
        getSupportFragmentManager().beginTransaction().add(R.id.contentHolder, mChallengeListFragment).commit();

        PermissionManager.checkPermissions(this);

    }



    /**
     * Wait until permission is granted, then check if all permissions are really granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean fineLocationPermissionGranted = PermissionManager.checkAccessFineLocation(this);
        boolean coarseLoctationPermissionGranted = PermissionManager.checkAccessCoarseLocation(this);

        if (!fineLocationPermissionGranted && !coarseLoctationPermissionGranted) {
            Toast.makeText(this, R.string.no_permissions_granted, Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            // Go to the challenge list fragment

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
            transaction.replace(R.id.contentHolder, mChallengeListFragment).commit();

        } else if (id == R.id.nav_challenge_list){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
            transaction.replace(R.id.contentHolder, mChallengeCreateListFragment).commit();

        } else if (id == R.id.nav_roomlist) {
            // Go to the room list fragment

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_down, R.anim.slide_out_down, R.anim.slide_out_up);
            transaction.replace(R.id.contentHolder, mRoomListFragment).commit();
        } else if (id == R.id.nav_stats) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentHolder, mStatsFragment).commit();
        } else if (id == R.id.nav_challenge_transfer) {
            /*
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentHolder, mChallengeTransfer).commit();
            */

            //Fragment not needed, the menu point will start the receiving activity
            Intent intent = new Intent(this, ReceiverActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentHolder, mSettingsFragment).commit();
        } else if (id == R.id.nav_highscore) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contentHolder, mHighScoreFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
