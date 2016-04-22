package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class PicturePrevLoadSuccessEvent {
    public final PicturePage page;

    public PicturePrevLoadSuccessEvent(PicturePage page) {
        this.page = page;
    }
}
