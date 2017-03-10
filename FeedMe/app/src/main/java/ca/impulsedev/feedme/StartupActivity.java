package ca.impulsedev.feedme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ca.impulsedev.feedme.api.AsyncTask;
import ca.impulsedev.feedme.api.AsyncTaskResult;

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
        mStartupTask.execute();
    }

    @Override
    protected void onStop() {
        mStartupTask.cancel(true);
        super.onStop();
    }
}
