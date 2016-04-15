package com.arellomobile.picwall.events;

public class LoadBigPictureUrlEvent {
    public final String pictureUrl;

    public LoadBigPictureUrlEvent(String url) {
        this.pictureUrl = url;
    }
}
