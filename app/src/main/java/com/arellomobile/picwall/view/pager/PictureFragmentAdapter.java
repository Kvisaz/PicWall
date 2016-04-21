package com.arellomobile.picwall.view.pager;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arellomobile.picwall.R;
import com.arellomobile.picwall.model.PictureItem;

import java.util.LinkedList;
import java.util.List;

public class PictureFragmentAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>(); // *SmartFragmentStatePagerAdapter
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

    // ------------------------- bitmap viewpager memory leak patch ---------------------
    // see http://stackoverflow.com/questions/13586963/android-viewpager-with-images-memory-leak-app-crashes
    //  also used SmartFragmentStatePagerAdapter
    //   https://gist.github.com/nesquena/c715c9b22fb873b1d259

    // Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        registeredFragments.put(position, fragment); // *SmartFragmentStatePagerAdapter

        return fragment;
    }

    // Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        registeredFragments.remove(position); // *SmartFragmentStatePagerAdapter
        View view = ((Fragment)object).getView();
        ImageView imgView = (ImageView) view.findViewById(R.id.picture_viewer_imageview);
        BitmapDrawable bmpDrawable = (BitmapDrawable) imgView.getDrawable();
        if (bmpDrawable != null && bmpDrawable.getBitmap() != null) {
            // This is the important part - http://stackoverflow.com/questions/13586963/android-viewpager-with-images-memory-leak-app-crashes
            bmpDrawable.getBitmap().recycle();
        }

        super.destroyItem(container, position, object);
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
