package com.softwaremobility.taskmanager;

import android.content.Context;

import com.softwaremobility.data.TaskContract;
import com.softwaremobility.data.TaskDataBase;
import com.softwaremobility.listeners.TaskListener;
import com.softwaremobility.listeners.TaskManagerListener;
import com.softwaremobility.models.TaskRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by darkgeat on 3/12/17.
 */

public class TaskManager implements TaskManagerListener, TaskListener {

    private static TaskManager ourInstance = null;
    private TaskModelThread taskExecutor = null;
    private Queue<TaskRequest> taskRequestQueue = new LinkedList<>();
    private Context context;
    private static int numberTaskRejected = 0;

    private synchronized static void createInstance(){
        if (ourInstance == null){
            ourInstance = new TaskManager();
            getInstance().taskExecutor = new TaskModelThread(getInstance());
        }
    }

    private static TaskManager getInstance(){
        if (ourInstance == null) createInstance();
        return ourInstance;
    }

    public static void addTask(TaskRequest taskRequest, Context context){
        getInstance().getTaskRequestQueue().add(taskRequest);
        getInstance().setContext(context);
        executeTask();
    }

    private static void executeTask() {
        if (getInstance().getTaskRequestQueue().isEmpty()){
            TaskDataBase taskDataBase = new TaskDataBase(getInstance().getContext());
            if (taskDataBase.isEmpty(TaskContract.TABLE_NAME,TaskContract.Key_IdContact)){
                ArrayList<TaskRequest> requests = taskDataBase.getTasks(getInstance());
                if (!requests.isEmpty()){
                    for (TaskRequest request : requests){
                        getInstance().getTaskRequestQueue().add(request);
                    }
                }
            }
        }
        while (!getInstance().getTaskRequestQueue().isEmpty()){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(200);
                        getInstance().taskExecutor.setRequest(getInstance().getTaskRequestQueue().peek());
                        getInstance().taskExecutor.execute();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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

    @Override
    public void OnErrorTask(TaskRequest request) {
        TaskDataBase taskDataBase = new TaskDataBase(getContext());
        taskDataBase.batchTask(request);
        if (getInstance().getTaskRequestQueue().contains(request)){
            getInstance().getTaskRequestQueue().remove(request);
        }
        numberTaskRejected++;
    }

    @Override
    public void OnFinishedTask(int id) {

    }
}
