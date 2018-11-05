package com.softwaresaturdays.app.arcade.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.softwaresaturdays.app.arcade.models.Game;
import com.softwaresaturdays.app.arcade.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Util {

    public static final Random random = new Random();
    public static final String USERS_KEY = "users";
    public static final String PREF = "PREF";
    public static ArrayList<User> allUsers = new ArrayList<>();

    public static String getFormattedTime(Double time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time.longValue());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, MMM d, h:mm aa");
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

    public static void storeUserData(ArrayList<User> users, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (User user : users) {
            editor.putString(user.getUid(), user.toString());
        }

        editor.apply();
    }

    public static void storeGameData(ArrayList<Game> games, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        for (Game game : games) {
            editor.putString(game.getTitle(), game.toString());
        }

        editor.apply();
    }

    public static Game getGameData(String gameTitle, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        return new Game(prefs.getString(gameTitle, ""));
    }


    public static User getUserData(String uid, Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);

        return new User(prefs.getString(uid, ""));
    }

    public static int getRandInt(final int min, final int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static float convertDpToPixel(final float dp, final Context context) {
        final Resources resources = context.getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
