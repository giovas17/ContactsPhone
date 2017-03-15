package com.softwaremobility.contactsphone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by darkgeat on 3/12/17.
 */

public class Contacts extends AppCompatActivity {

    private Fragment contactsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contactsFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContacts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        contactsFragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
