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
 * Reviews for a business
 */
public class Review {
    public Aspect[] aspects;
    public String author_name;
    public String author_url;
    public String language;
    public String profile_photo_url;
    public int rating;
    public String relative_time_description;
    public String text;
    public int time;
}
