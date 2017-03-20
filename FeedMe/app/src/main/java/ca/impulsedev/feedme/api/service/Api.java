package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.service.models.Place;

public class Api {
    public static String API_ADDRESS = "http://192.168.1.175:5050";

    /**
     * Argument classes
     */
    public static class SearchFoodImageArgs {
        public String food;
    }

    public static class SearchNearbyFoodPlacesArgs {
        public String food;
        public double longitude;
        public double latitude;
        public double radius;
        public int limit;
        public String token;
    }

    public static class SearchNearbyFoodPlacesResult {
        public Place[] nearby;
        public String next;
    }

    public static ServiceDataTask getNearbyFoodPlaces(String food, double latitude,
                                                      double longitude, double radius, int limit,
                                                      String previousToken,
                                                      ServiceCallback<SearchNearbyFoodPlacesResult>
                                                              callback) {
        SearchNearbyFoodPlacesArgs args = new SearchNearbyFoodPlacesArgs();
        args.food = food;
        args.longitude = longitude;
        args.latitude = latitude;
        args.radius = radius;
        args.limit = limit;
        args.token = previousToken;

        return ServiceCall.dataCall(API_ADDRESS, "search", "nearbyFoodPlaces", args,
                SearchNearbyFoodPlacesArgs.class, SearchNearbyFoodPlacesResult.class, callback);
    }

    public static ServiceDataTask getNearbyFoodPlaces(String food, double latitude,
                                                      double longitude, double radius, int limit,
                                                      ServiceCallback<SearchNearbyFoodPlacesResult>
                                                              callback) {
        return getNearbyFoodPlaces(food, latitude, longitude, radius, limit, null, callback);
    }
}
