package com.rupik.a2017calendar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;

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

    @Override
    public void onResume()
    {
        super.onResume();

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
            }
        });

        ImageButton nextButton = (ImageButton) findViewById(R.id.nextMonthBtn);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextMonthCalendar();
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
        Button todaysDateButton = (Button)findViewById(R.id.dateSummaryButton);
        todaysDateButton.setText(Integer.toString(date));
    }
}
