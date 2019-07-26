package de.tubs.campusjagd.view.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import de.tubs.campusjagd.Database.DatabaseHelperRoom;
import de.tubs.campusjagd.R;
import de.tubs.campusjagd.etc.Logger;
import de.tubs.campusjagd.etc.PermissionManager;
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
    public ExtendedRoomAdapter(List<Room> roomList, Activity activity) {
        mActivity = activity;
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
     * The {@link Context}
     */
    private Activity mActivity;

    /**
     * The bitmap of the qr code
     */
    private Bitmap mBitmap;

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

        Date date = new Date(room.getTimestamp());
        holder.timestamp.setText("Hinzugefügt am: " + date.getDay() + "." + date.getMonth() + "." + (date.getYear() + 1900));
        holder.points.setText("Punkte: " + Integer.toString(room.getPoints()));
        holder.gpsPosition.setText(room.getGps().toString());
        holder.checkBox.setChecked(room.isRoomFound());
        mBitmap = generateQR(holder.qr, room.toString());

        holder.qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtendedRoomAdapter.this.createSaveFileDialog().show();
            }
        });

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

    /**
     * Creates a Dialog to ask if the bitmap can be saved
     * @return Dialog to show
     */
    private Dialog createSaveFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(R.string.ask_to_save_qr)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Be careful that you get the right bitmap
                        saveToFile(mBitmap);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });
        return builder.create();
    }

    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    /**
     * Generates a qr code for the room
     * @param imageView Imageview to display the qr code
     * @param text Text to generate into qr
     */
    private Bitmap generateQR(ImageView imageView, String text) {
        Bitmap bitmap = null;

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,400,400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
           bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Logger.LogExeption(ExtendedRoomAdapter.class.getSimpleName(), "Unable to write QR", e);

        }

        return bitmap;
    }

    /**
     * Saves a bitmap to a file
     * @param bitmap Bitmap to save
     */
    private void saveToFile(Bitmap bitmap) {
        // Ask for write permission
        if (!PermissionManager.checkAccessWriteExternalStorage(mActivity)) {
            PermissionManager.askPermissionWrite(mActivity);
        }

        // If we still dont have it return
        if (!PermissionManager.checkAccessWriteExternalStorage(mActivity)) {
            return;
        }

        //  Write image
        Uri path = mActivity.getContentResolver().insert (MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        boolean writeWasSuccessFull = false;
        try
        {
            OutputStream stream = mActivity.getContentResolver ().openOutputStream (path);
            bitmap.compress (Bitmap.CompressFormat.JPEG, 90, stream);
            if (stream != null) {
                stream.close();
            }
            writeWasSuccessFull = true;
        }
        catch (IOException e){
            Logger.LogExeption(ExtendedRoomAdapter.class.getSimpleName(),"Unable to write file", e);
        }

        // Start Galery
        if (writeWasSuccessFull) {
            Intent i = new Intent(Intent.ACTION_VIEW,
                    path);
            final int ACTION_VIEW_IMAGE = 1234;
            mActivity.startActivityForResult(i, ACTION_VIEW_IMAGE);
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