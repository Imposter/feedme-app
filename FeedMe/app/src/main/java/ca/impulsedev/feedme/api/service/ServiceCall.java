package ca.impulsedev.feedme.api.service;

import java.io.InputStream;

import ca.impulsedev.feedme.api.AsyncTaskResult;
import ca.impulsedev.feedme.api.protocol.ApiDataTask;
import ca.impulsedev.feedme.api.protocol.ApiMessage;
import ca.impulsedev.feedme.api.protocol.ApiStreamTask;

public class ServiceCall {
    public static <TArgs, TResult>
    ServiceDataTask dataCall(String url, String component, String command, TArgs args,
                             Class<TArgs> argsClass, Class<TResult> resultClass,
                             final ServiceCallback<TResult> callback) {
        ApiDataTask<TArgs, TResult> task = new ApiDataTask<TArgs, TResult>(url, component, command,
                args, argsClass, resultClass) {
            @Override
            protected void onBegin() {
                callback.onBegin();
            }

            @Override
            protected void onEnd(AsyncTaskResult<ApiMessage<TResult>> result) {
                if (result != null && !result.hasError()) {
                    callback.onEnd(result.getResult().getCode(), result.getResult().getData());
                } else {
                    callback.onError(result.getException());
                }
            }

            @Override
            protected void onCancelled() {
                callback.onCancelled();
            }
        };
        task.execute();
        return new ServiceDataTask(task);
    }

    public static <TArgs>
    ServiceStreamTask streamCall(String url, String component, String command, TArgs args,
                                 Class<TArgs> argsClass,
                                 final ServiceCallback<InputStream> callback) {
        ApiStreamTask<TArgs> task = new ApiStreamTask<TArgs>(url, component, command, args,
                argsClass) {
            @Override
            protected void onBegin() {
                callback.onBegin();
            }

            @Override
            protected void onEnd(AsyncTaskResult<InputStream> result) {
                if (result != null && !result.hasError()) {
                    callback.onEnd(result.getResult());
                } else {
                    callback.onError(result.getException());
                }
            }

            @Override
            protected void onCancelled() {
                callback.onCancelled();
            }
        };
        task.execute();
        return new ServiceStreamTask(task);
    }
}
