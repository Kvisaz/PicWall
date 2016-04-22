package com.arellomobile.picwall.model;

/**
 * Simple gallery item
 */
public class PictureItem {
    public String urlFullImage;
    public String urlThumbImage;
    public int width;
    public int height;
    public String title;
    public String desc;

    public int bytes;

    public int colorBackground;
    public int colorBackground2;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!PictureItem.class.isAssignableFrom(obj.getClass())) return false;

        final PictureItem other = (PictureItem) obj;
        if ((this.urlFullImage == null) ? (other.urlFullImage != null) : !this.urlFullImage.equals(other.urlFullImage)) {
            return false;
        }

        if (this.width != other.width || this.height != other.height)
            return false;

        return true;
    }
}
