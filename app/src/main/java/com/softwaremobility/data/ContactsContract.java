package com.softwaremobility.data;

import android.provider.BaseColumns;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsContract {

    public static final class ContactsEntry implements BaseColumns {

        public static final String TABLE_NAME = "ContactsPhone";
        /** --------------------------------- Table members -------------------------------------**/
        public static final String Key_IdContact = "id_contact";
        public static final String Key_Name = "name";
        public static final String Key_Phone = "phone_number";
        public static final String Key_Email = "email_address";
        public static final String Key_TypePhone = "type_phone";
        public static final String Key_PhotoContact = "photograph";
        public static final String Key_Group = "group";
    }
}
