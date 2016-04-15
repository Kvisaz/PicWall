package com.arellomobile.picwall.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple picture gallery page
 */
public class PicturePage {
    public List<PictureItem> pictures;
    public int numberCurrent;
    public int numberNext;
    public int numberPrev;
    public int totalPageAmount;
    public int totalPictureAmount;

    public PicturePage(){
        pictures = new ArrayList<>();
    }

    public int getNumberOfPages(){
        if(pictures==null) return 0;
        else return pictures.size();
    }
}
