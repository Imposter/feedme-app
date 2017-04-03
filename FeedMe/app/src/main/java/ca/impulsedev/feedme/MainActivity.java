package ca.impulsedev.feedme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;
import ca.impulsedev.feedme.api.service.ServiceTask;
import ca.impulsedev.feedme.api.service.models.Place;
import ca.impulsedev.feedme.ui.ViewUtils;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String SEARCH_HISTORY_FILE = "searches.txt";

    private static final int PERMISSION_REQUEST_LOCATION = 100;

    private static final int LOCATION_REQUEST_DELAY = 2500;
    private static final int LOCATION_REQUEST_DISTANCE = 25;
    private static final int LOCATION_TIMEOUT = 1000 * 60 * 2;

    private List<Place> mPlaces;
    private SearchAdapter mAdapter;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    private LocationManager mLocationManager;
    private Location mCurrentLocation;

    private View mSearchView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AutoCompleteTextView mSearchText;
    private List<String> mPreviousSearches;
    private ArrayAdapter<String> mSearchAdapter;
    private ServiceTask mSearchNearbyPlacesTask;
    private String mLastToken;

    private View mRestaurantView;
    private ScrollView mRestaurantScroll;
    private TextView mRestaurantName;
    private RatingBar mRestaurantRating;
    private View mRestaurantCallView;
    private TextView mRestaurantPhoneNumber;
    private View mRestaurantDirectionsView;
    private TextView mRestaurantLocation;
    private TextView mRestaurantHours;
    private ServiceTask mGetPlaceInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlaces = new ArrayList<>();
        mAdapter = new SearchAdapter(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        // Search for more once the recycler view scrolled to the bottom
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = mLinearLayoutManager.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int pastVisibleItems = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    if (mSearchNearbyPlacesTask == null && mLastToken != null) {
                        // Search more
                        doSearch();
                    }
                }
            }
        });

        mSearchView = findViewById(R.id.search_view);
        mSearchText = (AutoCompleteTextView) findViewById(R.id.search_text);
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // Clear current results
                mPlaces.clear();
                mAdapter.notifyDataSetChanged();

                doSearch();

                return true;
            }
        });

        // Add previous searches to array list
        mPreviousSearches = new ArrayList<>();
        mSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                mPreviousSearches);
        mSearchText.setAdapter(mSearchAdapter);

        // Read search history file and add it to the previous search list
        loadSearches();

        // Notify adapter that the data set has changed and we need to update the UI
        mSearchAdapter.notifyDataSetChanged();

        mRestaurantView = findViewById(R.id.restaurant_view);
        mRestaurantScroll = (ScrollView) findViewById(R.id.restaurant_info_scroll);
        mRestaurantName = (TextView) findViewById(R.id.restaurant_info_name);
        mRestaurantRating = (RatingBar) findViewById(R.id.restaurant_info_rating);
        mRestaurantCallView = findViewById(R.id.restaurant_info_call);
        mRestaurantPhoneNumber = (TextView) findViewById(R.id.restaurant_info_phone_number);
        mRestaurantDirectionsView = findViewById(R.id.restaurant_info_directions);
        mRestaurantLocation = (TextView) findViewById(R.id.restaurant_info_location);
        mRestaurantHours = (TextView) findViewById(R.id.restaurant_info_hours);

        // Request permissions for location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, PERMISSION_REQUEST_LOCATION);
        }
    }

    private void doSearch() {
        // If we're already searching, cancel previous request
        if (mSearchNearbyPlacesTask != null && mSearchNearbyPlacesTask.isRunning()) {
            mSearchNearbyPlacesTask.cancel();
        }

        // If we don't currently have a location fix, use last known position
        Location location = mCurrentLocation;
        if (mCurrentLocation == null) {
            try {
                location = mLocationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
            } catch (SecurityException ex) {
                ex.printStackTrace();

                // Notify user that this application requires location permission
                Toast.makeText(this, R.string.permission_location_required_fail,
                        Toast.LENGTH_LONG).show();
            }
        }

        // Check if location is null
        if (location == null) {
            // Unable to get location
            Toast.makeText(this, R.string.error_location_fail,
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Set current location
        mCurrentLocation = location;

        final String searchText = mSearchText.getText().toString();
        mSearchNearbyPlacesTask = Api.getNearbyFoodPlaces(searchText, location.getLatitude(),
                location.getLongitude(), mLastToken,
                new ServiceCallback<Api.SearchNearbyFoodPlacesResult>() {
                    @Override
                    protected void onBegin() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onEnd(int code,
                                         Api.SearchNearbyFoodPlacesResult result) {
                        mProgressBar.setVisibility(View.GONE);
                        if (result != null) {
                            mPlaces.addAll(Arrays.asList(result.nearby));
                            mAdapter.notifyDataSetChanged();

                            mLastToken = result.next;

                            // Add string to search history
                            if (!mPreviousSearches.contains(searchText))
                                mPreviousSearches.add(searchText);
                            updateSearches();
                        }

                        mSearchNearbyPlacesTask = null;
                    }

                    @Override
                    protected void onError(Exception ex) {
                        ex.printStackTrace();

                        mProgressBar.setVisibility(View.GONE);
                        mSearchNearbyPlacesTask = null;

                        Toast.makeText(MainActivity.this, R.string.error_unable_to_get_data,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onCancelled() {
                        mSearchNearbyPlacesTask = null;
                        mLastToken = null;
                    }
                }
        );
    }

    public void showDetailsForPlace(Place place) {
        // Get details for place
        mGetPlaceInfoTask = Api.getPlaceInfo(place.place_id,
                new ServiceCallback<Api.GetPlaceInfoResult>() {
                    @Override
                    protected void onBegin() {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onEnd(int code,
                                         final Api.GetPlaceInfoResult result) {
                        mProgressBar.setVisibility(View.GONE);
                        if (result != null) {
                            // Set restaurant text
                            mRestaurantName.setText(result.result.name);
                            mRestaurantRating.setRating(result.result.rating.floatValue());
                            mRestaurantLocation.setText(result.result.formatted_address);
                            mRestaurantPhoneNumber.setText(result.result.formatted_phone_number);
                            mRestaurantHours.setText(TextUtils.join("\n",
                                    result.result.opening_hours.weekday_text
                            ));

                            // Update buttons
                            mRestaurantCallView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                    dialIntent.setData(Uri.parse(String.format("tel:%s",
                                            result.result.formatted_phone_number)));
                                    startActivity(dialIntent);
                                }
                            });
                            mRestaurantDirectionsView.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent mapsIntent = new Intent(Intent.ACTION_VIEW);
                                            mapsIntent.setData(Uri.parse(String.format(
                                                    "geo:0,0?q=%s", result.result.formatted_address
                                            )));
                                            mapsIntent.setPackage("com.google.android.apps.maps");
                                            startActivity(mapsIntent);
                                        }
                                    });

                            // Scroll to top
                            mRestaurantScroll.scrollTo(0, 0);

                            // Hide search view, show restaurant view
                            ViewUtils.animateShow(mSearchView, false);
                            ViewUtils.animateShow(mRestaurantView, true);
                        }

                        mGetPlaceInfoTask = null;
                    }

                    @Override
                    protected void onError(Exception ex) {
                        ex.printStackTrace();

                        mProgressBar.setVisibility(View.GONE);
                        mGetPlaceInfoTask = null;

                        Toast.makeText(MainActivity.this, R.string.error_unable_to_get_data,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onCancelled() {
                        mGetPlaceInfoTask = null;
                    }
                }
        );
    }

    public List<Place> getPlaces() {
        return mPlaces;
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // If we're searching, cancel the task
            if (mSearchNearbyPlacesTask != null) {
                mProgressBar.setVisibility(View.GONE);
                mSearchNearbyPlacesTask.cancel();
                return true;
            }

            // If we're getting details about a place, cancel the task
            if (mGetPlaceInfoTask != null) {
                mProgressBar.setVisibility(View.GONE);
                mGetPlaceInfoTask.cancel();
                return true;
            }

            // If we're in the details view, return to the main view
            if (mRestaurantView.getVisibility() == View.VISIBLE) {
                ViewUtils.animateShow(mRestaurantView, false);
                ViewUtils.animateShow(mSearchView, true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestLocationUpdates() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Get location updates using the GPS
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    LOCATION_REQUEST_DELAY, LOCATION_REQUEST_DISTANCE, this);
        } catch (SecurityException ex) {
            ex.printStackTrace();

            // Notify user that this application requires location permission
            Toast.makeText(this, R.string.permission_location_required_fail,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void removeLocationUpdates() {
        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();

            // Notify user that this application requires location permission
            Toast.makeText(this, R.string.permission_location_required_fail,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadSearches() {
        try {
            FileInputStream input = openFileInput(SEARCH_HISTORY_FILE);
            StringBuilder builder = new StringBuilder();
            while (input.available() > 0) {
                int b = input.read();
                if (b == '\n') {
                    String search = builder.toString();
                    if (!mPreviousSearches.contains(search))
                        mPreviousSearches.add(search);
                    builder.delete(0, builder.length());
                } else {
                    builder.append((char) b);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        updateSearches();
    }

    private void updateSearches() {
        mSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                mPreviousSearches);
        mSearchText.setAdapter(mSearchAdapter);
    }

    private void saveSearches() {
        try {
            FileOutputStream output = openFileOutput(SEARCH_HISTORY_FILE, Context.MODE_PRIVATE);
            for (String search : mPreviousSearches) {
                output.write(String.format("%s\n", search).getBytes());
            }
            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Save searches
        saveSearches();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Request location updates
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();

        removeLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Request location updates
                requestLocationUpdates();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isBetterLocation(location, mCurrentLocation))
            mCurrentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not needed, but required
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Not needed, but required
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Not needed, but required
    }

    // https://developer.android.com/guide/topics/location/strategies.html
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > LOCATION_TIMEOUT;
        boolean isSignificantlyOlder = timeDelta < -LOCATION_TIMEOUT;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            // If it's been more than two minutes since the current location, use the new location
            // because the user has likely moved
            return true;
        } else if (isSignificantlyOlder) {
            // If the new location is more than two minutes older, it must be worse
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }

        return false;
    }

    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }

        return provider1.equals(provider2);
    }
}