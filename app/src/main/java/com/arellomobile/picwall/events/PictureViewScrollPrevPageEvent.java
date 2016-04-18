package com.arellomobile.picwall.events;
import com.arellomobile.picwall.model.PicturePage;

public class PictureViewScrollPrevPageEvent {
    public final PicturePage page;

    public PictureViewScrollPrevPageEvent(PicturePage page) {
        this.page = page;
    }
}
