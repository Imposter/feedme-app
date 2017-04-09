package ca.impulsedev.feedme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Toast;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;
import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;

/**
 * Startup check activity, tests if the app has network connectivity and necessary permissions
 */
public class StartupActivity extends AppCompatActivity {
    private AsyncTask<Void> mStartupTask;

    /**
     * Called when the activity is created, for initialization
     * @param savedInstanceState Saved state information
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize network and location services
        createStartupTask();
        mStartupTask.execute();
    }

    /**
     * Called when the activity is stopped by the user or operating system
     */
    @Override
    protected void onStop() {
        mStartupTask.cancel(true);
        super.onStop();
    }

    /**
     * Called when the activity is resumed by the user or operating system
     */
    @Override
    protected void onResume() {
        if (!mStartupTask.isRunning()) {
            createStartupTask();
            mStartupTask.execute();
        }
        super.onResume();
    }

    /**
     * Called when the activity is paused by the user or operating system
     */
    @Override
    protected void onPause() {
        mStartupTask.cancel(true);
        super.onPause();
    }

    /**
     * Creates a startup task to check for network connectivity, location permissions and connection
     * to API backend
     */
    private void createStartupTask() {
        mStartupTask = new AsyncTask<Void>() {
            @Override
            protected AsyncTaskResult<Void> process() {
                // Check if network is available
                if (!isWifiAvailable() && !isMobileDataAvailable()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Prompt to open settings
                            AlertDialog.Builder alert =
                                    new AlertDialog.Builder(
                                            new ContextThemeWrapper(StartupActivity.this,
                                                    R.style.AppTheme));
                            alert.setMessage(R.string.prompt_open_network_settings);
                            alert.setCancelable(false);
                            alert.setPositiveButton(R.string.dialog_yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(
                                                    Settings.ACTION_WIFI_SETTINGS);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }
                            );
                            alert.setNegativeButton(R.string.dialog_no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }
                            );
                            alert.show();
                        }
                    });
                } else if (!isGpsEnabled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Prompt to open settings
                            AlertDialog.Builder alert =
                                    new AlertDialog.Builder(
                                            new ContextThemeWrapper(StartupActivity.this,
                                                    R.style.AppTheme));
                            alert.setMessage(R.string.prompt_open_location_settings);
                            alert.setCancelable(false);
                            alert.setPositiveButton(R.string.dialog_yes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(
                                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivity(intent);
                                            dialog.dismiss();
                                        }
                                    }
                            );
                            alert.setNegativeButton(R.string.dialog_no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    }
                            );
                            alert.show();
                        }
                    });
                } else {
                    // Check if API is available
                    Api.getVersion(new ServiceCallback<Api.GetVersionInfoResult>() {
                        @Override
                        protected void onEnd(int code, Api.GetVersionInfoResult result) {
                            Toast.makeText(StartupActivity.this, String.format("Server version: %s",
                                    result.version), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        protected void onError(Exception ex) {
                            Toast.makeText(StartupActivity.this, R.string.error_api_unavailable,
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }
                return null;
            }
        };
    }

    /**
     * Checks if WiFi is available
     * @return Whether WiFi is available or not
     */
    private boolean isWifiAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * Checks if mobile data is available
     * @return Whether mobile data is available or not
     */
    private boolean isMobileDataAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * Checks if GPS is enabled
     * @return Whether GPS is enabled or not
     */
    private boolean isGpsEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
