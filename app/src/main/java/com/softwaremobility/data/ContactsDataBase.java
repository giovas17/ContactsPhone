package com.softwaremobility.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.softwaremobility.models.Contact;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsDataBase {

    private static final String TAG = ContactsDataBase.class.getSimpleName();

    /** --------------------------------- DataBase name -------------------------------------**/
    private static final String DataBaseName = "ContactsDataBase";
    /** --------------------------------- Data Base Version ---------------------------------**/
    private static final int version = 2;
    /** --------------------------------- Table Statements ----------------------------------**/
    private static final String TContacts = "CREATE TABLE " + ContactsContract.ContactsEntry.TABLE_NAME + " (" +
            ContactsContract.ContactsEntry.Key_IdContact + " INTEGER PRIMARY KEY NOT NULL, " +
            ContactsContract.ContactsEntry.Key_Name + " TEXT NOT NULL, " +
            ContactsContract.ContactsEntry.Key_Phone + " INTEGER, " +
            ContactsContract.ContactsEntry.Key_Email + " TEXT, " +
            ContactsContract.ContactsEntry.Key_TypePhone + " TINYINT DEFAULT 0, " +
            ContactsContract.ContactsEntry.Key_Group + " TEXT, " +
            ContactsContract.ContactsEntry.Key_PhotoContact + " BLOB, " +
            "UNIQUE (" + ContactsContract.ContactsEntry.Key_IdContact + ") ON CONFLICT REPLACE);";

    /** ---------------------------------- SQLite Helper ------------------------------------**/
    private Helper myDB;
    private SQLiteDatabase dataBase;
    private final Context context;

    public ContactsDataBase(Context context){
        this.context = context;
    }

    private static class Helper extends SQLiteOpenHelper{

        public Helper(Context context) {
            super(context, DataBaseName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(TContacts);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContactsContract.ContactsEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);
            sqLiteDatabase.setVersion(newVersion);
        }
    }
    public SQLiteDatabase getInstanceDataBase(){
        if (dataBase == null || !dataBase.isOpen()) this.open();
        return dataBase;
    }


    /**  -----------------------------------------------------------------------------------**/

    /**
     * Open the DataBase
     * @return The database opened with write permissions
     */
    public ContactsDataBase open(){
        myDB = new Helper(context);
        dataBase = myDB.getWritableDatabase();
        return this;
    }

    /**
     * Open the DataBase
     * @return The database opened with read permissions
     */
    public ContactsDataBase open_read(){
        myDB = new Helper(context);
        dataBase = myDB.getReadableDatabase();
        return this;
    }

    /**
     * Close the DataBase
     */
    public void close(){
        myDB.close();
        dataBase.close();
    }

    /**
     * Making custom queries with SQLite sentences.
     * @param sql
     * @param selectionArgs
     * @return
     */
    public Cursor querySQL(String sql, String[] selectionArgs){
        Cursor regreso = null;
        open();
        regreso = dataBase.rawQuery(sql, selectionArgs);
        return regreso;
    }

    /**
     * This method inserts a new contact in the database
     * @param contact Contact that will be added.
     */
    public void newEntryContacts(Contact contact){
        open();
        ContentValues values = new ContentValues();
        values.put(ContactsContract.ContactsEntry.Key_IdContact, contact.getId());
        values.put(ContactsContract.ContactsEntry.Key_Name, contact.getName());
        if (contact.getEmail() != null && contact.getEmail().length() > 0) {
            values.put(ContactsContract.ContactsEntry.Key_Email, contact.getEmail());
        }
        if (contact.getPhone() != null && contact.getPhone().length() > 0){
            values.put(ContactsContract.ContactsEntry.Key_Phone,contact.getPhone());
        }
        if (contact.getPhoto_path() != null && contact.getPhoto_path().length() > 0){
            values.put(ContactsContract.ContactsEntry.Key_PhotoContact,contact.getPhoto_path());
        }
        if (contact.getTypePhone() != null && contact.getTypePhone().length() > 0){
            values.put(ContactsContract.ContactsEntry.Key_TypePhone,contact.getTypePhone());
        }else {
            values.put(ContactsContract.ContactsEntry.Key_TypePhone,"MOBILE");
        }
        if (contact.getGroup() != null && contact.getGroup().length() > 0){
            values.put(ContactsContract.ContactsEntry.Key_Group,contact.getGroup());
        }
        dataBase.beginTransaction();
        dataBase.insert(ContactsContract.ContactsEntry.TABLE_NAME,null,values);
        Log.d(TAG,"Inserted contact: " + contact.getName());
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        close();
    }

    /**
     * This method says if the table selected is empty
     * @param tableName String that represents the table that will be check.
     * @param primaryKey String that represents the primary field of the table selected.
     * @return a boolean values that represents if the table is empty.
     */
    public boolean isEmpty(String tableName, String primaryKey){
        try {
            open();
            Cursor c = dataBase.query(tableName,new String[]{primaryKey},null,null,null,null,null);
            if (c!=null && c.moveToFirst()){
                if (c.getCount() > 0){
                    close();
                    return false;
                }else {
                    return true;
                }
            }else {
                close();
                return true;
            }
        }catch (Exception e){
            if (dataBase != null) {
                close();
            }
            return true;
        }
    }

    /**
     * This method is used to delete all data from the table selected
     * @param table String that represents the name of the table that will be erased
     */
    public void DeleteDataFromTable(String table){
        open();
        dataBase.execSQL("DROP TABLE IF EXISTS " + table);
        if(table.equalsIgnoreCase(ContactsContract.ContactsEntry.TABLE_NAME)){
            dataBase.execSQL(TContacts);
        }
        close();
    }
}
