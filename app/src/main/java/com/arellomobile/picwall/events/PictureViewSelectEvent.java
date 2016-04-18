package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class PictureViewSelectEvent {
    public final int position;
    public final PicturePage page;

    public PictureViewSelectEvent(int position, PicturePage page) {
        this.position = position;
        this.page = page;
    }
}
