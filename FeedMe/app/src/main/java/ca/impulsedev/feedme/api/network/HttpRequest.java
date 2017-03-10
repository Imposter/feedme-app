package ca.impulsedev.feedme.api.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequest {
    public static final int DEFAULT_TIMEOUT = 10000;

    private URL mUrl;
    private byte[] mPost;
    private HttpURLConnection mConnection;
    private HttpResponse mResponse;
    private int mTimeout = DEFAULT_TIMEOUT;

    public HttpRequest(String url) throws MalformedURLException {
        mUrl = new URL(url);
        mPost = null;
    }

    public HttpRequest(String url, byte[] post) throws MalformedURLException {
        mUrl = new URL(url);
        mPost = post;
    }

    public HttpRequest(String url, String post) throws MalformedURLException {
        mUrl = new URL(url);
        if (post != null && !post.isEmpty()) {
            mPost = post.getBytes();
        }
    }

    public void setTimeout(int timeout) {
        mTimeout = timeout;
    }

    public int getTimeout() {
        return mTimeout;
    }

    public String getUrl() {
        return mUrl.toString();
    }

    public void close() {
        if (mConnection != null) {
            mConnection.disconnect();
            mConnection = null;
        }
    }

    public HttpResponse getResponse() throws IOException {
        if (mConnection != null) {
            return null;
        }

        if (mResponse != null && mResponse.isOpen()) {
            return mResponse;
        }

        mConnection = (HttpURLConnection)mUrl.openConnection();
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
