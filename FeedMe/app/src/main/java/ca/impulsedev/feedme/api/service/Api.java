/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme.api.service;

import ca.impulsedev.feedme.api.network.HttpStream;
import ca.impulsedev.feedme.api.service.models.Place;
import ca.impulsedev.feedme.api.service.models.PlaceDetails;

/**
 * API for communication with Feed Me! backend using components and commands. Messages are
 * serialized in argument and result classes
 */
public class Api {
    public static final String API_ADDRESS = "http://feedme.indigogames.ca";
    public static final int API_TIMEOUT = 3500;
    public static final int DOWNLOAD_TIMEOUT = 5000;

    /**
     * System::Version request result
     */
    public static class GetVersionInfoResult {
        public String version;
    }

    /**
     * Search::FoodImage request arguments
     */
    public static class SearchFoodImageArgs {
        public String food;
    }

    /**
     * Search::FoodImage request result
     */
    public static class SearchFoodImageResult {
        public String link;
    }

    /**
     * Search::NearbyFoodPlaces request arguments
     */
    public static class SearchNearbyFoodPlacesArgs {
        public String food;
        public double longitude;
        public double latitude;
        public String token;
    }

    /**
     * Search::NearbyFoodPlaces request result
     */
    public static class SearchNearbyFoodPlacesResult {
        public Place[] nearby;
        public String next;
    }

    /**
     * Search::GetPlaceInfo request arguments
     */
    public static class GetPlaceInfoArgs {
        public String place;
    }

    /**
     * Search::GetPlaceInfo request result
     */
    public static class GetPlaceInfoResult {
        public PlaceDetails result;
    }

    /**
     * Get server version asynchronously and return result in callback
     * @param callback Callback class for version info result
     * @return API service task created
     */
    public static ServiceTask getVersion(ServiceCallback<GetVersionInfoResult> callback) {
        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "system", "version", null, null,
                GetVersionInfoResult.class, callback);
    }

    /**
     * Get food image url asynchronously and return result in callback
     * @param food Food name
     * @param callback Callback class for food image result
     * @return API service task created
     */
    public static ServiceTask getFoodImage(String food,
                                           ServiceCallback<SearchFoodImageResult> callback) {
        SearchFoodImageArgs args = new SearchFoodImageArgs();
        args.food = food;

        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "search", "foodImage", args,
                SearchFoodImageArgs.class, SearchFoodImageResult.class, callback);
    }

    /**
     * Get nearby food places which offer the specified food in a 50KM radius
     * @param food Food name
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param previousToken Pagination token
     * @param callback Callback class for food places result
     * @return API service task created
     */
    public static ServiceTask getNearbyFoodPlaces(String food, double latitude,
                                                  double longitude, String previousToken,
                                                  ServiceCallback<SearchNearbyFoodPlacesResult>
                                                          callback) {
        SearchNearbyFoodPlacesArgs args = new SearchNearbyFoodPlacesArgs();
        args.food = food;
        args.longitude = longitude;
        args.latitude = latitude;
        args.token = previousToken;

        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "search", "nearbyFoodPlaces", args,
                SearchNearbyFoodPlacesArgs.class, SearchNearbyFoodPlacesResult.class, callback);
    }

    /**
     * Get nearby food places which offer the specified food in a 50KM radius
     * @param food Food name
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @param callback Callback class for food places result
     * @return API service task created
     */
    public static ServiceTask getNearbyFoodPlaces(String food, double latitude,
                                                  double longitude,
                                                  ServiceCallback<SearchNearbyFoodPlacesResult>
                                                          callback) {
        return getNearbyFoodPlaces(food, latitude, longitude, null, callback);
    }

    /**
     * Get detailed place info
     * @param placeId Place identification string
     * @param callback Callback class for place information result
     * @return API service task created
     */
    public static ServiceTask getPlaceInfo(String placeId, ServiceCallback<GetPlaceInfoResult>
            callback) {
        GetPlaceInfoArgs args = new GetPlaceInfoArgs();
        args.place = placeId;

        return ServiceCall.dataCall(API_ADDRESS, API_TIMEOUT, "search", "placeInfo", args,
                GetPlaceInfoArgs.class, GetPlaceInfoResult.class, callback);
    }

    /**
     * Download a file asynchronously
     * @param url URL to download
     * @param callback Callback class for the stream for the downloaded file
     * @return API service task created
     */
    public static ServiceTask downloadFile(String url, ServiceCallback<HttpStream> callback) {
        return ServiceCall.streamCall(url, DOWNLOAD_TIMEOUT, callback);
    }
}
