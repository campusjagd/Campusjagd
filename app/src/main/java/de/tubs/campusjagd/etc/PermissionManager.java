package de.tubs.campusjagd.etc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Permission Manager to handle permission requests and checks for permissions
 *
 * @author leon.brettin@tu-bs.de
 */
public class PermissionManager {

    /**
     * Check for all needed permissions.
     * If not ask for permissions
     */
    public static boolean checkPermissions(Activity activity) {
        boolean fineLocationPermissionGranted = checkAccessFineLocation(activity);
        boolean coarseLoctationPermissionGranted = checkAccessCoarseLocation(activity);
        boolean cameraPermissionGranted = checkCameraAccess(activity);

        if (!fineLocationPermissionGranted) {
            ActivityCompat.requestPermissions(activity, new String [] {Manifest.permission.ACCESS_FINE_LOCATION}, 14141);

        } else if (!coarseLoctationPermissionGranted) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 14141);

        } else if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 14141);

        } else {
            return true;

        }

        return false;
    }

    /**
     * Checks if the permission for the camera is given
     * @param activity The {@link Activity}
     * @return True if the permission is given
     */
    public static boolean checkCameraAccess(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
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
