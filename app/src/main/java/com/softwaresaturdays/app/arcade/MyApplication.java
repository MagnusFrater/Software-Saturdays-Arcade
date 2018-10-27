package com.softwaresaturdays.app.arcade;

import android.app.Application;

import com.softwaresaturdays.app.arcade.models.User;

public class MyApplication extends Application {
    public static User currUser;
    public static String fcmToken;
    public static boolean isForeground;
}
