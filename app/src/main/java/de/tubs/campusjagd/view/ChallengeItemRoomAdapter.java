package de.tubs.campusjagd.view;

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

public class ChallengeItemRoomAdapter extends RecyclerView.Adapter<ChallengeItemRoomViewHolder> {

    List<Room> mRoomList;

    public ChallengeItemRoomAdapter(List<Room> roomList) {
        mRoomList = roomList;
    }

    @NonNull
    @Override
    public ChallengeItemRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_challenge_card_item, parent, false);

        return new ChallengeItemRoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeItemRoomViewHolder holder, int position) {
        Room room = mRoomList.get(position);

        holder.roomName.setText(room.getName());
        holder.roomCheckBox.setChecked(room.isRoomFound());

    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }
}

class ChallengeItemRoomViewHolder extends RecyclerView.ViewHolder{

    TextView roomName;

    CheckBox roomCheckBox;

    public ChallengeItemRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        roomName = itemView.findViewById(R.id.challenge_item_room_name);
        roomCheckBox = itemView.findViewById(R.id.challenge_item_room_checkBox);
    }
}