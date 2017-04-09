package ca.impulsedev.feedme.api.service.models;

/**
 * Extended business information
 */
public class PlaceDetails {
    public AddressComponent[] address_components;
    public String adr_address;
    public String formatted_address;
    public String formatted_phone_number;
    public Geometry geometry;
    public String icon;
    public String id;
    public String international_phone_number;
    public String name;
    public OpenHours opening_hours;
    public Photo[] photos;
    public String place_id;
    public int price_level;
    public Double rating;
    public String reference;
    public Review[] reviews;
    public String scope;
    public String[] types;
    public String url;
    public int utc_offset;
    public String vicinity;
    public String website;
}
