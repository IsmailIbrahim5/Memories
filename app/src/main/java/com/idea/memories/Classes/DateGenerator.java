package com.idea.memories.Classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateGenerator {

    public String date;
    public int year,month,day;
    private static final String dateFormat = "yyyy/MM/dd";

    public DateGenerator(String date) {
        this.date = date;
        StringToInts();
    }

    public void StringToInts(){
        year = Integer.valueOf(date.substring(0,4));
        month = Integer.valueOf(String.valueOf(date.charAt(5)) + date.charAt(6));
        day = Integer.valueOf(String.valueOf(date.charAt(8)) + date.charAt(9));
    }

    public void IntsToString(){
        format();
    }


    public void setDate(String date) {
        this.date = date;
        StringToInts();
    }


    public void setYear(int year) {
        this.year = year;
        IntsToString();
    }

    public void setMonth(int month) {
        this.month = month;
        IntsToString();
    }

    public void setDay(int day) {
        this.day = day;
        IntsToString();
    }

    public String getDate() {
        return date;
    }

    public static String getDateOfToday (){
        return  new SimpleDateFormat(dateFormat).format(new Date());
    }

    public void format (){
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR , year);
        calendar.set(Calendar.MONTH , month);
        calendar.set(Calendar.DAY_OF_MONTH , day);
        date = sdf.format(calendar.getTime());
    }
}