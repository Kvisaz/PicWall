package com.arellomobile.picwall;

import android.content.Context;

public class Constants {
    public static String APP_NAME;
    public static String LOG_TAG;

    public static String SERVER_MESSAGE_0;
    public static String SERVER_MESSAGE_SIGNED_1;
    public static String SERVER_MESSAGE_SIGNED_100;
    public static String SERVER_MESSAGE_UNKNOWN;


    public static void getResources(Context context){
        APP_NAME = context.getString(R.string.app_name);
        LOG_TAG = context.getString(R.string.logTag);
    }

}
