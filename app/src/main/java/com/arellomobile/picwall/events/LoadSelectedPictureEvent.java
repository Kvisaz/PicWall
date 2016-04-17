package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

// Use page.getSelectedPicture() to get selected PictureItem
public class LoadSelectedPictureEvent {
    public final PicturePage page;

    public LoadSelectedPictureEvent(PicturePage page) {
        this.page = page;
    }
}
