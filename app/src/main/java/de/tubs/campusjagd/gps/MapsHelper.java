package de.tubs.campusjagd.gps;

import android.content.Context;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.tubs.campusjagd.model.Resources;
import de.tubs.campusjagd.model.Room;

/**
 * Class to work with the points to create on the map
 *
 * @author l.brettin@tu-bs.de
 */
public class MapsHelper {

    // The zoom preferences are just integer on the google maps api.
    private static final float ZOOMPREFERENCE = 14F;

    // The context
    private Context mContext;
    // The google map to work with
    private GoogleMap mGoogleMap;
    // List of all rooms
    private List<Room> mRooms;

    /**
     * Basic constructor
     * @param context The {@link Context}
     * @param googleMap Map to draw points on
     */
    public MapsHelper(Context context, GoogleMap googleMap) {
        mContext = context;
        mGoogleMap = googleMap;
        mRooms = Resources.getInstance(mContext).getAllRooms();
    }

    /**
     * Creates points on the map
     */
    public void createPointsOnMap() {
        for (Room room : mRooms) {
            if (room.isRoomFound()) {
                mGoogleMap.addMarker(createOnePoint(room));
            }
        }
    }

    /**
     * Creates one Point object to add on the map
     * @param room Room which represents the point
     * @return MarkerOptions to put on the map
     */
    private MarkerOptions createOnePoint(Room room) {
        MarkerOptions options = new MarkerOptions();

        // Set position
        LatLng position = new LatLng(room.getGps().latitude, room.getGps().longitude);
        options.position(position);

        // Set Title
        options.title(room.getName());

        // Change color of the icon
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        return options;
    }

    /**
     * Try to set the google maps camera on an optimal position too see the most important points.
     */
    public void setCameraPosition() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder = this.getCameraPositionBuilder(builder);

        // Set Camera
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), ZOOMPREFERENCE);
        mGoogleMap.animateCamera(cu);
    }


    /**
     * Just add all points and get the middlepoint
     * @param builder Builder for the camera
     * @return {@link LatLngBounds.Builder} with points included
     */
    private LatLngBounds.Builder getCameraPositionBuilder(LatLngBounds.Builder builder) {
        for (Room room: mRooms) {
            if (room.isRoomFound()) {
                builder.include(new LatLng(room.getGps().latitude, room.getGps().longitude));

            }
        }
        return builder;
    }

}
