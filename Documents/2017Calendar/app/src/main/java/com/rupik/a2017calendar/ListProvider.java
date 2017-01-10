package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by macmin5 on 26/10/16.
 */
public class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    ArrayList<DateObj> listItemList;
    private Context context = null;
    private int appWidgetId;

    int currentDisplayedMonth;
    int currentDisplayedYear;
    Date todaysDate;

    public ListProvider(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        setTodaysDate();
        ArrayList currentMonthsData = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, context, true);
        if(currentDisplayedMonth==11)
        {
            currentDisplayedMonth=0;
            currentDisplayedYear += 1;
        }
        else {
            currentDisplayedMonth += 1;
        }
        ArrayList nextMonthsData = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, context, true);
        listItemList = new ArrayList<>();
        listItemList.addAll(currentMonthsData);
        listItemList.addAll(nextMonthsData);
    }

    void setTodaysDate()
    {
        todaysDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysDate);

        currentDisplayedMonth = calendar.get(Calendar.MONTH);
        currentDisplayedYear = calendar.get(Calendar.YEAR);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        populateListItem();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.cal_widget_listview_cell);
        DateObj dateObj = (DateObj) listItemList.get(position);
        String occasionsStr = "";
        if(dateObj.getOccasionName() != null)
        {
            occasionsStr = dateObj.getOccasionName();
        }
        if(dateObj.getUserEvents() != null)
        {
            if(occasionsStr.length() > 1)
            {
                occasionsStr = occasionsStr + ", ";
            }
            occasionsStr = occasionsStr + dateObj.getUserEvents();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateObj.getDate());
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);

        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);

        remoteView.setTextViewText(R.id.CalwidgetCellEventsTV, occasionsStr);
        remoteView.setTextViewText(R.id.CalwidgetCellDateTV, dateObj.getDateStr() + " " + monthName + "\n" + dayName);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(WidgetReceiver.EXTRA_LIST_VIEW_ROW_NUMBER, position);
        remoteView.setOnClickFillInIntent(R.id.CalwidgetCellEventsTV, fillInIntent);
        //
        Intent fillInIntent1 = new Intent();
        fillInIntent.putExtra(WidgetReceiver.EXTRA_LIST_VIEW_ROW_NUMBER, position);
        remoteView.setOnClickFillInIntent(R.id.CalwidgetCellDateTV, fillInIntent1);
        //

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
