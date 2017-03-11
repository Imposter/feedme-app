package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.service.models.Business;

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
    }

    public static class SearchNearbyFoodPlacesResult {
        public Business[] nearby;
    }

    public static ServiceDataTask getNearbyFoodPlaces(String food, double latitude,
                                                      double longitude, double radius, int limit,
                                                      ServiceCallback<SearchNearbyFoodPlacesResult>
                                                              callback) {
        SearchNearbyFoodPlacesArgs args = new SearchNearbyFoodPlacesArgs();
        args.food = food;
        args.longitude = longitude;
        args.latitude = latitude;
        args.radius = radius;
        args.limit = limit;

        return ServiceCall.dataCall(API_ADDRESS, "search", "nearbyFoodPlaces", args,
                SearchNearbyFoodPlacesArgs.class, SearchNearbyFoodPlacesResult.class, callback);
    }
}
