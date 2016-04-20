package com.arellomobile.picwall.view.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.arellomobile.picwall.model.PictureItem;

import java.util.LinkedList;
import java.util.List;

public class PictureFragmentAdapter extends SmartFragmentStatePagerAdapter {
    private static int MAX_NUM_ITEMS = 3;
    private List<PictureItem> pictureItems;

    public PictureFragmentAdapter(FragmentManager fm) {
        super(fm);
        pictureItems = new LinkedList<>();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        String url = pictureItems.get(position).urlFullImage;
        return PictureFragment.getInstance(url);
    }

    @Override
    public int getCount() {
        return pictureItems.size();
    }


    // ------------------------- pictures ---------------------
    public void clear(){
        pictureItems.clear();
    }

    public void add(PictureItem pictureItem){
        pictureItems.add(pictureItem);
        if(pictureItems.size()> MAX_NUM_ITEMS) pictureItems.remove(0);
        notifyDataSetChanged();
    }

    public void addPrev(PictureItem pictureItem){
        pictureItems.add(0,pictureItem);
        int l = pictureItems.size();
        if(l > MAX_NUM_ITEMS) pictureItems.remove(l-1);
        notifyDataSetChanged();
    }

    public void addNext(PictureItem pictureItem){
        add(pictureItem);
    }
}
