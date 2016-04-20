package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class LoadPageEvent {
    public final PicturePage page;
    public final int pageType;

    public LoadPageEvent(PicturePage page, int pageType) {
        this.page = page;
        this.pageType = pageType;
    }
}
