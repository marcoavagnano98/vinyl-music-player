package com.example.vinylmusicplayer.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static Date addSeconds(Date date, int seconds){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,seconds);
        return cal.getTime();
    }
    public static Date getDataFromTime(Long time){
        Calendar cal= Calendar.getInstance();
        cal.setTimeInMillis(time);
        return cal.getTime();
    }
    public static Date getCurrentDateTime(){
        return Calendar.getInstance().getTime();
    }
    public static Date getDateFromString(String string) throws ParseException {
        DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        Date date = format.parse(string);
        return date;
    }
}
