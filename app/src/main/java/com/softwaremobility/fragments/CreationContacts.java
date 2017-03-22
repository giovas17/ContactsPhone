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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.softwaremobility.contactsphone.*;
import com.softwaremobility.data.ContactsDataBase;
import com.softwaremobility.listeners.TaskListener;
import com.softwaremobility.models.Contact;
import com.softwaremobility.models.TaskRequest;
import com.softwaremobility.taskmanager.TaskManager;

/**
 * Created by darkgeat on 3/15/17.
 */

public class CreationContacts extends Fragment implements TaskListener {

    private static final int PICK_IMAGE_REQUEST = 25;
    private static final int REQUEST_PERMISSION_CODE = 2453;
    private static final int REQUEST_PERMISSION_CODE_STORAGE = 1342;
    private String path_image = "";
    private ImageView imageContact;
    private EditText nameText;
    private EditText emailText;
    private EditText phoneText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creation_contact,container,false);

        imageContact = (ImageView)view.findViewById(R.id.imageViewContact);
        nameText = (EditText)view.findViewById(R.id.personName);
        emailText = (EditText)view.findViewById(R.id.emailText);
        phoneText = (EditText)view.findViewById(R.id.phoneText);

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

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fabCreation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passValidation()){
                    TaskRequest request = new TaskRequest(CreationContacts.this) {
                        @Override
                        public boolean functionality(int id) {
                            try {
                                Contact contact = getContact();
                                contact.setId(id);
                                ContactsDataBase dataBase = new ContactsDataBase(getContext());
                                dataBase.newEntryContacts(contact);
                                return true;
                            }catch (Exception e){
                                return false;
                            }

                        }
                    };
                    TaskManager.addTask(request);
                }
            }
        });

        return view;
    }

    private Contact getContact() {
        Contact contact = new Contact();
        contact.setName(nameText.getText().toString());
        contact.setPhone(phoneText.getText().toString());
        contact.setPhoto_path(path_image);
        contact.setEmail(emailText.getText().toString());
        return contact;
    }

    private boolean passValidation() {
        boolean pass = true;
        emailText.setError(null);
        nameText.setError(null);
        phoneText.setError(null);
        if (emailText.getText().toString().length() == 0){
            emailText.setError(getString(R.string.error_email_empty));
            pass = false;
        }
        if (nameText.getText().toString().length() == 0){
            nameText.setError(getString(R.string.error_name_empty));
            pass = false;
        }
        if (phoneText.getText().toString().length() == 0){
            nameText.setError(getString(R.string.error_phone_empty));
            pass = false;
        }
        return pass;
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
            path_image = data.getStringExtra(getString(R.string.tag_picture));
            Glide.with(this).load(path_image)
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

    @Override
    public void OnFinishedTask(int id) {

    }
}
