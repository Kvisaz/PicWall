package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class PictureNextLoadSuccessEvent {
    public final PicturePage page;

    public PictureNextLoadSuccessEvent(PicturePage page) {
        this.page = page;
    }
}
