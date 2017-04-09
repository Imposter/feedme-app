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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Used for HTTP communication. HTTP requests contain url and POST data which is submitted when
 * requesting a response from the server specified.
 */
public class HttpRequest {
    public static final int DEFAULT_TIMEOUT = 10000;

    private URL mUrl;
    private byte[] mPost;
    private HttpURLConnection mConnection;
    private HttpResponse mResponse;
    private int mTimeout = DEFAULT_TIMEOUT;

    /**
     * Constructor initializing request with specified URL
     * @param url Request URL
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url) throws MalformedURLException {
        mUrl = new URL(url);
        mPost = null;
    }

    /**
     * Constructor initializing request with specified URL
     * @param url Request URL
     * @param post POST data in byte array
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url, byte[] post) throws MalformedURLException {
        mUrl = new URL(url);
        mPost = post;
    }

    /**
     * Constructor initializing request with specified URL
     * @param url Request URL
     * @param post POST data in string
     * @throws MalformedURLException Thrown when URL is malformed
     */
    public HttpRequest(String url, String post) throws MalformedURLException {
        mUrl = new URL(url);
        if (post != null && !post.isEmpty()) {
            mPost = post.getBytes();
        }
    }

    /**
     * Sets request timeout
     * @param timeout Request timeout
     */
    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    /**
     * Gets request timeout
     * @return Request timeout
     */
    public int getTimeout() {
        return mTimeout;
    }

    /**
     * Gets URL
     * @return Request URL
     */
    public String getUrl() {
        return mUrl.toString();
    }

    /**
     * Closes request connection
     */
    public void close() {
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
    }

    /**
     * Gets response for HTTP request
     * @return HTTP response
     * @throws IOException Thrown when connection is not opened successfully
     */
    public HttpResponse getResponse() throws IOException {
        if (mConnection != null) {
            return null;
        }

        if (mResponse != null && mResponse.isOpen()) {
            return mResponse;
        }

        mConnection = (HttpURLConnection) mUrl.openConnection();
        mConnection.setConnectTimeout(mTimeout);
        mConnection.setReadTimeout(mTimeout);

        if (mPost != null && mPost.length > 0) {
            mConnection.setDoOutput(true);
            mConnection.setFixedLengthStreamingMode(mPost.length);
            mConnection.setRequestMethod("POST");

            OutputStream outputStream = mConnection.getOutputStream();
            outputStream.write(mPost);
        }

        InputStream inputStream = mConnection.getInputStream();
        int code = mConnection.getResponseCode();

        return (mResponse = new HttpResponse(this, mConnection, code, inputStream));
    }
}
