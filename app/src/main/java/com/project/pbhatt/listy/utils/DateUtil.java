package com.project.pbhatt.listy.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Calendar;

/**
 * Created by pbhatt on 12/5/17.
 */

public class DateUtil {
    static final SimpleDateFormat LISTY_DATE_FORMATTER = new SimpleDateFormat("EEE MMM d, yyyy");
    private static final String TAG = DateUtil.class.toString();

    public static String getFormattedDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return LISTY_DATE_FORMATTER.format(calendar.getTime());
    }

    public static Date getTodayDate() {
        java.util.Date utilDate = new java.util.Date();
        return new Date(utilDate.getTime());
    }

    public static Date parseDate(String dateTime) {
        if("".equals(dateTime)){
            return null;
        }
        java.util.Date utilDate = new java.util.Date();
        try {
            utilDate = LISTY_DATE_FORMATTER.parse(dateTime);
        } catch (ParseException pe) {
            Log.d(TAG, "parsing failed", pe);
        }
        return new Date(utilDate.getTime());
    }

    public static boolean isDueDatePassed(String dateString) {
        boolean flag = false;
        try {
            java.util.Date date = LISTY_DATE_FORMATTER.parse(dateString);
            java.util.Date currDate = new java.util.Date();
            flag = date.before(currDate) ? true : false;
        } catch (ParseException pe) {
            Log.d(TAG, "parsing failed", pe);
        }
        return flag;
    }
}
