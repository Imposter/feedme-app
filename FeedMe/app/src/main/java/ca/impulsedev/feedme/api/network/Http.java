package ca.impulsedev.feedme.api.network;

import java.net.MalformedURLException;
import java.util.Map;

public class Http {
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

    public static HttpRequest createRequest(String url) {
        try {
            return new HttpRequest(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
