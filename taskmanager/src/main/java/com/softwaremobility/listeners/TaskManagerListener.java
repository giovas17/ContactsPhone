package com.softwaremobility.listeners;

import com.softwaremobility.models.TaskRequest;

/**
 * Created by darkgeat on 3/13/17.
 */

public interface TaskManagerListener {
    void OnFinishedTask(TaskRequest request);
    void OnErrorTask(TaskRequest request);
}
