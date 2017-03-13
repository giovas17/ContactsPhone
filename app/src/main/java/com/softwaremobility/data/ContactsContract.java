package com.softwaremobility.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsContract {

    public static final String CONTENT_AUTHORITY = "com.softwaremobility.contacts.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_CONTACTS = "contacts";

    public static final class ContactsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CONTACTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTACTS;

        public static final String TABLE_NAME = "ContactsPhone";
        /** --------------------------------- Table members -------------------------------------**/
        public static final String Key_IdContact = "id_contact";
        public static final String Key_Name = "name";
        public static final String Key_Phone = "phone_number";
        public static final String Key_Email = "email_address";
        public static final String Key_TypePhone = "type_phone";
        public static final String Key_PhotoContact = "photograph";
        public static final String Key_Group = "group";
        public static final String Key_SearchTerm = "searchTerm";

        public static Uri buildContactsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildContactsUriParams(@Nullable String group, @Nullable String termToSearch){
            return CONTENT_URI.buildUpon().appendPath("data")
                    .appendQueryParameter(Key_Group, group == null ? "" : group)
                    .appendQueryParameter(Key_SearchTerm, termToSearch == null ? "" : termToSearch)
                    .build();
        }
    }
}
