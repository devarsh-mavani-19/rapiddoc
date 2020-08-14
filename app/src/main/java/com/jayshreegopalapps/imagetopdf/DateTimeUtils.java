package com.jayshreegopalapps.imagetopdf;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date d = new Date();
        d.setTime(System.currentTimeMillis());
        return sdf.format(d);
    }

    public static String getDate() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Date d2 = new Date();
        d2.setTime(System.currentTimeMillis());
        return sdf2.format(d2);
    }
}
