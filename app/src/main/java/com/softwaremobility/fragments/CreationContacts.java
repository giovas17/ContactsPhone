package com.softwaremobility.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.softwaremobility.contactsphone.*;

import static com.softwaremobility.utilities.MediaUtilities.mCurrentPhotoPath;

/**
 * Created by darkgeat on 3/15/17.
 */

public class CreationContacts extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 25;
    private static final int REQUEST_PERMISSION_CODE = 2453;
    private static final int REQUEST_PERMISSION_CODE_STORAGE = 1342;
    private ImageView imageContact;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creation_contact,container,false);

        imageContact = (ImageView)view.findViewById(R.id.imageViewContact);

        TextView cameraShoot = (TextView)view.findViewById(R.id.cameraButtonCreation);
        cameraShoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
                    goToCamera();
                }else {
                    int permissionCheckCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
                    if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CAMERA},REQUEST_PERMISSION_CODE);
                    }else {
                        goToCamera();
                    }
                }
            }
        });

        TextView imagePicker = (TextView)view.findViewById(R.id.galleryButtonCreation);
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
                    openGallery();
                }else {
                    int permissionCheckRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    int permissionCheckWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if ((permissionCheckRead != PackageManager.PERMISSION_GRANTED) && (permissionCheckWrite != PackageManager.PERMISSION_GRANTED)){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE_STORAGE);
                    }else {
                        openGallery();
                    }
                }

            }
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(getActivity(), com.softwaremobility.contactsphone.PreviewCropImage.class);
        intent.putExtra(getString(R.string.tag_gallery),true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void goToCamera() {
        Intent intent = new Intent(getActivity(), com.softwaremobility.contactsphone.PreviewCropImage.class);
        intent.putExtra(getString(R.string.tag_photo),true);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllApproved = true;
        for (int result : grantResults){
            isAllApproved = isAllApproved & (result == PackageManager.PERMISSION_GRANTED);
        }
        if (requestCode == REQUEST_PERMISSION_CODE && isAllApproved){
            goToCamera();
        }
        if (requestCode == REQUEST_PERMISSION_CODE_STORAGE && isAllApproved){
            openGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            String path = data.getStringExtra(getString(R.string.tag_picture));
            Glide.with(this).load(path)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            imageContact.setImageBitmap(resource);
                        }
                    });
        }
    }
}
