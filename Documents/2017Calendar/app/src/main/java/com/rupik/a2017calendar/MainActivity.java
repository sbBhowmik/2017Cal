package com.rupik.a2017calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.revmob.RevMob;
import com.revmob.RevMobAdsListener;
import com.revmob.ads.banner.RevMobBanner;
import com.revmob.ads.interstitial.RevMobFullscreen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int currentDisplayedMonth;
    int currentDisplayedYear;
    Date todaysDate;

    ArrayList<DateObj> dateArrayList;

    RevMob revmob;
    RevMobBanner banner;
    private boolean fullscreenIsLoaded;
    private RevMobFullscreen fullscreen;


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
        //refresh current displayed calendar
        dateArrayList = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, this, false);
        displayCalendar();

        Appodeal.onResume(this, Appodeal.BANNER);
        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        revmob = RevMob.startWithListener(this, new RevMobAdsListener() {
//            @Override
//            public void onRevMobSessionStarted() {
//                loadBanner(); // Cache the banner once the session is started
//                loadFullscreen(); // pre-cache it without showing it
//            }
//        },"5874a4b7c2164c4947d37e08");
//
//        loadBanner();
//        loadFullscreen(); // pre-cache it without showing it

        String appKey = "78dd18cbdd1a74e69505f95d0bc114e25e82f831f17e4bb9";
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER);

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

        setTodaysDate();

        dateArrayList = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, this, false);
        displayCalendar();

        ImageButton prevButton = (ImageButton) findViewById(R.id.prevMonthBtn);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPreviousMonthCalendar();
//                reloadBanner();
                Appodeal.show(MainActivity.this,Appodeal.BANNER_BOTTOM);
            }
        });

        ImageButton nextButton = (ImageButton) findViewById(R.id.nextMonthBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextMonthCalendar();
                Appodeal.show(MainActivity.this,Appodeal.BANNER_BOTTOM);
//                reloadBanner();
            }
        });

        GridView calGridView = (GridView)findViewById(R.id.calGridView);

        calGridView.setOnTouchListener(new SwipeGestureListener(MainActivity.this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                displayPreviousMonthCalendar();
            }
            public void onSwipeLeft() {
                displayNextMonthCalendar();
            }
            public void onSwipeBottom() {
            }
        });

        try {
            calGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    DateObj dateObj = dateArrayList.get(position);
                    Intent i = new Intent(MainActivity.this, AddEventActivity.class);
                    i.putExtra("DateObject", dateObj);
                    startActivity(i);
                }
            });
        }
        catch (NullPointerException e)
        {

        }

        Button todaysDateBtn = (Button) findViewById(R.id.dateSummaryButton);
        todaysDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTodaysDate();
                dateArrayList = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, MainActivity.this, false);
                displayCalendar();
            }
        });
    }

    static  int adCount = 0;

    void displayPreviousMonthCalendar()
    {
        if(adCount == 7)
        {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
//            showFullscreen();
            adCount = 0;
        }
        else {
            adCount+=1;
        }

        if(currentDisplayedMonth == 0)
        {
            currentDisplayedYear = currentDisplayedYear-1;
            currentDisplayedMonth = 11;
        }
        else {
            currentDisplayedMonth = currentDisplayedMonth - 1;
        }

        dateArrayList = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, this, false);
        displayCalendar();
    }

    void displayNextMonthCalendar()
    {
        if(adCount == 7)
        {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
//            showFullscreen();
            adCount = 0;
        }
        else {
            adCount+=1;
        }

        if(currentDisplayedMonth == 11)
        {
            currentDisplayedYear = currentDisplayedYear+1;
            currentDisplayedMonth = 0;
        }
        else {
            currentDisplayedMonth += 1;
        }

        dateArrayList = DateUtils.getInstance().prepareCalDateSet(currentDisplayedYear, currentDisplayedMonth, this, false);
        displayCalendar();
    }


    void displayCalendar()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(currentDisplayedYear, currentDisplayedMonth, 1);
        Date displayedDate = cal.getTime();

        GridView calGridView = (GridView)findViewById(R.id.calGridView);
        GridAdapter adapter = new GridAdapter(this, R.layout.grid_layout,dateArrayList, todaysDate, displayedDate);
        calGridView.setAdapter(adapter);

        updateCurrentMonthAndYear();
    }

    void updateCurrentMonthAndYear()
    {
        TextView currentMonthYearTV = (TextView) findViewById(R.id.currentMonthYearTV);
        Calendar cal = Calendar.getInstance();
        cal.set(currentDisplayedYear, currentDisplayedMonth, 1);
        String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String monthYearName = monthName + " " + currentDisplayedYear;
        currentMonthYearTV.setText(monthYearName);
    }

    void setTodaysDate()
    {
        todaysDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(todaysDate);

        currentDisplayedMonth = calendar.get(Calendar.MONTH);
        currentDisplayedYear = calendar.get(Calendar.YEAR);

        int date = calendar.get(Calendar.DATE);
//        Button todaysDateButton = (Button)findViewById(R.id.dateSummaryButton);
//        todaysDateButton.setText(Integer.toString(date));
    }

    //===Ad Methods

    public void loadBanner(){
//        banner = revmob.preLoadBanner(this, new RevMobAdsListener(){
//            @Override
//            public void onRevMobAdReceived() {
//                showBanner();
//                Log.i("RevMob","Banner Ready to be Displayed"); //At this point, the banner is ready to be displayed.
//            }
//            @Override
//            public void onRevMobAdNotReceived(String message) {
//                Log.i("RevMob","Banner Not Failed to Load");
//            }
//            @Override
//            public void onRevMobAdDisplayed() {
//                Log.i("RevMob","Banner Displayed");
//            }
//        });
    }

    public void showBanner(){
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Appodeal.hide(MainActivity.this, Appodeal.BANNER_BOTTOM);
//                ViewGroup view = (ViewGroup) findViewById(R.id.bannerLayout);
//                if(banner.getParent()!=null)
//                    ((ViewGroup)banner.getParent()).removeView(banner);
//                view.addView(banner);
//                banner.show(); //This method must be called in order to display the ad.
//            }
//        });
    }

    public void releaseBanner(){
//        banner.release();
    }

    public void reloadBanner()
    {
//        releaseBanner();
//        loadBanner();
    }

    public void loadFullscreen() {
        //load it with RevMob listeners to control the events fired
        fullscreen = revmob.createFullscreen(this,  new RevMobAdsListener() {
            @Override
            public void onRevMobAdReceived() {
                Log.i("RevMob", "Fullscreen loaded.");
                fullscreenIsLoaded = true;
//                showFullscreen();
            }
            @Override
            public void onRevMobAdNotReceived(String message) {
                Log.i("RevMob", "Fullscreen not received.");
            }
            @Override
            public void onRevMobAdDismissed() {
                Log.i("RevMob", "Fullscreen dismissed.");
            }
            @Override
            public void onRevMobAdClicked() {
                Log.i("RevMob", "Fullscreen clicked.");
            }
            @Override
            public void onRevMobAdDisplayed() {
                Log.i("RevMob", "Fullscreen displayed.");
            }
        });
    }
    public void showFullscreen() {
        if(fullscreenIsLoaded) {
            fullscreen.show(); // call it wherever you want to show the fullscreen ad
        } else {
            Log.i("RevMob", "Ad not loaded yet.");
            //
            // Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
    }

}
