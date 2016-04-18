package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class PictureViewScrollNextPageEvent {
    public final PicturePage page;

    public PictureViewScrollNextPageEvent(PicturePage page) {
        this.page = page;
    }
}
