package de.tubs.campusjagd.view.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.List;

import de.tubs.campusjagd.model.Room;

/**
 * RoomAdapter which adapts the basic {@link RoomAdapter} to show now checkboxes
 */
public class RoomAdapter_NoCheckbox extends RoomAdapter {

    public RoomAdapter_NoCheckbox(List<Room> roomList) {
        super(roomList);
    }

    /**
     * Binds the view item with the attributes.
     * Overrides the normal roomadapter to show no checkboxes
     *
     * @param holder Holding view for the item
     * @param position Position of the itemview in the adapter
     */
    @Override
    public void onBindViewHolder(@NonNull ItemRoomViewHolder holder, int position) {
        Room room = mRoomList.get(position);

        holder.roomName.setText(room.getName());
        holder.roomCheckBox.setChecked(room.isRoomFound());
        holder.roomCheckBox.setClickable(false);
        holder.roomCheckBox.setVisibility(View.GONE);

    }
}
