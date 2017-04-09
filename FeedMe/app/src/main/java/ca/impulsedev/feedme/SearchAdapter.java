package ca.impulsedev.feedme;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.text.DecimalFormat;

import ca.impulsedev.feedme.api.service.models.Place;

class SearchAdapter extends
        RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mRestaurantRating;
        private TextView mRestaurantRatingText;
        private TextView mRestaurantDistance;
        private TextView mRestaurantName;
        private TextView mRestaurantLocation;
        private TextView mRestaurantDescription;

        ViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.restaurant_card_view);
            mRestaurantRating = (TextView) itemView.findViewById(R.id.restaurant_rating);
            mRestaurantRatingText = (TextView) itemView.findViewById(R.id.restaurant_rating_text);
            mRestaurantDistance = (TextView) itemView.findViewById(R.id.restaurant_distance);
            mRestaurantName = (TextView) itemView.findViewById(R.id.restaurant_name);
            mRestaurantLocation = (TextView) itemView.findViewById(R.id.restaurant_location);
            mRestaurantDescription = (TextView) itemView.findViewById(R.id.restaurant_description);
        }
    }

    private static final double RADIUS_OF_EARTH = 6378.1; // Kilometers

    private MainActivity mActivity;
    private int mLastPosition = 0;

    SearchAdapter(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public int getItemCount() {
        return mActivity.getPlaces().size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.search_restaurant_result, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        Context context = viewHolder.mCardView.getContext();
        final Place place = mActivity.getPlaces().get(position);

        // Open details when the card is clicked
        viewHolder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE
                );
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                mActivity.showDetailsForPlace(place);
            }
        });

        // Create font
        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/material_icons.ttf");
        viewHolder.mRestaurantRating.setTypeface(font);

        // Set price range
        String priceRange = "Price range: ";
        for (int i = 0; i < place.price_level; i++) {
            priceRange += "$";
        }

        // Calculate distance
        double distance = getDistanceBetweenLocations(mActivity.getCurrentLocation().getLatitude(),
                mActivity.getCurrentLocation().getLongitude(), place.geometry.location.lat,
                place.geometry.location.lng);
        String distanceString;
        if (distance < 1000) {
            distanceString = new DecimalFormat("0.0").format(distance) + "m";
        } else {
            distanceString = new DecimalFormat("0.0").format(distance / 1000) + "km";
        }

        viewHolder.mRestaurantRatingText.setText(place.rating != null
                ? place.rating.toString() : "");
        viewHolder.mRestaurantDistance.setText(distanceString);
        viewHolder.mRestaurantName.setText(place.name);
        viewHolder.mRestaurantLocation.setText(place.vicinity);
        viewHolder.mRestaurantDescription.setText(priceRange);

        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewHolder.mCardView.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static double getDistanceBetweenLocations(double latitude1, double longitude1,
                                                      double latitude2, double longitude2) {
        double latitude = Math.toRadians(latitude2 - latitude1);
        double longitude = Math.toRadians(longitude2 - longitude1);

        double a = Math.pow(Math.sin(latitude / 2), 2)
                + Math.cos(Math.toRadians(latitude1))
                * Math.cos(Math.toRadians(latitude2))
                * Math.pow(Math.sin(longitude / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return RADIUS_OF_EARTH * c * 1000; // Meters
    }
}