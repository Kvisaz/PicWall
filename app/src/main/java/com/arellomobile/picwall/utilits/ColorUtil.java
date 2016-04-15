package com.arellomobile.picwall.utilits;

import android.graphics.Color;

public class ColorUtil {
    public static boolean isBrightColor(int color) {
        if (android.R.color.transparent == color)
            return true;

        boolean rtnValue = false;

        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };

        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
                * rgb[1] * .691 + rgb[2] * rgb[2] * .068);

        // color is light
        if (brightness >= 200) {
            rtnValue = true;
        }

        return rtnValue;
    }

    public static int getContrastColor(int colorBg){
        int color = ColorUtil.isBrightColor(colorBg) ? Color.BLACK : Color.WHITE;
        return color;
    }
}
