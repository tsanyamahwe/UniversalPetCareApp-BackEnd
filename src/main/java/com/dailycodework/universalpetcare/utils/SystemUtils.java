package com.dailycodework.universalpetcare.utils;

import java.util.Calendar;
import java.util.Date;

public class SystemUtils {
    public static final int EXPIRATION_TIME = 10;//expiring in 10 minutes time

    public static Date getExpirationTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
