package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class PageLoadEvent {
    public final PicturePage page;
    public final int correctScroll;

    public PageLoadEvent(PicturePage page, int correctScroll) {
        this.page = page;
        this.correctScroll = correctScroll;
    }
}
