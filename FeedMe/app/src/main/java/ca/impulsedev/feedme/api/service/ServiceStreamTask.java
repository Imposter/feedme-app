package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.protocol.ApiStreamTask;

public class ServiceStreamTask {
    private ApiStreamTask mTask;

    public ServiceStreamTask(ApiStreamTask task) {
        mTask = task;
    }

    public void cancel(boolean interruptIfRunning) {
        mTask.cancel(interruptIfRunning);
    }

    public void cancel() {
        mTask.cancel(true);
    }

    public boolean isCancelled() {
        return mTask.isCancelled();
    }
}
