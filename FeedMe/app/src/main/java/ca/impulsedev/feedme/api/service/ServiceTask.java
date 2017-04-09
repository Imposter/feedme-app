/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.network.HttpTask;

/**
 * Wraps around HttpTask to encapsulate internal task information and provide a simple interface
 * to the API
 */
public class ServiceTask {
    private HttpTask mTask;

    /**
     * Initializes instance with HttpTask
     * @param task Internal task
     */
    public ServiceTask(HttpTask task) {
        mTask = task;
    }

    /**
     * Cancels request
     */
    public void cancel() {
        mTask.cancel();
    }

    /**
     * Request was cancelled or not
     * @return Whether the request was cancelled or not
     */
    public boolean isCancelled() {
        return mTask.isCancelled();
    }

    /**
     * Task is running or not
     * @return Whether the task is running or not
     */
    public boolean isRunning() {
        return mTask.isRunning();
    }
}
