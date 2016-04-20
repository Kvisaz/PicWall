package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PictureItem;

public class PictureScrollPrevEvent {
    public final PictureItem prev;

    public PictureScrollPrevEvent(PictureItem prev) {
        this.prev = prev;
    }
}
