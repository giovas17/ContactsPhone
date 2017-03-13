package com.softwaremobility.taskmanager;

import android.content.Context;

import com.softwaremobility.listeners.TaskManagerListener;
import com.softwaremobility.models.TaskRequest;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by darkgeat on 3/12/17.
 */

public class TaskManager implements TaskManagerListener {

    private static TaskManager ourInstance = null;
    private Queue<TaskRequest> taskRequestQueue = new LinkedList<>();

    private synchronized static void createInstance(){
        if (ourInstance == null){
            ourInstance = new TaskManager();
        }
    }

    private static TaskManager getInstance(){
        if (ourInstance == null) createInstance();
        return ourInstance;
    }

    public static void addTask(TaskRequest taskRequest){
        getInstance().getTaskRequestQueue().add(taskRequest);
        TaskModelThread taskModelThread = new TaskModelThread(taskRequest, getInstance());
        taskModelThread.execute();
    }

    public Queue<TaskRequest> getTaskRequestQueue() {
        return taskRequestQueue;
    }

    @Override
    public void OnFinishedTask(TaskRequest request) {
        if (getInstance().getTaskRequestQueue().contains(request)){
            getInstance().getTaskRequestQueue().remove(request);
        }
    }
}
