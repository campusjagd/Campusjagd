package de.tubs.campusjagd.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import de.tubs.campusjagd.R;

public class ExtendedRoomAdapter extends RecyclerView.Adapter<ItemExtendedRoomViewHolder> {


    @NonNull
    @Override
    public ItemExtendedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemExtendedRoomViewHolder itemExtendedRoomViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

/**
 * Viewholder class for the extended room Adapter
 */
class ItemExtendedRoomViewHolder extends RecyclerView.ViewHolder{

    // Elements matching items of item_room_card_item
    TextView roomName;
    TextView timestamp;
    TextView points;
    TextView gpsPosition;
    CheckBox checkBox;
    ImageView qr;

    public ItemExtendedRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        roomName = itemView.findViewById(R.id.item_extended_room_adapter_roomname);
        timestamp = itemView.findViewById(R.id.item_extended_room_adapter_timestamp);
        points = itemView.findViewById(R.id.item_extended_room_adapter_points);
        gpsPosition = itemView.findViewById(R.id.item_extended_room_adapter_gps_position);
        checkBox = itemView.findViewById(R.id.item_extended_room_adapter_checkBox);
        qr = itemView.findViewById(R.id.item_extended_room_adapter_qr);
    }
}