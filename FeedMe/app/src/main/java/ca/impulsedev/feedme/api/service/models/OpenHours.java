/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme.api.service.models;

/**
 * Hours of operation for a business
 */
public class OpenHours {
    public boolean open_now;
    public Period[] periods;
    public String[] weekday_text;
}
