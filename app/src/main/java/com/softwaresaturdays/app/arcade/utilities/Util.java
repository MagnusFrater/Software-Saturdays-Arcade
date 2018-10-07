package com.softwaresaturdays.app.arcade.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Util {

    public static String getFormattedTime(Double time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.longValue());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, MMM d\nh:mm:ss aa");
        return simpleDateFormat.format(calendar.getTime());
    }

}
