package ca.impulsedev.feedme.api.service;


import ca.impulsedev.feedme.api.network.HttpTask;

public class ServiceTask {
    private HttpTask mTask;

    public ServiceTask(HttpTask task) {
        mTask = task;
    }

    public void cancel() {
        mTask.cancel();
    }

    public boolean isCancelled() {
        return mTask.isCancelled();
    }

    public boolean isRunning() {
        return mTask.isRunning();
    }
}
