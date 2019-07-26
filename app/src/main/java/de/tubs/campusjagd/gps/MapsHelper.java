package de.tubs.campusjagd.gps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.tubs.campusjagd.R;
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
        options.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(mContext, R.drawable.ic_campusjagd_pin)));

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


    @SuppressLint("ObsoleteSdkInt")
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap((int)(drawable.getIntrinsicWidth() *1.5),
                (int) (drawable.getIntrinsicHeight() * 1.5), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
