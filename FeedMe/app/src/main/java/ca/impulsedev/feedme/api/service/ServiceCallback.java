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
 * An interface for implementing different callback outcomes for a Service call
 * @param <TResult> API result data type
 */
public class ServiceCallback<TResult> {
    /**
     * Called when the request begins
     */
    protected void onBegin() {
    }

    /**
     * Called when the request ends, along with the result from the API
     * @param result Call result data
     */
    protected void onEnd(TResult result) {
    }

    /**
     * Called when the request ends, along with the success/error code and result from the API
     * @param code Success/error code
     * @param result Call result data
     */
    protected void onEnd(int code, TResult result) {
    }

    /**
     * Called if the request was cancelled
     */
    protected void onCancelled() {
    }

    /**
     * Called if there was an error processing the request, along with the exception information
     * @param ex Exception information
     */
    protected void onError(Exception ex) {
    }
}
