package ca.impulsedev.feedme;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;
import ca.impulsedev.feedme.api.service.ServiceTask;
import ca.impulsedev.feedme.api.service.models.Place;

public class SearchActivity extends AppCompatActivity {
    private List<Place> mPlaces;
    private RestaurantSearchAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mSearchProgressBarView;
    private EditText mSearchEditTextView;
    private ServiceTask mSearchNearbyPlacesTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //getSupportActionBar().setTitle;



        mPlaces = new ArrayList<>();
        mAdapter = new RestaurantSearchAdapter(mPlaces);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mSearchProgressBarView = (ProgressBar) findViewById(R.id.search_progress);

        mSearchEditTextView = (EditText) findViewById(R.id.search_text);
        mSearchEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                InputMethodManager inputMethodManager
                        = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                mSearchNearbyPlacesTask = Api.getNearbyFoodPlaces(view.getText().toString(),
                        43.713757, -79.2800344, 50000,
                        new ServiceCallback<Api.SearchNearbyFoodPlacesResult>() {
                                @Override
                                protected void onBegin() {
                                    mSearchProgressBarView.setVisibility(View.VISIBLE);
                                }

                                // TODO: Reduce page size and query more often whenever you go down
                                @Override
                                protected void onEnd(int code,
                                                     Api.SearchNearbyFoodPlacesResult result) {
                                    mSearchProgressBarView.setVisibility(View.GONE);
                                    if (result != null) {
                                        mPlaces.clear();
                                        mAdapter.notifyDataSetChanged();

                                        mPlaces.addAll(Arrays.asList(result.nearby));
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                protected void onError(Exception ex) {
                                    mSearchProgressBarView.setVisibility(View.GONE);
                                    ex.printStackTrace();
                                }
                            }
                    );
                    return true;
            }
        });
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
}