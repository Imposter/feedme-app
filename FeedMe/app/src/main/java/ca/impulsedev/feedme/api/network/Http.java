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

import java.net.MalformedURLException;
import java.util.Map;

/**
 * Used for synchronous HTTP communication where the response of the request is needed right
 * away. Contains helper methods to create HTTP requests.
 */
public class Http {
    /**
     * Creates an HTTP request which can be manipulated with headers and POST data
     * @param url Request URL
     * @param params Request params, serialized into a POST byte array
     * @return HTTP request object
     */
    public static HttpRequest createRequest(String url, Map<String, String> params) {
        String post = "";
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                post += entry.getKey() + "=" + entry.getValue() + "&";
            }
        }

        try {
            if (!post.isEmpty()) {
                return new HttpRequest(url, post);
            }
            return new HttpRequest(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an HTTP request which can be manipulated with headers and POST data
     * @param url Request URL
     * @param post POST data as string
     * @return HTTP request object
     */
    public static HttpRequest createRequest(String url, String post) {
        try {
            if (post != null && !post.isEmpty()) {
                return new HttpRequest(url, post);
            }
            return new HttpRequest(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an HTTP request which can be manipulated with headers and POST data
     * @param url Request URL
     * @param post POST data as byte array
     * @return HTTP request object
     */
    public static HttpRequest createRequest(String url, byte[] post) {
        try {
            if (post != null && post.length > 0) {
                return new HttpRequest(url, post);
            }
            return new HttpRequest(url);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Creates an HTTP request which can be manipulated with headers
     * @param url Request URL
     * @return HTTP request object
     */
    public static HttpRequest createRequest(String url) {
        try {
            return new HttpRequest(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
