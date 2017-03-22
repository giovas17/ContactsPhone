package com.softwaremobility.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.isseiaoki.simplecropview.util.Utils;
import com.softwaremobility.adapters.ContactsAdapter;
import com.softwaremobility.contactsphone.*;
import com.softwaremobility.custom.EmptyRecyclerView;
import com.softwaremobility.data.ContactsContract;
import com.softwaremobility.data.ContactsDataBase;
import com.softwaremobility.listeners.TaskListener;
import com.softwaremobility.models.Contact;
import com.softwaremobility.models.TaskRequest;
import com.softwaremobility.taskmanager.TaskManager;
import com.softwaremobility.utilities.Utilities;

import java.util.ArrayList;

/**
 * Created by darkgeat on 3/12/17.
 */

public class Contacts extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,TaskListener {

    private static final int REQUEST_PERMISSION_CODE = 1787;
    private final int LOADER_ID = 2342;
    private ProgressBar progress;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmptyRecyclerView list;
    private ContactsAdapter adapter;
    private EditText searchField;
    private ImageView searchIcon;
    private String search = "";
    private int GET_CONTACTS_TASK = 0;
    private String[] projection = new String[]{
            ContactsContract.ContactsEntry.Key_Name,
            ContactsContract.ContactsEntry.Key_Group,
            ContactsContract.ContactsEntry.Key_PhotoContact,
            ContactsContract.ContactsEntry.Key_Phone
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP){
            getContactList();
        }else {
            int permissionCheckRead = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
            int permissionCheckWrite = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS);
            if ((permissionCheckRead != PackageManager.PERMISSION_GRANTED) && (permissionCheckWrite != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS},REQUEST_PERMISSION_CODE);
            }else {
                getContactList();
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);

        list = (EmptyRecyclerView) view.findViewById(R.id.listContacts);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshingLayoutContacts);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.green_color,R.color.orange_color,R.color.purple_color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MakeAnUpdate();
            }
        });
        searchField = (EditText)view.findViewById(R.id.editSearch);
        searchIcon = (ImageView)view.findViewById(R.id.searchQuery);


        progress = (ProgressBar) view.findViewById(R.id.progressBarContacts);
        TextView emptyTextView = (TextView) view.findViewById(R.id.noData);
        emptyTextView.setText(getString(R.string.no_data));
        list.setProgressView(progress);
        list.setEmptyTextView(emptyTextView);
        list.setSwipeRefreshLayout(swipeRefreshLayout);

        LinearLayoutManager laman = new LinearLayoutManager(getContext());
        list.setLayoutManager(laman);
        list.setHasFixedSize(true);

        adapter = new ContactsAdapter(getContext());
        list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), com.softwaremobility.contactsphone.CreationContacts.class);
                startActivity(intent);
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search = searchField.getText().toString();
                Utilities.hideKeyboard(getActivity());
                getLoaderManager().restartLoader(LOADER_ID,null,Contacts.this);
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search = charSequence.toString();
                getLoaderManager().restartLoader(LOADER_ID,null,Contacts.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Utilities.hideKeyboard(getActivity());
                }
                return false;
            }
        });


        return view;
    }

    private void MakeAnUpdate() {
        getLoaderManager().restartLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = ContactsContract.ContactsEntry.Key_Name + " ASC";
        Uri uri = ContactsContract.ContactsEntry.buildContactsUriParams(null,search);
        return new CursorLoader(getContext(),uri,projection,null,null,sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        if (progress != null && progress.getVisibility() == View.VISIBLE){
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void OnFinishedTask(int id) {
        if (GET_CONTACTS_TASK == id){
            getLoaderManager().restartLoader(LOADER_ID,null,this);
        }
    }

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = android.provider.ContactsContract.Contacts.CONTENT_URI;
        String _ID = android.provider.ContactsContract.Contacts._ID;
        String DISPLAY_NAME = android.provider.ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = android.provider.ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String PHOTO_PATH = android.provider.ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
        Uri PhoneCONTENT_URI = android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = android.provider.ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI =  android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = android.provider.ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output;
        ContentResolver contentResolver = getContext().getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
        // Iterate every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                String photo_path = cursor.getString(cursor.getColumnIndex(PHOTO_PATH));
                contact.setId(Integer.parseInt(contact_id));
                contact.setName(name);
                contact.setPhoto_path(photo_path);
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                if (hasPhoneNumber > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                    StringBuilder builder = new StringBuilder();
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        if (builder.length() > 0){
                            builder.append(", " + phoneNumber);
                        }else {
                            builder.append(phoneNumber);
                        }
                    }
                    if (builder.length() > 0){
                        contact.setPhone(builder.toString());
                        contact.setTypePhone((String.valueOf(android.provider.ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)));
                    }
                    phoneCursor.close();
                    // Read every email id associated with the contact
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
                    while (emailCursor.moveToNext()) {
                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        if (email.length() > 0){
                            contact.setEmail(email);
                        }
                    }
                    emailCursor.close();
                }
                contacts.add(contact);
            }
        }
        return contacts;
    }

    private void getContactList(){
        final ContactsDataBase dataBase = new ContactsDataBase(getContext());
        if (dataBase.isEmpty(ContactsContract.ContactsEntry.TABLE_NAME, ContactsContract.ContactsEntry.Key_IdContact)){
            TaskRequest request = new TaskRequest(this) {
                @Override
                public boolean functionality(int id) {
                    ArrayList<Contact> contacts = getContacts();
                    for (Contact contact : contacts){
                        dataBase.newEntryContacts(contact);
                    }
                    return true;
                }
            };
            GET_CONTACTS_TASK = request.getId();
            TaskManager.addTask(request);
        }else {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:{
                int numberOfPermissionApproved = 0;
                for (int result : grantResults){
                    if (result == PackageManager.PERMISSION_GRANTED){
                        numberOfPermissionApproved++;
                    }
                }
                if (numberOfPermissionApproved > 0){
                    if (progress != null && progress.getVisibility() != View.VISIBLE){
                        progress.setVisibility(View.VISIBLE);
                    }
                    getContactList();
                }else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS},REQUEST_PERMISSION_CODE);
                }
            }
        }
    }
}
