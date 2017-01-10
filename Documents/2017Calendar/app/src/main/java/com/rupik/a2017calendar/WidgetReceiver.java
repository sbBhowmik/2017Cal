package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by macmin5 on 25/10/16.
 */
public class WidgetReceiver extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM";
    public static final String TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION";

    public static final String EXTRA_LIST_VIEW_ROW_NUMBER = "Widget_Clicked";
    public static String YOUR_AWESOME_ACTION = "YourAwesomeAction";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(YOUR_AWESOME_ACTION)) {
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
//            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = updateWidgetListView(context,
                    appWidgetIds[i]);

            //
            Intent intent = new Intent(context, WidgetReceiver.class);
            intent.setAction(YOUR_AWESOME_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widgetTodaysDateTV, pendingIntent);
            //
            //
            Intent startActivityIntent = new Intent(context, MainActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.calwidgetListView, startActivityPendingIntent);
            //

            appWidgetManager.updateAppWidget(appWidgetIds[i],
                    remoteViews);



        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private RemoteViews updateWidgetListView(Context context,
                                             int appWidgetId) {

        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),R.layout.widget_layout);

        Date todaysDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todaysDate);
        String dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        String dateStr = cal.get(Calendar.DATE) + "-" + monthName + "-" + cal.get(Calendar.YEAR) + " " + dayName;
        remoteViews.setTextViewText(R.id.widgetTodaysDateTV,dateStr);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(appWidgetId, R.id.calwidgetListView,
                svcIntent);
        //setting an empty view in case of no data
//        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
        return remoteViews;
    }
}
