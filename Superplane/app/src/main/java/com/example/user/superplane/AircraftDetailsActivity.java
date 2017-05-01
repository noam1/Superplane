package com.example.user.superplane;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowId;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity to show all the details of the activity
 * including an image of the aircraft (if permitted in the settings).
 */
public class AircraftDetailsActivity extends AppCompatActivity
{
    private SettingsManager settingsManager;
    private FavoritesManager favoritesManager;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Menu menu;

    private ImageView imageView;
    private Bitmap currentImage;

    private Aircraft aircraft;

    /**
     * The onCreate method of AircraftDetailsActivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aircraft_details);

        favoritesManager = new FavoritesManager(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.ToolbarDark);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ToolbarDark);


        imageView = (ImageView)findViewById(R.id.imageView);
        new DefaultImageLoadTask().execute();

        aircraft = (Aircraft) getIntent().getSerializableExtra(Aircraft.AIRCRAFT_INTENT_NAME);

        if (aircraft == null)
            throw new RuntimeException("No aircraft instance was passed to this activity!");

        settingsManager = new SettingsManager(this);

        //Download image only if permitted by settings
        if (settingsManager.getDownloadImagesSetting())
            this.initImageSearch(aircraft);

        getSupportActionBar().setTitle(aircraft.getOperator());

        this.addProperties();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.aircraft_details_menu, menu);
        this.menu = menu;

        updateFavoriteState();

        return true;
    }

    /**
     * Changes whether the displayed aircraft is favorite or not.
     * If it is favorite, it won't be anymore and visa-versa.
     */
    private void updateFavoriteState()
    {
        MenuItem favItem = menu.findItem(R.id.action_add_favorite);

        if (favoritesManager.favoriteExists(aircraft))
        {
            favItem.setIcon(R.drawable.ic_star_24dp);
        }
        else
        {
            favItem.setIcon(R.drawable.ic_star_outline_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_favorite:
                if (!favoritesManager.favoriteExists(aircraft))
                    favoritesManager.addFavorite(aircraft);
                else
                    favoritesManager.removeFavorite(aircraft);

                updateFavoriteState();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adds all the aircraft's properties to the layout.
     */
    private void addProperties()
    {
        LinearLayout contentLayout = (LinearLayout)findViewById(R.id.bottomContentLayout);
        LayoutInflater inflater = LayoutInflater.from(this);

        AircraftPropertiesManager propertiesManager = new AircraftPropertiesManager(aircraft);
        int count = propertiesManager.getPropertyCount();

        for (int i = 0; i < count; i++)
        {
            LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.aircraft_property_list_item, contentLayout, false);

            TextView propertyText = (TextView)layout.findViewById(R.id.propertyTextView);
            propertyText.setText(propertiesManager.getPropertyName(i));

            TextView valueText = (TextView)layout.findViewById(R.id.valueTextView);
            valueText.setText(propertiesManager.getPropertyValue(i));

            contentLayout.addView(layout);
        }
    }

    /**
     * Starts the image serach in the background as to not disturb the UI thread.
     * @param aircraft The aircraft to search an image for.
     */
    private void initImageSearch(Aircraft aircraft)
    {
        String query = aircraft.getModel() + " " + aircraft.getOperator();

        GoogleImageProvider imageProvider = new GoogleImageProvider();
        imageProvider.setImageFoundListener(imageFoundListener);

        imageProvider.findRandomImage(query);
    }

    private ImageFoundListener imageFoundListener = new ImageFoundListener() {
        @Override
        public void imageFound(Bitmap image) {
            imageView.setImageBitmap(image);
            currentImage = image;
        }

        @Override
        public void internetError() {

        }
    };

    /**
     * Used for cleaning the downloaded image memory
     */
    private void flush()
    {
        currentImage = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        flush();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        flush();
        super.onBackPressed();
    }

    /**
     * An AsyncTask to load the default Superplane logo in the background
     * as to not disturb the UI thread.
     */
    class DefaultImageLoadTask extends AsyncTask<Void, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(Void... params) {
            return BitmapFactory.decodeResource(getResources(), R.drawable.airplane_logo);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (imageView.getDrawable() == null)
                imageView.setImageBitmap(bitmap);
        }
    }
}
