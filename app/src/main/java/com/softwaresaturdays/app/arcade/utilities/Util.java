package com.softwaresaturdays.app.arcade.utilities;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.softwaresaturdays.app.arcade.models.User;

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
