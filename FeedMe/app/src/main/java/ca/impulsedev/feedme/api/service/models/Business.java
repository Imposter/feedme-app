package ca.impulsedev.feedme.api.service.models;

public class Business {
    public String id;
    public String name;
    public String image_url;
    public boolean is_closed;
    public String url;
    public String price;
    public String phone;
    public double rating;
    public int review_count;
    public double distance;
    public Category[] categories;
    public Coordinates coordinates;
    public Location location;
    public String[] transactions;
}
