package com.softwaremobility.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import com.softwaremobility.listeners.TaskListener;
import com.softwaremobility.models.TaskRequest;

import java.util.ArrayList;

/**
 * Created by darkgeat on 3/21/17.
 */

public class TaskDataBase {

    private static final String TAG = TaskDataBase.class.getSimpleName();

    /** --------------------------------- DataBase name -------------------------------------**/
    private static final String DataBaseName = "TaskDataBase";
    /** --------------------------------- Data Base Version ---------------------------------**/
    private static final int version = 1;
    /** --------------------------------- Table Statements ----------------------------------**/
    private static final String TContacts = "CREATE TABLE " + TaskContract.TABLE_NAME + " (" +
            TaskContract.Key_IdContact + " INTEGER PRIMARY KEY NOT NULL, " +
            "UNIQUE (" + TaskContract.Key_IdContact + ") ON CONFLICT REPLACE);";

    /** ---------------------------------- SQLite Helper ------------------------------------**/
    private Helper myDB;
    private SQLiteDatabase dataBase;
    private final Context context;

    public TaskDataBase(Context context){
        this.context = context;
    }

    private static class Helper extends SQLiteOpenHelper {

        public Helper(Context context) {
            super(context, DataBaseName, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(TContacts);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TaskContract.TABLE_NAME);
            onCreate(sqLiteDatabase);
            sqLiteDatabase.setVersion(newVersion);
        }
    }


    /**  -----------------------------------------------------------------------------------**/

    /**
     * Open the DataBase
     * @return The database opened with write permissions
     */
    public TaskDataBase open(){
        myDB = new Helper(context);
        dataBase = myDB.getWritableDatabase();
        return this;
    }

    /**
     * Open the DataBase
     * @return The database opened with read permissions
     */
    public TaskDataBase open_read(){
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
     * @param task Task that will be added.
     */
    public void batchTask(TaskRequest task){
        open();
        ContentValues values = new ContentValues();
        values.put(TaskContract.Key_IdContact,task.getId());
        dataBase.beginTransaction();
        dataBase.insert(TaskContract.TABLE_NAME,null,values);
        Log.d(TAG,"batched task: " + task.getId());
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        close();
    }

    public ArrayList<TaskRequest> getTasks(TaskListener listener){
        ArrayList<TaskRequest> requests = new ArrayList<>();
        open();
        Cursor cursor = dataBase.query(TaskContract.TABLE_NAME,new String[]{TaskContract.Key_IdContact},null,null,null,null,null);
        for (cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(TaskContract.Key_IdContact));
            TaskRequest request = new TaskRequest(id, listener) {
                @Override
                public boolean functionality(int id) {
                    return false;
                }
            };
            requests.add(request);
        }
        close();
        return requests;
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
        if(table.equalsIgnoreCase(TaskContract.TABLE_NAME)){
            dataBase.execSQL(TContacts);
        }
        close();
    }
}
