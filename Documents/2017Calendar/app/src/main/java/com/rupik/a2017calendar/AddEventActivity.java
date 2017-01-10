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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.appodeal.ads.Appodeal;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.RevMobUserGender;
import com.revmob.ads.banner.RevMobBanner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

public class AddEventActivity extends AppCompatActivity {
    DateObj dateObj;
    boolean isNotificationSet;
    String monthYearName;
    boolean isInEditMode = false;
    String editOccasionsDateKey="";

    RevMob revmob;
    RevMobBanner banner;

    @Override
    public void  onPause()
    {
        super.onPause();

        releaseBanner();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        loadBanner();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
            @Override
            public void onRevMobSessionStarted() {
                loadBanner(); // Cache the banner once the session is started
            }
        },"5874a4b7c2164c4947d37e08");

        SharedPreferences sp = getSharedPreferences("your_prefs",MODE_PRIVATE);
        if(sp.getBoolean("isProfileUpdated", false))
        {
            revmob.setUserEmail(sp.getString("email","testEmail@test.com"));
            if(sp.getBoolean("isFemale",false)) {
                revmob.setUserGender(RevMobUserGender.FEMALE);
            }
            else {
                revmob.setUserGender(RevMobUserGender.MALE);
            }
            revmob.setUserPage(sp.getString("pageLink","facebook.com/revmob"));
            String ageStr = sp.getString("ageStr","18");
            int age = Integer.parseInt(ageStr);
            revmob.setUserAgeRangeMax(age-3);
            revmob.setUserAgeRangeMin(age+3);

            String bDayText = sp.getString("bDayText","28/03/1988");
            String components[] = bDayText.split("/");
            revmob.setUserBirthday(new GregorianCalendar(Integer.parseInt(components[2]), Integer.parseInt(components[1]), Integer.parseInt(components[0])));

            String interestsStr = sp.getString("interestsString","Literature, Books");

            ArrayList<String> interests = new ArrayList<String>();
            interests.add(interestsStr);
            revmob.setUserInterests(interests);
        }
        else {
            Intent i = new Intent(this, UserProfile.class);
            startActivity(i);
        }

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

    //===Ad Methods

    public void loadBanner(){
        banner = revmob.preLoadBanner(this, new RevMobAdsListener(){
            @Override
            public void onRevMobAdReceived() {
                showBanner();
                Log.i("RevMob","Banner Ready to be Displayed"); //At this point, the banner is ready to be displayed.
            }
            @Override
            public void onRevMobAdNotReceived(String message) {
                Log.i("RevMob","Banner Not Failed to Load");
            }
            @Override
            public void onRevMobAdDisplayed() {
                Log.i("RevMob","Banner Displayed");
            }
        });
    }

    public void showBanner(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Appodeal.hide(AddEventActivity.this, Appodeal.BANNER_BOTTOM);
                ViewGroup view = (ViewGroup) findViewById(R.id.bannerLayout);
                if(banner.getParent()!=null)
                    ((ViewGroup)banner.getParent()).removeView(banner);
                view.addView(banner);
                banner.show(); //This method must be called in order to display the ad.
            }
        });
    }

    public void releaseBanner(){
        banner.release();
    }
}