package com.arellomobile.picwall.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple picture gallery page
 *
 *  todo
 *
 */
public class PicturePage {
    public List<PictureItem> pictures;
    public int numberCurrent;
    public int numberNext;
    public int numberPrev;
    public int totalPageAmount;
    public int totalPictureAmount;

    public String title;

    private int selectedPicture;

    public PicturePage(){
        pictures = new ArrayList<>();
        title = "";
        selectedPicture = 0;
    }

    public int getNumberOfPictures(){
        if(pictures==null) return 0;
        else return pictures.size();
    }

    public void setSelected(int selectedPicture){
        this.selectedPicture = selectedPicture;
        if(selectedPicture < 0) this.selectedPicture = 0;
        else if (selectedPicture > pictures.size()) this.selectedPicture = pictures.size()-1;
    }

    public int getSelected(){
        return selectedPicture;
    }

    public PictureItem getSelectedPicture(){
        return pictures.get(selectedPicture);
    }
}
