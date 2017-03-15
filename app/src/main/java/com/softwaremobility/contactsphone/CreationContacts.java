package com.softwaremobility.contactsphone;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by darkgeat on 3/15/17.
 */

public class CreationContacts extends AppCompatActivity {

    private Fragment contactsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_contact);

        contactsFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentCreationContacts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        contactsFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        contactsFragment.onActivityResult(requestCode,resultCode,data);
    }
}
