package com.example.user.superplane;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Class for handling the widget events extending BroadcastReceiver.
 * A pending intent is set on the widget's button to activate the MainActivity
 * with an 'action' to start the search.
 */
public class WidgetProvider extends BroadcastReceiver
{
    private static final String ACTION_START_SEARCH = "ActionStart";
    private static final String ACTION_STOP_SEARCH = "ActionStop";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
            ComponentName thisWidget = new ComponentName(context.getApplicationContext(), WidgetProvider.class);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

            final int N = appWidgetIds.length;

            for (int i = 0; i < N; i++)
            {
                int appWidgetId = appWidgetIds[i];

                // Create an Intent to launch MainActivity
                Intent activityIntent = new Intent(context, MainActivity.class);
                activityIntent.setAction(MainActivity.ACTION_START_SEARCH);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                views.setOnClickPendingIntent(R.id.imageButton, pendingIntent);

                //Perform an update on the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        }
    }
}
