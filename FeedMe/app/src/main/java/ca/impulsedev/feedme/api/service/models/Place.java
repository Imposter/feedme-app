package ca.impulsedev.feedme.api.service.models;

/**
 * Basic business information
 */
public class Place {
    public Geometry geometry;
    public String icon;
    public String id;
    public String name;
    public OpenHours opening_hours;
    public Photo[] photos;
    public String place_id;
    public int price_level;
    public Double rating;
    public String reference;
    public String scope;
    public String[] types;
    public String vicinity;
}
