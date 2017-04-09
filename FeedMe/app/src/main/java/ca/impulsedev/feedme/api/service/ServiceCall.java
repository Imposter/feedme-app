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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.impulsedev.feedme.api.AsyncTaskResult;
import ca.impulsedev.feedme.api.network.HttpStream;
import ca.impulsedev.feedme.api.network.HttpTask;

/**
 * Used by API calls to get different types of results from server, either in streams, strings, or
 * class objects
 */
public class ServiceCall {
    private static final ExecutorService sExecutor
            = Executors.newCachedThreadPool(Executors.defaultThreadFactory());

    /**
     * Calls API component and command returning data as a class object in a callback
     * @param url API Url
     * @param timeout Request timeout (Milliseconds)
     * @param component API component
     * @param command Component command
     * @param args Argument class object
     * @param argsClass Type information of argument class object
     * @param resultClass Type information of result class object
     * @param callback Result callback for errors and results
     * @param <TArgs> Argument class type
     * @param <TResult> Result class type
     * @return API service task created
     */
    public static <TArgs, TResult>
    ServiceTask dataCall(String url, int timeout, String component, String command, TArgs args,
                         Class<TArgs> argsClass, final Class<TResult> resultClass,
                         final ServiceCallback<TResult> callback) {
        String requestUrl = String.format("%s/%s/%s", url, component, command);
        String data = null;
        if (args != null && argsClass != null) {
            Gson gson = new Gson();
            data = gson.toJson(args, argsClass);
        }

        HttpTask task = new HttpTask(requestUrl, data, timeout) {
            @Override
            protected void onBegin() {
                callback.onBegin();
            }

            @Override
            protected void onEnd(final AsyncTaskResult<HttpStream> result) {
                if (result != null && !result.hasError()) {
                    // Read stream
                    InputStreamReader inputStreamReader = new InputStreamReader(result.getResult());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    final StringBuilder builder = new StringBuilder();
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        ApiMessage<TResult> message = new ApiMessage<>(resultClass);
                        message.deserialize(builder.toString());

                        callback.onEnd(message.getCode(), message.getData());
                    } catch (Exception ex) {
                        callback.onError(ex);
                    }
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
        return new ServiceTask(task);
    }

    /**
     * Calls API component and command returning data as a stream in a callback
     * @param url API Url
     * @param timeout Request timeout (Milliseconds)
     * @param component API component
     * @param command Component command
     * @param args Argument class object
     * @param argsClass Type information of argument class object
     * @param callback Result callback for errors and results
     * @param <TArgs> Argument class type
     * @return API service task created
     */
    public static <TArgs>
    ServiceTask streamCall(String url, int timeout, String component, String command, TArgs args,
                           Class<TArgs> argsClass,
                           final ServiceCallback<HttpStream> callback) {
        String requestUrl = String.format("%s/%s/%s", url, component, command);
        String data = null;
        if (args != null && argsClass != null) {
            Gson gson = new Gson();
            data = gson.toJson(args, argsClass);
        }

        HttpTask task = new HttpTask(requestUrl, data, timeout) {
            @Override
            protected void onBegin() {
                callback.onBegin();
            }

            @Override
            protected void onEnd(AsyncTaskResult<HttpStream> result) {
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
        return new ServiceTask(task);
    }

    /**
     * Gets data from URL, returning data as a stream in a callback
     * @param url API Url
     * @param timeout Request timeout (Milliseconds)
     * @param callback Result callback for errors and results
     * @return API service task created
     */
    public static ServiceTask streamCall(String url, int timeout,
                                         final ServiceCallback<HttpStream> callback) {
        HttpTask task = new HttpTask(url, timeout) {
            @Override
            protected void onBegin() {
                callback.onBegin();
            }

            @Override
            protected void onEnd(AsyncTaskResult<HttpStream> result) {
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
        return new ServiceTask(task);
    }
}
