package com.softwaremobility.models;

import com.softwaremobility.listeners.TaskListener;

import java.util.Random;

/**
 * Created by darkgeat on 3/12/17.
 */

public abstract class TaskRequest{

    private int id;
    private TaskListener listener;

    public TaskRequest(TaskListener listener){
        Random random = new Random();
        id = random.nextInt();
        this.listener = listener;
    }

    public abstract boolean functionality(int id);

    public int getId() {
        return id;
    }

    public TaskListener getListener() {
        return listener;
    }
}
