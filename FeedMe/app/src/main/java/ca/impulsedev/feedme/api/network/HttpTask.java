/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme.api.network;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;

/**
 * Used for asynchronous HTTP communication. Wrapper class for HttpRequest returning streams instead
 * of HTTP responses
 */
public class HttpTask extends AsyncTask<HttpStream> {
    private static final int REQUEST_TIMEOUT = 1000;

    private static final ExecutorService sExecutor
            = Executors.newCachedThreadPool(Executors.defaultThreadFactory());
    private String mUrl;
    private byte[] mData;
    private int mTimeout;
    private Future<HttpResponse> mFutureResponse;

    /**
     * Default constructor
     */
    public HttpTask() {
        mUrl = "";
        mData = new byte[0];
        mTimeout = REQUEST_TIMEOUT;
    }

    /**
     * Initializes task with HTTP url and POST data
     * @param url Request URL
     * @param data Request POST data as string
     */
    public HttpTask(String url, String data) {
        mUrl = url;
        mData = data.getBytes();
    }

    /**
     * Initializes task with HTTP url and POST data
     * @param url Request URL
     * @param data Request POST data as byte array
     */
    public HttpTask(String url, byte[] data) {
        mUrl = url;
        mData = data;
    }

    /**
     * Initializes task with HTTP url and response timeout
     * @param url Request URL
     * @param timeout Request timeout
     */
    public HttpTask(String url, int timeout) {
        mUrl = url;
        mTimeout = timeout;
    }

    /**
     * Initializes task with HTTP url, POST data and response timeout
     * @param url Request URL
     * @param data Request POST data as string
     * @param timeout Request timeout
     */
    public HttpTask(String url, String data, int timeout) {
        mUrl = url;
        if (data != null)
            mData = data.getBytes();
        mTimeout = timeout;
    }

    /**
     * Initializes task with HTTP url, POST data and response timeout
     * @param url Request URL
     * @param data Request POST data as byte array
     * @param timeout Request timeout
     */
    public HttpTask(String url, byte[] data, int timeout) {
        mUrl = url;
        mData = data;
        mTimeout = timeout;
    }

    /**
     * Sets request URL
     * @param url HTTP request URL
     */
    public void setUrl(String url) {
        mUrl = url;
    }

    /**
     * Sets request POST data
     * @param data HTTP POST data
     */
    public void setData(String data) {
        mData = data.getBytes();
    }

    /**
     * Sets request POST data
     * @param data HTTP POST data
     */
    public void setData(byte[] data) {
        mData = data;
    }

    /**
     * Sets timeout
     * @param timeout HTTP request timeout
     */
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    /**
     * Processes request asynchronously
     * @return HTTP stream
     */
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

    /**
     * Cancels request
     */
    @Override
    public void cancel() {
        if (mFutureResponse != null) {
            mFutureResponse.cancel(true);
        }
        super.cancel();
    }
}
