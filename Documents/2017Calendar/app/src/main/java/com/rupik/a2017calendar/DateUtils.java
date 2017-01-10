package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by macmin5 on 26/10/16.
 */
public class DateUtils {
    private static DateUtils ourInstance = new DateUtils();

    public static DateUtils getInstance() {
        return ourInstance;
    }

    private DateUtils() {
    }

    ArrayList prepareCalDateSet(int currentDisplayedYear, int currentDisplayedMonth, Context context, boolean isDataForWidget)
    {
        Calendar cal = Calendar.getInstance();
        cal.set(currentDisplayedYear, currentDisplayedMonth, 1);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int totalNoOfDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int previousYear = currentDisplayedYear;
        int previousMonth;
        if(currentDisplayedMonth == 0)
        {
            previousYear = currentDisplayedYear - 1;
            previousMonth = 11;
        }
        else {
            previousMonth = currentDisplayedMonth - 1;
        }
        cal.set(previousYear, previousMonth, 1);
        int totalNoOfDaysInPreviousMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        ArrayList dateArrayList = new ArrayList<>();

        if(!isDataForWidget) {
            for (int i = dayOfWeek - 1; i > 0; i--) {
                //previous month

                cal.set(previousYear, previousMonth, totalNoOfDaysInPreviousMonth - i + 1);
                Date date = cal.getTime();

                String dateStr = String.valueOf(totalNoOfDaysInPreviousMonth - i + 1);
                DateObj dateObj = new DateObj();
                dateObj.dateStr = dateStr;
                dateObj.isActive = false;
                dateObj.setDate(date);
                dateArrayList.add(dateObj);
            }
        }
        for(int i=1; i<=totalNoOfDaysInMonth; i++)
        {
            //current month
            cal.set(currentDisplayedYear, currentDisplayedMonth, i);
            Date date = cal.getTime();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);


            if(isDataForWidget)
            {
                Date todaysDate = new Date();
                Calendar todaysCal = Calendar.getInstance();
                todaysCal.setTime(todaysDate);
                todaysCal.set(todaysCal.get(Calendar.YEAR), todaysCal.get(Calendar.MONTH), todaysCal.get(Calendar.DATE));
                todaysCal.set(Calendar.HOUR_OF_DAY, 0);
                todaysCal.set(Calendar.MINUTE, 0);
                todaysCal.set(Calendar.SECOND, 0);
                todaysCal.set(Calendar.MILLISECOND, 0);

                todaysDate = todaysCal.getTime();
                if(date.compareTo(todaysDate) < 0)
                {
                    continue;
                }
            }

            String dateStr = String.valueOf(i);
            DateObj dateObj = new DateObj();
            dateObj.dateStr = dateStr;
            dateObj.isActive = true;
            dateObj.setDate(date);
            dateArrayList.add(dateObj);
        }

        if(!isDataForWidget) {
            int totalNoOfDays = dateArrayList.size();

            if (totalNoOfDays % 7 != 0) {
                //next month
                int noOfRows = totalNoOfDays / 7;
                noOfRows += 1;
                int remainingNoOfCells = noOfRows * 7 - totalNoOfDays;
                for (int i = 1; i <= remainingNoOfCells; i++) {
                    int nextMonth;
                    int nextYear = currentDisplayedYear;
                    if (currentDisplayedMonth == 11) {
                        nextMonth = 0;
                        nextYear = currentDisplayedYear + 1;
                    } else {
                        nextMonth = currentDisplayedMonth + 1;
                    }
                    cal.set(nextYear, nextMonth, i);
                    Date date = cal.getTime();

                    String dateStr = String.valueOf(i);
                    DateObj dateObj = new DateObj();
                    dateObj.dateStr = dateStr;
                    dateObj.isActive = false;
                    dateObj.setDate(date);
                    dateArrayList.add(dateObj);
                }
            }
        }

        try {
            InputStream is = context.getResources().openRawResource(R.raw.occasions_json);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            String jsontext = new String(buffer);

            JSONObject jsonObj = new JSONObject(jsontext);
            JSONObject yearJsonObj = jsonObj.getJSONObject(Integer.toString(currentDisplayedYear));
            JSONArray monthJsonObj = yearJsonObj.getJSONArray(Integer.toString(currentDisplayedMonth));
            for(int i=0; i<monthJsonObj.length(); i++)
            {
                JSONObject dateJson = monthJsonObj.getJSONObject(i);
                String dateString = dateJson.optString("date");

                for(int j=0; j<dateArrayList.size(); j++)
                {
                    DateObj dateObj = (DateObj) dateArrayList.get(j);
                    if(!dateObj.isActive) continue;
                    int dateObjDate = Integer.parseInt(dateObj.getDateStr());
                    int date = Integer.parseInt(dateString);
                    if(dateObjDate == date)
                    {
                        String occasionName = dateObj.getOccasionName();
                        if(occasionName!=null && occasionName.length()>0)
                        {
                            occasionName = occasionName + ", " + dateJson.optString("occasion");
                        }
                        else {
                            occasionName = dateJson.optString("occasion");
                        }
                        dateObj.setOccasionName(occasionName);
                        String nationalHolidayString = dateJson.optString("nationalHoliday");
                        if(nationalHolidayString != null && nationalHolidayString.length()>0) {
                            int nationalHolidayCode = Integer.parseInt(nationalHolidayString);
                            if (nationalHolidayCode == 1) {
                                dateObj.isNationalHoliday = true;
                            } else {
                                dateObj.isNationalHoliday = false;
                            }
                        }
                        else {
                            dateObj.isNationalHoliday = false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.d("Exception", e.getLocalizedMessage());
        }

        for(int j=0;j<dateArrayList.size(); j++) {
            DateObj dateObj = (DateObj) dateArrayList.get(j);

            cal = Calendar.getInstance();
            cal.setTime(dateObj.getDate());
            String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
            int year = cal.get(Calendar.YEAR);
            int date = cal.get(Calendar.DATE);
            String dateString = Integer.toString(date) + "-" + monthName + "-" + Integer.toString(year);

            SharedPreferences prefs = context.getSharedPreferences("Cal2016UserEvents", context.MODE_PRIVATE);

            for (int i = 0; i < 1000; i++) {
                String dateKey = dateString + "-" + Integer.toString(i) + "-userEvent";
                String userEvent = prefs.getString(dateKey, "");
                if (userEvent.length() > 0) {
                    String userEvents = dateObj.getUserEvents();
                    if(userEvents!=null && userEvents.length()>0)
                    {
                        userEvents = userEvents + ", " + userEvent;
                    }
                    else {
                        userEvents = userEvent;
                    }

                    dateObj.setUserEvents(userEvents);
//                    dateObj.setOccasionName(occasionName);

                    dateKey = dateString + "-" + Integer.toString(i) + "-shouldNotify";
                    if(dateObj.isNotificationSet == false) {
                        boolean isnotificationEnabled = prefs.getBoolean(dateKey, false);
                        dateObj.setNotificationSet(isnotificationEnabled);
                    }

                } else {
                    break;
                }
            }
        }

        return dateArrayList;
    }


}
