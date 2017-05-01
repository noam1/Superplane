package com.example.user.superplane;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Activity for displaying all the favorite aircraft.
 */
public class FavoritesActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private ListView listView;
    private FavoriteListAdapter favoriteListAdapter;

    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        favoritesManager = new FavoritesManager(this);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarLight);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favorites");

        this.listView = (ListView)findViewById(R.id.favListView);
        this.favoriteListAdapter = new FavoriteListAdapter();
        this.listView.setAdapter(favoriteListAdapter);
        this.listView.setOnItemClickListener(listItemClicked);

        this.setUpList();
    }

    private ListView.OnItemClickListener listItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Aircraft aircraft = (Aircraft)favoriteListAdapter.getItem(position);

            Intent intent = new Intent(FavoritesActivity.this, AircraftDetailsActivity.class);
            intent.putExtra(Aircraft.AIRCRAFT_INTENT_NAME, aircraft);

            startActivity(intent);
        }
    };

    /**
     * Reads the favorites from the database and sets them up in the ListView.
     */
    private void setUpList()
    {
        ArrayList<Aircraft> favList = favoritesManager.getFavorites();

        for (int i = 0; i < favList.size(); i++)
            favoriteListAdapter.addItem(favList.get(i));

        favoriteListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * The adapter for the favorites list.
     */
    class FavoriteListAdapter extends BaseAdapter
    {
        private ArrayList<Aircraft> items;

        public FavoriteListAdapter()
        {
            this.items = new ArrayList<>();
        }

        /**
         * Adds a new aircraft to list.
         * @param aircraft The aircraft to add to the list.
         */
        public void addItem(Aircraft aircraft)
        {
            items.add(aircraft);
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position)
        {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            Aircraft aircraft = (Aircraft)getItem(position);
            RelativeLayout itemLayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.aircraft_list_item, parent, false);

            TextView modelName = (TextView)itemLayout.findViewById(R.id.modelName);
            TextView companyName = (TextView)itemLayout.findViewById(R.id.companyName);

            modelName.setText(aircraft.getModel());
            companyName.setText(aircraft.getOperator());

            return  itemLayout;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return (items.size() == 0);
        }
    }
}
