package ca.impulsedev.feedme.api.service;

public enum ApiResponseCode {
    SUCCESS,
    ERROR,

    // Search
    SEARCH_INVALID_PARAMETERS,
    SEARCH_FOOD_IMAGE_NOT_FOUND,
    SEARCH_FOOD_IMAGE_ERROR,

    // Place
    PLACE_INVALID_PARAMETERS,
}
