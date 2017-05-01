package com.example.user.superplane;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Activity to display all the settings and change them.
 */
public class SettingsActivity extends AppCompatActivity
{
    private SettingsManager settingsManager;

    private Toolbar toolbar;
    private LoadImageSetting loadImageSetting;
    private SearchRadiusSetting searchRadiusSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorLight));

        settingsManager = new SettingsManager(this);

        RelativeLayout loadImageSettingLayout = (RelativeLayout)findViewById(R.id.load_image_setting);
        loadImageSetting = new LoadImageSetting(loadImageSettingLayout);

        RelativeLayout searchRadiusSettingLayout = (RelativeLayout)findViewById(R.id.search_radius_setting);
        searchRadiusSetting = new SearchRadiusSetting(searchRadiusSettingLayout);
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }

    /**
     * Class to handle the changing and displaying of the 'Load Image' setting.
     * Loads the layout from xml and sets the views accordingly.
     */
    class LoadImageSetting
    {
        private TextView titleText;
        private TextView subtext;
        private CheckBox checkBox;

        /**
         * Constructor getting the layout of the setting.
         * @param settingLayout The layout of the setting.
         */
        public LoadImageSetting(RelativeLayout settingLayout)
        {
            titleText = (TextView)settingLayout.findViewById(R.id.setting_text);
            titleText.setText("Download Images");
            subtext = (TextView)settingLayout.findViewById(R.id.setting_subtext);
            subtext.setText("Automatically download images for aircraft");

            checkBox = (CheckBox)settingLayout.findViewById(R.id.setting_checkbox);
            checkBox.setOnClickListener(clickListener);
            checkBox.setChecked(settingsManager.getDownloadImagesSetting());

        }
        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // Downloading images is very heavy on internet usage. Notify the user.
                if (checkBox.isChecked())
                {
                    new AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Internet Usage")
                            .setMessage("Enabling image download can be very heavy on internet usage. Are you sure you want to do this?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    settingsManager.setDownloadImagesSetting(checkBox.isChecked());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    checkBox.setChecked(false);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                else
                    settingsManager.setDownloadImagesSetting(false);
            }
        };
    }

    /**
     * Class to handle the changing and displaying of the 'Search Radius' setting.
     * Loads the layout from xml and sets the views accordingly.
     */
    class SearchRadiusSetting
    {
        private static final float MIN_RADIUS = 3;
        private static final float MAX_RADIUS = 50;

        private TextView titleText;
        private TextView valueText;
        private SeekBar seekBar;

        /**
         * Constructor getting the layout of the setting.
         * @param settingLayout The layout of the setting.
         */
        public SearchRadiusSetting(RelativeLayout settingLayout)
        {
            titleText = (TextView)settingLayout.findViewById(R.id.setting_name);
            titleText.setText("Search Radius");

            valueText = (TextView)settingLayout.findViewById(R.id.setting_value);
            valueText.setText(String.valueOf(settingsManager.getSearchRadiusSetting()));

            seekBar = (SeekBar)settingLayout.findViewById(R.id.seekBar);
            seekBar.setProgress(getProgressFromRadius(settingsManager.getSearchRadiusSetting()));

            seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        }

        private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                String text = String.valueOf(getRadiusFromProgress(seekBar.getProgress())) + "km";
                valueText.setText(text);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                float radius = getRadiusFromProgress(seekBar.getProgress());
                settingsManager.setSearchRadiusSetting(radius);
            }
        };

        /**
         * Converts the progress of the SeekBar to search radius in kilometers.
         * @param progress The progress to convert.
         * @return Returns the radius in kilometers.
         */
        private float getRadiusFromProgress(int progress)
        {
            float ratio = progress / 100f;
            float radius = MIN_RADIUS + ratio * (MAX_RADIUS - MIN_RADIUS);

            return radius;
        }

        /**
         * Converts the search radius in kilometers to SeekBar progress.
         * @param radius The search radius.
         * @return Returns the progress of the SeekBar.
         */
        private int getProgressFromRadius(float radius)
        {
            float delta = radius - MIN_RADIUS;
            float fullRange = MAX_RADIUS - MIN_RADIUS;
            int progress = (int)((delta / fullRange) * 100);

            return progress;
        }
    }
}
