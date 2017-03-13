package com.softwaremobility.taskmanager;

import android.os.AsyncTask;

import com.softwaremobility.listeners.TaskManagerListener;
import com.softwaremobility.models.TaskRequest;

/**
 * Created by darkgeat on 3/12/17.
 */

public class TaskModelThread extends AsyncTask<Void,Void,Boolean> {

    private TaskRequest request;
    private TaskManagerListener listener;

    public TaskModelThread(TaskRequest request, TaskManagerListener listener){
        this.request = request;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        request.functionality(request.getId());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result){
            if (listener != null){
                listener.OnFinishedTask(request);
                if (request.getListener() != null){
                    request.getListener().OnFinishedTask(request.getId());
                }
            }
        }
    }
}
