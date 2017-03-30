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

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;

public class StartupActivity extends AppCompatActivity {
    private AsyncTask<Void> mStartupTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // Initialize network and location services
        createStartupTask();
        mStartupTask.execute();
    }

    @Override
    protected void onStop() {
        mStartupTask.cancel(true);
        super.onStop();
    }

    @Override
    protected void onResume() {
        if (!mStartupTask.isRunning()) {
            createStartupTask();
            mStartupTask.execute();
        }
        super.onPause();
    }

    @Override
    protected void onPause() {
        mStartupTask.cancel(true);
        super.onPause();
    }

    private void createStartupTask() {
        mStartupTask = new AsyncTask<Void>() {
            @Override
            protected AsyncTaskResult<Void> process() {
                try {
                    // Check if network is available
                    if (!isWifiAvailable() && !isMobileDataAvailable()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Prompt to open settings
                                AlertDialog.Builder builder =
                                        new AlertDialog.Builder(
                                                new ContextThemeWrapper(StartupActivity.this,
                                                        R.style.AppTheme));
                                builder.setMessage(R.string.prompt_open_network_settings);
                                builder.setPositiveButton(R.string.dialog_yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(
                                                        Settings.ACTION_WIFI_SETTINGS);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                builder.setNegativeButton(R.string.dialog_no,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                builder.show();
                            }
                        });
                    } else if (!isGpsEnabled() && !isMobileLocationEnabled()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Prompt to open settings
                                AlertDialog.Builder alert =
                                        new AlertDialog.Builder(
                                                new ContextThemeWrapper(StartupActivity.this,
                                                        R.style.AppTheme));
                                alert.setMessage(R.string.prompt_open_location_settings);
                                alert.setPositiveButton(R.string.dialog_yes,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(
                                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        });
                                alert.setNegativeButton(R.string.dialog_no,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                alert.show();
                            }
                        });
                    } else {
                        // TODO: Check if API is available

                        // Wait
                        Thread.sleep(1000);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        };
    }

    private boolean isWifiAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private boolean isMobileDataAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

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

    private boolean isMobileLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
