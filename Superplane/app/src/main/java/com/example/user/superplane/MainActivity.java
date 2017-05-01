package com.example.user.superplane;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Activity for displaying the activation button and a menu to navigate to other Activities.
 */
public class MainActivity extends AppCompatActivity
{
    public static final String ACTION_START_SEARCH = "ActionStart";

    private FlightDataProvider flightDataProvider;

    private CircleButtonView circleButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");

        circleButtonView = (CircleButtonView)findViewById(R.id.mainButton);
        circleButtonView.setStatusListener(circleButtonStatusListener);

        flightDataProvider = new FlightDataProvider(this);
        flightDataProvider.setOnAircraftFoundListener(aircraftFoundListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start the search if the intent action is to start
        if (getIntent().getAction() != null && getIntent().getAction().equals(ACTION_START_SEARCH))
            circleButtonView.callOnClick();
        else
            circleButtonView.deactivate();
    }

    private CircleButtonStatusListener circleButtonStatusListener = new CircleButtonStatusListener() {
        @Override
        public void buttonActivated() {
            flightDataProvider.getClosestAircraft();
        }

        @Override
        public void buttonDeactivated() {
            flightDataProvider.abortGetClosestAircraft();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    private AircraftFoundListener aircraftFoundListener = new AircraftFoundListener()
    {
        @Override
        public void aircraftFound(Aircraft aircraft)
        {
            //Handle cases of empty list (No aircraft found)
            if (aircraft != null)
            {
                Intent detailsIntent = new Intent(MainActivity.this, AircraftDetailsActivity.class);
                detailsIntent.putExtra(Aircraft.AIRCRAFT_INTENT_NAME, aircraft);
                startActivity(detailsIntent);
            }
            else
            {
                View rootView = findViewById(android.R.id.content);

                final Snackbar snackbar = Snackbar.make(rootView, "No Aircraft Found.", Snackbar.LENGTH_LONG);
                snackbar.setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                snackbar.show();

                circleButtonView.deactivate();
            }
        }

        @Override
        public void aircraftFound(ArrayList<Aircraft> aircraft) {
            throw new RuntimeException("Unimplemented Function Exception");
        }

        @Override
        public void internetError()
        {
            View rootView = findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(rootView, "Internet Connection Error.", Snackbar.LENGTH_LONG);
            snackbar.setAction("CLOSE", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            snackbar.show();

            circleButtonView.deactivate();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_favorites:
                Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
                startActivity(favoritesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
