package de.tubs.campusjagd.view.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;
import java.util.List;

import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.model.Room;

/**
 * Extend room adapter showing much more informations than just the basic {@link RoomAdapter}
 * Because the implementation is much more complex I decided to give this adapter a own class
 * instead of just extending it from the basic {@link RoomAdapter}.
 *
 * @author leon.brettin@tu-bs.de
 */
public class ExtendedRoomAdapter extends RecyclerView.Adapter<ItemExtendedRoomViewHolder> {

    /**
     * Basic constructor
     * @param roomList List with all rooms to show
     */
    public ExtendedRoomAdapter(List<Room> roomList) {
        mRoomList = roomList;
    }

    /**
     * List holding all the rooms
     */
    private List<Room> mRoomList;

    /**
     * The position of the expanded element
     */
    private int mExpandedPosition = -1;

    /**
     * Inflates the view for the single items
     *
     * @param parent Parent view
     * @param position Counter
     * @return Inflated view items
     */
    @NonNull
    @Override
    public ItemExtendedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_card_item, parent, false);

        return new ItemExtendedRoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemExtendedRoomViewHolder holder, int position) {
        Room room = mRoomList.get(position);

        // Bind holder with element from the list
        holder.roomName.setText(room.getName());
        holder.timestamp.setText(new Date(room.getTimestamp()).toString());
        holder.points.setText(Integer.toString(room.getPoints()));
        holder.gpsPosition.setText("GPS: " + room.getGps().toString());
        holder.checkBox.setChecked(room.isRoomFound());
        generateQR(holder.qr, room.toString());

        // Expansion of challenge when you click on it
        final boolean isExpanded = position == mExpandedPosition;
        holder.hiddenView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        final int itemposition = holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : itemposition;

                // You can add a transition later with (But you have to implement stuff here later
                //TransitionManager.beginDelayedTransition(mRecyclerView);

                notifyItemChanged(itemposition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    private void generateQR(ImageView imageView, String text) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Logger.LogExeption(ExtendedRoomAdapter.class.getSimpleName(), "Unable to write QR", e);

        }
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
    View hiddenView;

    ItemExtendedRoomViewHolder(@NonNull View itemView) {
        super(itemView);

        roomName = itemView.findViewById(R.id.item_extended_room_adapter_roomname);
        timestamp = itemView.findViewById(R.id.item_extended_room_adapter_timestamp);
        points = itemView.findViewById(R.id.item_extended_room_adapter_points);
        gpsPosition = itemView.findViewById(R.id.item_extended_room_adapter_gps_position);
        checkBox = itemView.findViewById(R.id.item_extended_room_adapter_checkBox);
        qr = itemView.findViewById(R.id.item_extended_room_adapter_qr);
        hiddenView = itemView.findViewById(R.id.item_extended_room_adapter_hiddenView);
    }
}