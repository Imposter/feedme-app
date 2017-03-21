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
import android.widget.TextView;

import java.util.List;

import ca.impulsedev.feedme.api.service.models.Place;

public class RestaurantSearchAdapter extends
        RecyclerView.Adapter<RestaurantSearchAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        private TextView mRestaurantRating;
        private TextView mRestaurantRatingText;
        private TextView mRestaurantDistance;
        private TextView mRestaurantName;
        private TextView mRestaurantLocation;
        private TextView mRestaurantDescription;

        ViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView)itemView.findViewById(R.id.restaurant_card_view);
            mRestaurantRating = (TextView)itemView.findViewById(R.id.restaurant_rating);
            mRestaurantRatingText = (TextView)itemView.findViewById(R.id.restaurant_rating_text);
            mRestaurantDistance = (TextView)itemView.findViewById(R.id.restaurant_distance);
            mRestaurantName = (TextView)itemView.findViewById(R.id.restaurant_name);
            mRestaurantLocation = (TextView)itemView.findViewById(R.id.restaurant_location);
            mRestaurantDescription = (TextView)itemView.findViewById(R.id.restaurant_description);
        }
    }

    private List<Place> mPlaces;
    private int mLastPosition = 0;

    public RestaurantSearchAdapter(List<Place> places) {
        mPlaces = places;
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
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
        Place place = mPlaces.get(position);

        // Create font
        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/material_icons.ttf");
        viewHolder.mRestaurantRating.setTypeface(font);

        // Set price range
        String priceRange = "Price range: ";
        for (int i = 0; i < place.price_level; i++) {
            priceRange += "$";
        }

        viewHolder.mRestaurantRatingText.setText(place.rating.toString());
        viewHolder.mRestaurantDistance.setText("0.0km");
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
}