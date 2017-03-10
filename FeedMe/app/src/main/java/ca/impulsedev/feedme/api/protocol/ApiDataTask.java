package ca.impulsedev.feedme.api.protocol;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;
import ca.impulsedev.feedme.api.network.HttpRequest;
import ca.impulsedev.feedme.api.network.HttpResponse;

public class ApiDataTask<TArgs, TResult> extends AsyncTask<ApiMessage<TResult>> {
    private static final int REQUEST_TIMEOUT = 2500;

    private static final ExecutorService sExecutor
            = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    private String mUrl;
    private String mData;
    private Class<TResult> mResultClass;
    private Future<HttpResponse> mFutureResponse;

    public ApiDataTask(String url, String component, String command, TArgs args,
                       Class<TArgs> argsClass, Class<TResult> resultClass) {
        // Store result class
        mResultClass = resultClass;

        // Build URL
        mUrl = String.format("%s/%s/%s", url, component, command);

        // Serialize data
        if (args != null && argsClass != null) {
            Gson gson = new Gson();
            mData = gson.toJson(args, argsClass);
        }
    }

    public ApiDataTask(String url, String component, String command, Class<TResult> resultClass) {
        // Store result class
        mResultClass = resultClass;

        // Build URL
        mUrl = String.format("%s/%s/%s", url, component, command);
    }

    @Override
    protected AsyncTaskResult<ApiMessage<TResult>> process() {
        // End if task was cancelled
        if (isCancelled()) {
            return null;
        }

        // Check if a request already exists
        if (mFutureResponse != null) {
            return null;
        }

        try {
            // Create request
            final HttpRequest request = new HttpRequest(mUrl, mData);
            request.setTimeout(REQUEST_TIMEOUT);

            // Process request
            mFutureResponse = sExecutor.submit(new Callable<HttpResponse>() {
                @Override
                public HttpResponse call() throws Exception {
                    return request.getResponse();
                }
            });

            HttpResponse response = mFutureResponse.get();
            mFutureResponse = null;

            // Read stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            // Decode message
            ApiMessage<TResult> message = new ApiMessage<>(mResultClass);
            message.deserialize(builder.toString());

            // Return deserialized class object
            return new AsyncTaskResult(message);
        } catch (Exception ex) {
            return new AsyncTaskResult(ex);
        }
    }

    @Override
    public void cancel() {
        mFutureResponse.cancel(true);
        super.cancel();
    }
}
