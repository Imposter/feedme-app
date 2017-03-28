package ca.impulsedev.feedme;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.impulsedev.feedme.api.service.Api;
import ca.impulsedev.feedme.api.service.ServiceCallback;
import ca.impulsedev.feedme.api.service.ServiceTask;
import ca.impulsedev.feedme.api.service.models.Place;

public class SubPageActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_sub_page);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        LinearLayout callButton = (LinearLayout) findViewById(R.id.call_layout);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phone = new Intent(Intent.ACTION_DIAL);
                //to be replaced with api-provided phone number
                phone.setData(Uri.parse("tel:9059059059"));
                startActivity(phone);
            }
        });

        LinearLayout directionButton = (LinearLayout) findViewById(R.id.direction_layout);
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "geo:43.8765786,-79.0942335,17z";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

}