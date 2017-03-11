package ca.impulsedev.feedme.api.service;

public class ServiceCallback<TResult> {
    protected void onBegin() {
    }

    protected void onEnd(TResult result) {
    }

    protected void onEnd(int code, TResult result) {
    }

    protected void onCancelled() {
    }

    protected void onError(Exception ex) {
    }
}
