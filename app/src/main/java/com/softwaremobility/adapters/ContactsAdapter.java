package com.softwaremobility.adapters;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.softwaremobility.contactsphone.R;
import com.softwaremobility.data.ContactsContract;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolderContact>{

    private Context context;
    private Cursor cursor;

    public ContactsAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolderContact onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact,parent,false);
        ViewHolderContact holderContact = new ViewHolderContact(view);
        view.setTag(holderContact);
        return holderContact;
    }

    @Override
    public void onBindViewHolder(ViewHolderContact holder, int position) {
        cursor = getItem(position);
        holder.nameContact.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.ContactsEntry.Key_Name)));
        holder.phoneContact.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.ContactsEntry.Key_Phone)));
        if (cursor.getString(cursor.getColumnIndex(ContactsContract.ContactsEntry.Key_PhotoContact)) != null) {
            holder.imageContact.setImageBitmap(loadContactPhotoThumbnail(cursor.getString(cursor.getColumnIndex(ContactsContract.ContactsEntry.Key_PhotoContact))));
        }else {
            holder.imageContact.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public int getItemCount() {
        if (cursor != null){
            return cursor.getCount();
        }else {
            return 0;
        }
    }

    public void swapCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public Cursor getItem(final int position){
        if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()){
            if (cursor.moveToNext()) {
                cursor.moveToPosition(position);
            }else {
                cursor.moveToFirst();
            }
        }
        return cursor;
    }

    static class ViewHolderContact extends RecyclerView.ViewHolder{
        private final ImageView imageContact;
        private final TextView nameContact;
        private final TextView phoneContact;

        public ViewHolderContact(View view){
            super(view);
            imageContact = (ImageView)view.findViewById(R.id.imageContact);
            nameContact = (TextView)view.findViewById(R.id.nameContact);
            phoneContact = (TextView)view.findViewById(R.id.phoneContact);
        }
    }

    private Bitmap loadContactPhotoThumbnail(String photoData) {
        // Creates an asset file descriptor for the thumbnail file.
        AssetFileDescriptor afd = null;
        // try-catch block for file not found
        try {
            // Creates a holder for the URI.
            Uri thumbUri;
            thumbUri = Uri.parse(photoData);

        /*
         * Retrieves an AssetFileDescriptor object for the thumbnail
         * URI
         * using ContentResolver.openAssetFileDescriptor
         */
            afd = context.getContentResolver().
                    openAssetFileDescriptor(thumbUri, "r");
        /*
         * Gets a file descriptor from the asset file descriptor.
         * This object can be used across processes.
         */
            FileDescriptor fileDescriptor = afd.getFileDescriptor();
            // Decode the photo file and return the result as a Bitmap
            // If the file descriptor is valid
            if (fileDescriptor != null) {
                // Decodes the bitmap
                return BitmapFactory.decodeFileDescriptor(
                        fileDescriptor, null, null);
            }
            // If the file isn't found
        } catch (FileNotFoundException e) {
            /*
             * Handle file not found errors
             */
            // In all cases, close the asset file descriptor
        } finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }
}
