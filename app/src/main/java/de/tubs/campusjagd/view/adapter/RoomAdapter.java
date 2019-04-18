package de.tubs.campusjagd.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.model.Room;

/**
 * Adapter for the rooms displayed for one challenge
 *
 * @author leon.brettin@tu-bs.de
 */
public class RoomAdapter extends RecyclerView.Adapter<ItemRoomViewHolder> {

    /**
     * List holding our rooms for the adapter
     */
    private List<Room> mRoomList;

    /**
     * Boolean value to determine if we should show the checkbox in the adapter
     * Standard is true
     */
    private boolean mShowCheckBoxes = true;

    public RoomAdapter(List<Room> roomList) {
        mRoomList = roomList;
    }

    public RoomAdapter(List<Room> roomList, boolean showCheckboxes) {
        mRoomList = roomList;
        mShowCheckBoxes = showCheckboxes;
    }

    /**
     * Inflates the view for the single items
     *
     * @param parent Parent view
     * @param i Counter which we dont need
     * @return Inflated view item
     */
    @NonNull
    @Override
    public ItemRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_card_item, parent, false);

        return new ItemRoomViewHolder(v);
    }

    /**
     * Binds the view item with the attributes.
     *
     * @param holder Holding view for the item
     * @param position Position of the itemview in the adapter
     */
    @Override
    public void onBindViewHolder(@NonNull ItemRoomViewHolder holder, int position) {
        Room room = mRoomList.get(position);

        holder.roomName.setText(room.getName());
        holder.roomCheckBox.setChecked(room.isRoomFound());
        holder.roomCheckBox.setVisibility(mShowCheckBoxes ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }
}

/**
 * Basic viewholder class for the items.
 */
class ItemRoomViewHolder extends RecyclerView.ViewHolder{

    TextView roomName;
    CheckBox roomCheckBox;

    ItemRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        roomName = itemView.findViewById(R.id.challenge_item_room_name);
        roomCheckBox = itemView.findViewById(R.id.challenge_item_room_checkBox);
    }
}