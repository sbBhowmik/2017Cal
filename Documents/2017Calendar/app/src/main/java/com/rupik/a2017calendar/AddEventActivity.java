package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.appodeal.ads.Appodeal;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {
    DateObj dateObj;
    boolean isNotificationSet;
    String monthYearName;
    boolean isInEditMode = false;
    String editOccasionsDateKey="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

        Intent intent = this.getIntent();
        dateObj = (DateObj)intent.getSerializableExtra("DateObject");

        TextView selectedDateTV = (TextView) findViewById(R.id.selectedDateTV);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj.getDate());
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int year = cal.get(Calendar.YEAR);
        int date = cal.get(Calendar.DATE);
        monthYearName = Integer.toString(date) + "-" + monthName + "-" + Integer.toString(year);
        selectedDateTV.setText(monthYearName);

        TextView eventsTVLabel = (TextView) findViewById(R.id.eventsTVLabel);
        monthName = "Events on " + monthYearName;
        eventsTVLabel.setText(monthName);

        updateCurrentEvents();

        ImageButton addNotificationBtn = (ImageButton)findViewById(R.id.addReminderBtn);
        addNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isNotificationSet = true;
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancelReminderBtn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEventActivity.this.finish();
            }
        });

        Button saveReminderButton = (Button) findViewById(R.id.saveReminderBtn);
        saveReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText eventsEditText = (EditText)findViewById(R.id.addReminderEditText);
                String reminderString = eventsEditText.getText().toString();

                if(isInEditMode)
                {
                    SharedPreferences prefs = AddEventActivity.this.getSharedPreferences("Cal2016UserEvents", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    prefEditor.putString(editOccasionsDateKey, reminderString);
                    prefEditor.commit();
                }
                else {

                    String dateString = monthYearName;

                    SharedPreferences prefs = AddEventActivity.this.getSharedPreferences("Cal2016UserEvents", MODE_PRIVATE);
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    int counter = 0;
                    for (int i = 0; i < 1000; i++) {
                        String dateKey = dateString + "-" + Integer.toString(i) + "-userEvent";
                        String userEvent = prefs.getString(dateKey, "");
                        if (userEvent.length() > 0) {
                            counter += 1;
                        } else {
                            break;
                        }
                    }
                    String dateKey = dateString + "-" + Integer.toString(counter) + "-userEvent";
                    prefEditor.putString(dateKey, reminderString);
                    dateKey = dateString + "-" + Integer.toString(counter) + "-shouldNotify";
                    prefEditor.putBoolean(dateKey, isNotificationSet);

                    prefEditor.commit();
                }

                isInEditMode = false;

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(AddEventActivity.this);
                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                        new ComponentName(AddEventActivity.this, WidgetReceiver.class));
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.calwidgetListView);


                AddEventActivity.this.finish();
            }
        });
    }



    void updateCurrentEvents()
    {
        ListView listView = (ListView)findViewById(R.id.eventsListView);
        ListAdapter adapter = new ListAdapter(this, dateObj);
        listView.setAdapter(adapter);

        String occasionsText = dateObj.getOccasionName();
        if(occasionsText!=null) {
            occasionsText = occasionsText.replace(", ", "\n");
            TextView eventsTV = (TextView) findViewById(R.id.eventsTV);
            eventsTV.setText(occasionsText);
        }
    }

    public void editEntry(String occasionsText, String dateKey)
    {
        EditText eventsEditText = (EditText)findViewById(R.id.addReminderEditText);
        eventsEditText.setText(occasionsText);
        eventsEditText.setSelection(eventsEditText.getText().length());
        isInEditMode = true;
        editOccasionsDateKey = dateKey;
    }
}