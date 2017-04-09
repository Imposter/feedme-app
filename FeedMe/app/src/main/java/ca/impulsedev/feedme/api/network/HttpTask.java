package ca.impulsedev.feedme.api.network;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;

public class HttpTask extends AsyncTask<HttpStream> {
    private static final int REQUEST_TIMEOUT = 1000;

    private static final ExecutorService sExecutor
            = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    private String mUrl;
    private byte[] mData;
    private int mTimeout;
    private Future<HttpResponse> mFutureResponse;

    public HttpTask() {
        mUrl = "";
        mData = new byte[0];
        mTimeout = REQUEST_TIMEOUT;
    }

    public HttpTask(String url, String data) {
        mUrl = url;
        mData = data.getBytes();
    }

    public HttpTask(String url, byte[] data) {
        mUrl = url;
        mData = data;
    }

    public HttpTask(String url, int timeout) {
        mUrl = url;
        mTimeout = timeout;
    }

    public HttpTask(String url, String data, int timeout) {
        mUrl = url;
        if (data != null)
            mData = data.getBytes();
        mTimeout = timeout;
    }

    public HttpTask(String url, byte[] data, int timeout) {
        mUrl = url;
        mData = data;
        mTimeout = timeout;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setData(String data) {
        mData = data.getBytes();
    }

    public void setData(byte[] data) {
        mData = data;
    }

    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    @Override
    protected AsyncTaskResult<HttpStream> process() {
        // End if task was cancelled
        if (isCancelled())
            return null;

        // Check if a request already exists
        if (mFutureResponse != null) {
            return null;
        }

        try {
            // Create request
            final HttpRequest request = new HttpRequest(mUrl, mData);
            request.setTimeout(mTimeout);

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
            HttpStream stream = new HttpStream(response.getStream());

            // Return stream
            return new AsyncTaskResult(stream);
        } catch (Exception ex) {
            return new AsyncTaskResult(ex);
        }
    }

    @Override
    public void cancel() {
        if (mFutureResponse != null) {
            mFutureResponse.cancel(true);
        }
        super.cancel();
    }
}
