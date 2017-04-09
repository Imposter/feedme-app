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

/**
 * API response codes, used to identify how the server responded to a particular request
 */
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
