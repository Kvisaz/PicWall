package com.arellomobile.picwall;

import android.content.Context;

public class Constants {
    public static String APP_NAME;
    public static String LOG_TAG;

    public static final int PREV = 0;
    public static final int CURRENT = 1;
    public static final int NEXT = 2;


    public static int GRID_PICTURE_MAX_WIDTH;
    public static int GRID_ITEMS_IN_ROW;
    public static int GRID_SCROLL_K;
    public static String GRID_PAGE_TITLE_PREFIX;
    public static String GRID_PAGE_TITLE_MID;
    public static String GRID_PAGE_TITLE_POSTFIX;

    public final static int GRID_PICTURE_SELECT_ANIMATION_TIME = 1500;

    public final static int PICTURE_VIEWER_MAX_PAGES = 3;
    public static int PICTURE_VIEWER_MAX_WIDTH;

    public static String PICTURE_VIEWER_START_PAGE_TEXT;
    public static String PICTURE_VIEWER_FINAL_PAGE_TEXT;


    public final static int GRID_MAX_PAGES_IN_MEMORY = 3;

    public static boolean IS_TABLET;
    public static boolean IS_LANDSCAPE;
    public static boolean IS_PORTRAIT;

    public static void getResources(Context context){
        APP_NAME = context.getString(R.string.app_name);
        LOG_TAG = context.getString(R.string.logTag);

        GRID_PAGE_TITLE_PREFIX = context.getString(R.string.grid_page_title_prefix);
        GRID_PAGE_TITLE_MID = context.getString(R.string.grid_page_title_mid);
        GRID_PAGE_TITLE_POSTFIX = context.getString(R.string.grid_page_title_postfix);

        IS_TABLET = context.getResources().getBoolean(R.bool.isTablet);

        PICTURE_VIEWER_START_PAGE_TEXT = context.getString(R.string.picture_viewer_nopage_text_start);
        PICTURE_VIEWER_FINAL_PAGE_TEXT = context.getString(R.string.picture_viewer_nopage_text_final);

        getResourcesForOrientation(context);
    }

    public static void getResourcesForOrientation(Context context){

        IS_PORTRAIT = context.getResources().getBoolean(R.bool.is_portrait);
        IS_LANDSCAPE = context.getResources().getBoolean(R.bool.is_landscape);

        /*
        *   Контроль числа рядов в сетке
        * */
        if(IS_TABLET)
        {
            if(IS_LANDSCAPE) {
                GRID_ITEMS_IN_ROW = 2;
                GRID_SCROLL_K = 3;
            }
            else{
                GRID_ITEMS_IN_ROW = 5;
                GRID_SCROLL_K = 6;
            }
        }
        else{ // PHONE
            if(IS_LANDSCAPE) {
                GRID_ITEMS_IN_ROW = 4;
                GRID_SCROLL_K = 2;
            }
            else{
                GRID_ITEMS_IN_ROW = 2;
                GRID_SCROLL_K = 2;
            }
        }

        /*
        *    Небольшая оптимизация памяти для портретного режима (ширина большой картинки поменьше)
        * */

        GRID_PICTURE_MAX_WIDTH =  context.getResources().getInteger(R.integer.grid_picture_max_width);
        PICTURE_VIEWER_MAX_WIDTH =  context.getResources().getInteger(R.integer.picture_viewer_max_width);
    }

}
