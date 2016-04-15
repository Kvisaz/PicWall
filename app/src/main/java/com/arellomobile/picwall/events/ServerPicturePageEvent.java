package com.arellomobile.picwall.events;

import com.arellomobile.picwall.model.PicturePage;

public class ServerPicturePageEvent {
    public final PicturePage picturePage;

    public ServerPicturePageEvent(PicturePage picturePage) {
        this.picturePage = picturePage;
    }
}
