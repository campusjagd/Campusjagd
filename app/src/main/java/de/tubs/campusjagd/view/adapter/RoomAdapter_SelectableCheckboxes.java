package de.tubs.campusjagd.view.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Room;

/**
 * RoomAdapter which adapts the basic {@link RoomAdapter} with a clickable checkbox and a selecting mechanism of rooms
 */
public class RoomAdapter_SelectableCheckboxes extends RoomAdapter {

    /**
     * List with all selected elements
     */
    private Set<Room> mSelectedElements;

    /**
     * Constructor which initiates the selected elements
     * @param roomList List of all rooms
     */
    public RoomAdapter_SelectableCheckboxes(List<Room> roomList) {
        super(roomList);

        mSelectedElements = new HashSet<>();
    }

    /**
     * Binds the view item with the attributes.
     * Makes the checkbox clickable and adds a onclick listener
     *
     * @param holder Holding view for the item
     * @param position Position of the itemview in the adapter
     */
    @Override
    public void onBindViewHolder(@NonNull ItemRoomViewHolder holder, int position) {
        final Room room = mRoomList.get(position);

        // Set up items
        holder.roomName.setText(room.getName());
        holder.roomCheckBox.setChecked(false);
        holder.roomCheckBox.setVisibility(View.VISIBLE);

        // Special case for this class:
        // Checkboxes are clickable and dont show the already found rooms
        // If you click on a checkbox it will be added to a list
        holder.roomCheckBox.setClickable(true);
        holder.roomCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedElements.contains(room)) {
                    mSelectedElements.remove(room);
                } else {
                    mSelectedElements.add(room);
                }

                Logger.Log(RoomAdapter_SelectableCheckboxes.class.getSimpleName(), "Elements size:" + mSelectedElements.size());
            }
        });
    }

    /**
     * Returns all selected rooms
     * @return all selected rooms
     */
    public Set<Room> getSelectedRooms() {
        return mSelectedElements;
    }
}
