package ca.impulsedev.feedme.api.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class HttpResponse {
    private HttpRequest mRequest;
    private HttpURLConnection mConnection;
    private int mCode;
    private InputStream mStream;
    private boolean mOpen;

    public HttpResponse(HttpRequest request, HttpURLConnection connection, int code,
                        InputStream stream) {
        mRequest = request;
        mConnection = connection;
        mStream = stream;
        mCode = code;
        mOpen = true;
    }

    public void close() throws IOException {
        mStream.close();
        mConnection.disconnect();
        mOpen = false;
    }

    public HttpRequest getRequest() {
        return mRequest;
    }

    public InputStream getStream() {
        return mStream;
    }

    public int getCode() {
        return mCode;
    }

    public boolean isOpen() {
        return mOpen;
    }
}
