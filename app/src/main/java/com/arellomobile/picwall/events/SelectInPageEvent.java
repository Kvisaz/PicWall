package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

// Use page.getSelectedPicture() to get selected PictureItem
public class SelectInPageEvent {
    public final PicturePage page;

    public SelectInPageEvent(PicturePage page) {
        this.page = page;
    }
}
