/**
 * Feed Me! Android App
 *
 * Created by:
 * - Betty Kwong
 * - Eyaz Rehman
 * - Rameet Sekhon
 * - Rishabh Patel
 */
 
package ca.impulsedev.feedme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private Switch mSwitch;
    private Button mButton;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private CardView mAboutApp;
    private CardView mAboutUs;
    private TextView mAboutAppText;
    private TextView mAboutUsText;
    private boolean bAboutApp = false;
    private boolean bAboutUs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mSwitch = (Switch) findViewById(R.id.location_switch);
        mButton = (Button) findViewById(R.id.clear_history);
        mAboutApp = (CardView) findViewById(R.id.AboutApp);
        mAboutUs = (CardView) findViewById(R.id.AboutUs);
        mAboutAppText = (TextView) findViewById(R.id.AboutAppText);
        mAboutUsText = (TextView) findViewById(R.id.AboutUsText);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    configureSwitch();
                }
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteFile(MainActivity.SEARCH_HISTORY_FILE);
            }
        });

        mAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bAboutApp) {
                    mAboutAppText.setVisibility(TextView.VISIBLE);
                    bAboutApp = true;
                } else {
                    mAboutAppText.setVisibility(TextView.GONE);
                    bAboutApp = false;
                }
            }

            ;
        });

        mAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bAboutUs) {
                    mAboutUsText.setVisibility(TextView.VISIBLE);
                    bAboutUs = true;
                } else {
                    mAboutUsText.setVisibility(TextView.GONE);
                    bAboutUs = false;
                }
            }

            ;
        });
    }

    void configureSwitch() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET}, 10);
            }

            return;
        }

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                mLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(i);
                    }
                };
                //noinspection MissingPermission
                mLocationManager.requestLocationUpdates("gps", 5000, 0, mLocationListener);
            }
        });
    }
}