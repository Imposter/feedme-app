package ca.impulsedev.feedme.api.service;

public enum ApiResponseCode {
    SUCCESS,
    ERROR,

    // Search
    SEARCH,
    SEARCH_INVALID_PARAMETERS,
    SEARCH_FOOD_IMAGE_NOT_FOUND,
    SEARCH_FOOD_IMAGE_ERROR;
}
