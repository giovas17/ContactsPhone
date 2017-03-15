package com.softwaremobility.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ContactsDataBase dataBase;

    private static final int CONTACTS = 100;

    private static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ContactsContract.CONTENT_AUTHORITY;

        // 2) Use the addURI function to match each of the types.  Use the constants from
        // WeatherContract to help define the types to the UriMatcher.

        matcher.addURI(authority,ContactsContract.PATH_CONTACTS + "/*", CONTACTS);

        // 3) Return the new matcher!
        return matcher;
    }

    // group = ?
    private static String groupSelection =
            ContactsContract.ContactsEntry.Key_Group + " = ?";

    @Override
    public boolean onCreate() {
        dataBase = new ContactsDataBase(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (sUriMatcher.match(uri)){
            case CONTACTS:{
                String termSearch = uri.getQueryParameter(ContactsContract.ContactsEntry.Key_SearchTerm);
                String group = uri.getQueryParameter(ContactsContract.ContactsEntry.Key_Group);

                if (group != null && group.length() > 0){
                    selection = groupSelection;
                    selectionArgs = new String[]{group};
                }
                if (termSearch != null && termSearch.length() > 0){
                    if(selection == null){
                        selection = ContactsContract.ContactsEntry.Key_Name + " LIKE ?";
                        selectionArgs = new String[]{"%" + termSearch + "%"};
                    }else {
                        selection = selection + " AND " + ContactsContract.ContactsEntry.Key_Name + " LIKE ?";
                        assert selectionArgs != null;
                        String[] aux = new String[selectionArgs.length + 1];
                        System.arraycopy(selectionArgs, 0, aux, 0, selectionArgs.length);
                        aux[aux.length - 1] = "%" + termSearch + "%";
                        selectionArgs = aux;
                    }

                }
                return dataBase.getInstanceDataBase().query(
                        ContactsContract.ContactsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            }default: {
                return null;
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)){
            case CONTACTS:
                return ContactsContract.ContactsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnURI;
        switch (sUriMatcher.match(uri)){
            case CONTACTS: {
                dataBase.getInstanceDataBase().beginTransaction();
                long _id = dataBase.getInstanceDataBase().insert(ContactsContract.ContactsEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnURI = ContactsContract.ContactsEntry.buildContactsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                dataBase.getInstanceDataBase().setTransactionSuccessful();
                dataBase.getInstanceDataBase().endTransaction();
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnURI;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;

        switch (sUriMatcher.match(uri)){
            case CONTACTS: {
                rowsDeleted = dataBase.getInstanceDataBase().delete(ContactsContract.ContactsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS: {
                dataBase.getInstanceDataBase().beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = dataBase.getInstanceDataBase().insert(ContactsContract.ContactsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    dataBase.getInstanceDataBase().setTransactionSuccessful();
                } finally {
                    dataBase.getInstanceDataBase().endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
