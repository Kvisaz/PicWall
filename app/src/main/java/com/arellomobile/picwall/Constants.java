package com.arellomobile.picwall;

import android.content.Context;

public class Constants {
    public static String APP_NAME;
    public static String LOG_TAG;

    public static final int PREV = 0;
    public static final int CURRENT = 1;
    public static final int NEXT = 2;


    public static int GRID_PICTURE_MAX_WIDTH;
    public final static int GRID_ITEMS_IN_ROW = 2;
    public final static int GRID_SCROLL_K = GRID_ITEMS_IN_ROW*2+1;
    public static String GRID_PAGE_TITLE_PREFIX;
    public static String GRID_PAGE_TITLE_MID;
    public static String GRID_PAGE_TITLE_POSTFIX;

    public final static int GRID_PICTURE_SELECT_ANIMATION_TIME = 1500;

    public final static int PICTURE_VIEWER_MAX_PAGES = 3;
    public static int PICTURE_VIEWER_MAX_WIDTH;


    public final static int GRID_MAX_PAGES_IN_MEMORY = 3;


    public static String SERVER_MESSAGE_0;
    public static String SERVER_MESSAGE_SIGNED_1;
    public static String SERVER_MESSAGE_SIGNED_100;
    public static String SERVER_MESSAGE_UNKNOWN;



    public static void getResources(Context context){
        APP_NAME = context.getString(R.string.app_name);
        LOG_TAG = context.getString(R.string.logTag);

        GRID_PAGE_TITLE_PREFIX = context.getString(R.string.grid_page_title_prefix);
        GRID_PAGE_TITLE_MID = context.getString(R.string.grid_page_title_mid);
        GRID_PAGE_TITLE_POSTFIX = context.getString(R.string.grid_page_title_postfix);

        GRID_PICTURE_MAX_WIDTH =  context.getResources().getInteger(R.integer.grid_picture_max_width);
        PICTURE_VIEWER_MAX_WIDTH =  context.getResources().getInteger(R.integer.picture_viewer_max_width);
    }

}
