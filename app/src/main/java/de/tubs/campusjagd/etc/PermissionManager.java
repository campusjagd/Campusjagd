package de.tubs.campusjagd.etc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionManager {

    /**
     * Check for all needed permissions.
     * If not ask for permissions
     */
    public static void checkPermissions(Activity activity) {
        boolean fineLocationPermissionGranted = checkAccessFineLocation(activity);
        boolean coarseLoctationPermissionGranted = checkAccessCoarseLocation(activity);

        if (!fineLocationPermissionGranted) {
            ActivityCompat.requestPermissions(activity, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 14141);

        } else if (!coarseLoctationPermissionGranted) {
            ActivityCompat.requestPermissions(activity, new String [] {Manifest.permission.ACCESS_COARSE_LOCATION}, 14141);
        }

    }

    /**
     * Checks if the permission for the fine location is given
     * @param activity The {@link Activity}
     * @return True if the permission is given
     */
    public static boolean checkAccessFineLocation(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Checks if the permission for the coarse location is given
     * @param activity The {@link Activity}
     * @return True if the permission is given
     */
    public static boolean checkAccessCoarseLocation(Activity activity) {
        return ContextCompat.checkSelfPermission( activity, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED;
    }
}
