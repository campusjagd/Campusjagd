package de.tubs.campusjagd.gps;

import java.util.ArrayList;
import java.util.List;

import de.tubs.campusjagd.model.GPS;
import de.tubs.campusjagd.model.Room;

/**
 * Calculator class to check a GPS position with a room or a roomlist
 *
 * @author l.brettin@tu-bs.de
 */
public class GPSCalculator {

    // The maximum accepted distance in meters
    public static final float MAXIMUM_ACCEPTED_DISTANCE_IN_METERS = 10;

    /**
     * Check a position against a list of rooms, and check wether a room is nearby
     * @param actualPosition Position where you want to find nearby rooms
     * @param roomList Roomlist to check from
     * @return A list of all rooms nearby
     */
    public static List<Room> checkPositionAgainstRoomList(GPS actualPosition, List<Room> roomList) {
        List<Room> nearbyRooms = new ArrayList<>();
        for (Room room : roomList) {
            if (isNearby(actualPosition, room)) {
                nearbyRooms.add(room);
            }
        }
        return nearbyRooms;
    }

    /**
     * Check wether a room is near a gps position
     * @param actualPosition Position which you want to check if a room is nearby
     * @param room The room you want to check with
     * @return True, when the room is in an acceptable distance
     */
    private static boolean isNearby(GPS actualPosition, Room room) {
        float distance =  distFrom(actualPosition.latitude, actualPosition.longitude,
                room.getGps().latitude, room.getGps().longitude);
        return distance < MAXIMUM_ACCEPTED_DISTANCE_IN_METERS;
    }

    /**
     * Calculates the distance in meters between two points
     * https://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
     * @return Distance in meters
     */
    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
}
