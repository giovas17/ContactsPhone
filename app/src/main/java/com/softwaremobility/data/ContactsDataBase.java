package com.softwaremobility.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by darkgeat on 3/12/17.
 */

public class ContactsDataBase {

    /** --------------------------------- DataBase name -------------------------------------**/
    private static final String DataBaseName = "ContactsDataBase";
    /** --------------------------------- Data Base Version ---------------------------------**/
    private static final int version = 1;
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
            close();
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
