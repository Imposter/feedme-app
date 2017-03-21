package ca.impulsedev.feedme.api.service;

import java.io.InputStream;

import ca.impulsedev.feedme.api.network.HttpStream;
import ca.impulsedev.feedme.api.service.models.Place;

public class Api {
    public static final String API_ADDRESS = "http://feedme.indigogames.ca";
    public static final int API_TIMEOUT = 2500;
    public static final int DOWNLOAD_TIMEOUT = 3000;

    /**
     * Argument classes
     */
    public static class SearchFoodImageArgs {
        public String food;
    }

    public static class SearchFoodImageResult {
        public String link;
    }

    public static class SearchNearbyFoodPlacesArgs {
        public String food;
        public double longitude;
        public double latitude;
        public double radius;
        public String token;
    }

    public static class SearchNearbyFoodPlacesResult {
        public Place[] nearby;
        public String next;
    }

    public static ServiceTask getFoodImage(String food,
                                               ServiceCallback<SearchFoodImageResult> callback) {
        SearchFoodImageArgs args = new SearchFoodImageArgs();
        args.food = food;

        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "search", "foodImage", args,
                SearchFoodImageArgs.class, SearchFoodImageResult.class, callback);
    }

    public static ServiceTask getNearbyFoodPlaces(String food, double latitude,
                                                      double longitude, double radius,
                                                      String previousToken,
                                                      ServiceCallback<SearchNearbyFoodPlacesResult>
                                                              callback) {
        SearchNearbyFoodPlacesArgs args = new SearchNearbyFoodPlacesArgs();
        args.food = food;
        args.longitude = longitude;
        args.latitude = latitude;
        args.radius = radius;
        args.token = previousToken;

        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "search", "nearbyFoodPlaces", args,
                SearchNearbyFoodPlacesArgs.class, SearchNearbyFoodPlacesResult.class, callback);
    }

    public static ServiceTask getNearbyFoodPlaces(String food, double latitude,
                                                      double longitude, double radius,
                                                      ServiceCallback<SearchNearbyFoodPlacesResult>
                                                              callback) {
        return getNearbyFoodPlaces(food, latitude, longitude, radius, null, callback);
    }

    public static ServiceTask downloadFile(String url, ServiceCallback<HttpStream> callback) {
        return ServiceCall.streamCall(url, DOWNLOAD_TIMEOUT, callback);
    }
}
