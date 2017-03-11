package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.protocol.ApiDataTask;

public class ServiceDataTask {
    private ApiDataTask mTask;

    public ServiceDataTask(ApiDataTask task) {
        mTask = task;
    }

    public void cancel() {
        mTask.cancel();
    }

    public boolean isCancelled() {
        return mTask.isCancelled();
    }
}
