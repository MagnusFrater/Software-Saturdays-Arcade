package com.softwaresaturdays.app.arcade.utilities;

import com.softwaresaturdays.app.arcade.models.User;
import com.softwaresaturdays.app.arcade.networkHelpers.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Util {

    public static ArrayList<User> allUsers = new ArrayList<>();

    public static String getFormattedTime(Double time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.longValue());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, MMM d\nh:mm:ss aa");
        return simpleDateFormat.format(calendar.getTime());
    }
}
