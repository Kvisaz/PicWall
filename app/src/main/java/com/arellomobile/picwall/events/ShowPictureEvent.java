package com.arellomobile.picwall.events;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;

public class ShowPictureEvent {
    public final PicturePage currentPage;
    public final PictureItem pictureItem;

    public ShowPictureEvent(PicturePage currentPage) {
        this.currentPage = currentPage;
        this.pictureItem = currentPage.getSelectedPicture();
    }
}
