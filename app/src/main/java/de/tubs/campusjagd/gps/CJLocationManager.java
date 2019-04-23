package de.tubs.campusjagd.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.etc.PermissionManager;
import de.tubs.campusjagd.model.GPS;

/**
 * Location Manager to get the Location of the device
 * Right now just GPS is activated, because GPS gives us an altitude and WIFI doesnt.
 *
 * @author leon.brettin@tu-bs.de
 */
public class CJLocationManager {

    /**
     * Callback interface to work with the location
     */
    public static interface LocationCallback {
        public void onNewLocationAvailable(GPS location);
    }

    @SuppressLint("MissingPermission")
    public void getLocation(Activity activity, final LocationCallback callback) {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                GPS gpsPosition = new GPS(location.getLatitude(), location.getLongitude(), location.getAltitude());
                callback.onNewLocationAvailable(gpsPosition);

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Check for permission to get location
        if (!PermissionManager.checkPermissions(activity)) {
            return;
        }

        // Check availability
        boolean isNetworkAvailable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        /**
         * If you want to get teh network connection also, you have to differ between network available and gps available
         * Right now we are just using GSP because it gives us a altitude...
         * Network doesnt give us an altitude.
         *
         * The altitude itself isnt very exact and you can finetune it more later
         */
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        locationManager.requestSingleUpdate(criteria, locationListener, null);

    }
}
