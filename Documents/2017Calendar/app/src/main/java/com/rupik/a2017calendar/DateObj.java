package com.rupik.a2017calendar;

/**
 * Created by boom on 12/12/16.
 */

import java.io.Serializable;
import java.util.Date;

/**
 * Created by macmin5 on 18/10/16.
 */
public class DateObj implements Serializable {
    String dateStr;
    Boolean isActive = false;
    String occasionName;
    String userEvents;
    Boolean isNationalHoliday = false;
    Boolean isNotificationSet = false;
    Date date;

    public Boolean getActive() {
        return isActive;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public Boolean getNationalHoliday() {
        return isNationalHoliday;
    }

    public String getOccasionName() {
        return occasionName;
    }

    public void setNationalHoliday(Boolean nationalHoliday) {
        isNationalHoliday = nationalHoliday;
    }

    public void setOccasionName(String occasionName) {
        this.occasionName = occasionName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserEvents() {
        return userEvents;
    }

    public void setUserEvents(String userEvents) {
        this.userEvents = userEvents;
    }

    public Boolean getNotificationSet() {
        return isNotificationSet;
    }

    public void setNotificationSet(Boolean notificationSet) {
        isNotificationSet = notificationSet;
    }
}