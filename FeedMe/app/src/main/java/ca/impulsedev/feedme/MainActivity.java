package ca.impulsedev.feedme;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;
import ca.impulsedev.feedme.api.service.ServiceTask;
import ca.impulsedev.feedme.api.service.models.Place;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final int PERMISSION_REQUEST_LOCATION = 100;

    private static final int LOCATION_REQUEST_DELAY = 2500;
    private static final int LOCATION_REQUEST_DISTANCE = 25;
    private static final int LOCATION_TIMEOUT = 1000 * 60 * 2;

    private static final int LOCATION_SEARCH_RADIUS = 50000; // Meters

    private List<Place> mPlaces;
    private RestaurantSearchAdapter mAdapter;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mSearchProgressBarView;
    private EditText mSearchEditTextView;
    private ServiceTask mSearchNearbyPlacesTask;
    private String mLastToken;

    private LocationManager mLocationManager;
    private List<String> mCurrentProviders;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPlaces = new ArrayList<>();
        mAdapter = new RestaurantSearchAdapter(mPlaces);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

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

        mSearchProgressBarView = (ProgressBar) findViewById(R.id.search_progress);

        mSearchEditTextView = (EditText) findViewById(R.id.search_text);
        mSearchEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                Location lastGpsLocation = mLocationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                Location lastNetworkLocation = mLocationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
                if (lastNetworkLocation == null) {
                    location = lastGpsLocation;
                } else if (lastGpsLocation == null) {
                    location = lastNetworkLocation;
                } else {
                    location = lastGpsLocation.getTime()
                            > lastNetworkLocation.getTime()
                            ? lastGpsLocation : lastNetworkLocation;
                }
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

        // Set current location in adapter
        mAdapter.setLocation(location);

        mSearchNearbyPlacesTask = Api.getNearbyFoodPlaces(mSearchEditTextView.getText().toString(),
                location.getLatitude(), location.getLongitude(), mLastToken,
                new ServiceCallback<Api.SearchNearbyFoodPlacesResult>() {
                    @Override
                    protected void onBegin() {
                        mSearchProgressBarView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected void onEnd(int code,
                                         Api.SearchNearbyFoodPlacesResult result) {
                        mSearchProgressBarView.setVisibility(View.GONE);
                        if (result != null) {
                            mPlaces.addAll(Arrays.asList(result.nearby));
                            mAdapter.notifyDataSetChanged();

                            mLastToken = result.next;
                        }

                        mSearchNearbyPlacesTask = null;
                    }

                    @Override
                    protected void onError(Exception ex) {
                        ex.printStackTrace();

                        mSearchProgressBarView.setVisibility(View.GONE);
                        mSearchNearbyPlacesTask = null;

                        Toast.makeText(MainActivity.this, R.string.error_unable_to_get_data,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected void onCancelled() {
                        mSearchNearbyPlacesTask = null;
                    }
                }
        );
        mLastToken = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // If we're searching, cancel the task
            if (mSearchNearbyPlacesTask != null) {
                mSearchProgressBarView.setVisibility(View.GONE);
                mSearchNearbyPlacesTask.cancel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            mLocationManager.removeUpdates(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();

            // Notify user that this application requires location permission
            Toast.makeText(this, R.string.permission_location_required_fail,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void requestLocationUpdates() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mCurrentProviders = new ArrayList<>();

        // Get location updates from best available provider
        List<String> providers = mLocationManager.getProviders(true);
        for (String provider : providers) {
            try {
                mLocationManager.requestLocationUpdates(provider, LOCATION_REQUEST_DELAY,
                        LOCATION_REQUEST_DISTANCE, this);
                mCurrentProviders.add(provider);
            } catch (SecurityException ex) {
                ex.printStackTrace();

                // Notify user that this application requires location permission
                Toast.makeText(this, R.string.permission_location_required_fail,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Request location updates
        requestLocationUpdates();
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
        if (isBetterLocation(location, mCurrentLocation)) {
            mCurrentLocation = location;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Not needed, but required
    }

    @Override
    public void onProviderEnabled(String provider) {
        try {
            if (!mCurrentProviders.contains(provider)) {
                mLocationManager.requestLocationUpdates(provider, LOCATION_REQUEST_DELAY,
                        LOCATION_REQUEST_DISTANCE, this);
                mCurrentProviders.add(provider);
            }
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (mCurrentProviders.contains(provider)) {
            mCurrentProviders.remove(provider);
        }
    }

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

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
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