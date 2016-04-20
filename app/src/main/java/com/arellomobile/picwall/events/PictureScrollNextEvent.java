package com.arellomobile.picwall.events;


import com.arellomobile.picwall.model.PictureItem;

public class PictureScrollNextEvent {
    public final PictureItem next;

    public PictureScrollNextEvent(PictureItem next) {
        this.next = next;
    }
}
