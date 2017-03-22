package com.softwaremobility.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.softwaremobility.listeners.TaskListener;

import java.util.Random;

/**
 * Created by darkgeat on 3/12/17.
 */

public abstract class TaskRequest implements Parcelable{

    private int id;
    private TaskListener listener;

    public TaskRequest(TaskListener listener){
        Random random = new Random();
        id = random.nextInt();
        this.listener = listener;
    }

    public TaskRequest(int id, @Nullable TaskListener listener){
        this.id = id;
        this.listener = listener;
    }

    public abstract boolean functionality(int id);

    public int getId() {
        return id;
    }

    public TaskListener getListener() {
        return listener;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        writeToParcel(parcel,i);
    }

    public static final Creator CREATOR = new Creator() {

        @Override
        public TaskRequest createFromParcel(Parcel parcel) {
            int id = parcel.readInt();
            return new TaskRequest(id, null) {
                @Override
                public boolean functionality(int id) {
                    return false;
                }
            };
        }

        @Override
        public TaskRequest[] newArray(int i) {
            return new TaskRequest[0];
        }
    };
}
