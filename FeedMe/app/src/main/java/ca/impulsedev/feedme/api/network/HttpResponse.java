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
import java.net.HttpURLConnection;

/**
 * Used for HTTP communication, contains response data for a request, containing the HTTP code,
 * stream, and whether the connection is open
 */
public class HttpResponse {
    private HttpRequest mRequest;
    private HttpURLConnection mConnection;
    private int mCode;
    private InputStream mStream;
    private boolean mOpen;

    /**
     * Initializes response with request, HTTP connection, the response code and input stream
     * @param request HTTP request
     * @param connection HTTP connection
     * @param code HTTP response code
     * @param stream HTTP data stream
     */
    public HttpResponse(HttpRequest request, HttpURLConnection connection, int code,
                        InputStream stream) {
        mRequest = request;
        mConnection = connection;
        mStream = stream;
        mCode = code;
        mOpen = true;
    }

    /**
     * Closes connection
     * @throws IOException Thrown if the connection failed to close
     */
    public void close() throws IOException {
        mStream.close();
        mConnection.disconnect();
        mOpen = false;
    }

    /**
     * Gets request
     * @return HTTP request
     */
    public HttpRequest getRequest() {
        return mRequest;
    }

    /**
     * Gets response data stream
     * @return HTTP data stream
     */
    public InputStream getStream() {
        return mStream;
    }

    /**
     * Gets HTTP response code
     * @return HTTP response code
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Connection is open or not
     * @return Whether the response connection is open or not
     */
    public boolean isOpen() {
        return mOpen;
    }
}
