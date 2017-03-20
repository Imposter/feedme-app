package ca.impulsedev.feedme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;
import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;
import ca.impulsedev.feedme.api.service.models.Place;

public class StartupActivity extends AppCompatActivity {
    private AsyncTask<Void> mStartupTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // TODO: initialize network and whatnot
        mStartupTask = new AsyncTask<Void>() {
            @Override
            protected AsyncTaskResult<Void> process() {
                try {
                    // Dummy wait
                    Thread.sleep(2500);

                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        //mStartupTask.execute();

        Api.getNearbyFoodPlaces("sushi", 43.8046673, -79.2493688, 10000, 50,
                new ServiceCallback<Api.SearchNearbyFoodPlacesResult>() {
            @Override
            protected void onBegin() {
                Log.i("Api", "Getting nearby food places");
            }

            @Override
            protected void onEnd(int code, Api.SearchNearbyFoodPlacesResult result) {
                Log.i("Api", "Got " + result.nearby.length + " results");
                for (Place place : result.nearby) {
                    Log.i("Api", String.format("%s: %f located at %s", place.name, place.rating,
                            place.vicinity));
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }

            @Override
            protected void onError(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    protected void onStop() {
        mStartupTask.cancel(true);
        super.onStop();
    }
}
