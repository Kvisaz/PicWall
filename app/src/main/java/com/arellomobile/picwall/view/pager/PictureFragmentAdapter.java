package com.arellomobile.picwall.view.pager;

import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.model.PictureItem;

import java.util.LinkedList;

public class PictureFragmentAdapter extends FragmentStatePagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>(); // *SmartFragmentStatePagerAdapter
    private static int MAX_NUM_ITEMS = 3;
    private LinkedList<PictureItem> pictureItems;

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
        PictureItem item=null;
        if(position<pictureItems.size()){
            item = pictureItems.get(position);
        }
        Log.d(Constants.LOG_TAG,"--- position in PFA ---- "+position);
        if(item!=null){
            return PictureFragment.getInstance(item.urlFullImage);
        }
        else
            return PictureFragment.getInstance(null);
    }

    @Override
    public int getCount() {
        return MAX_NUM_ITEMS;
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
    // just add one picture
    public void set(PictureItem pictureItem){
        pictureItems.clear();
        pictureItems.add(null);
        pictureItems.add(pictureItem);
        pictureItems.add(null);
        notifyDataSetChanged();
    }

    public void addNext(PictureItem pictureItem){
        pictureItems.removeFirst();
        pictureItems.set(1,pictureItem);
        pictureItems.add(new PictureItem());
        notifyDataSetChanged();
    }

    public void addPrev(PictureItem pictureItem){
        pictureItems.addFirst(new PictureItem());
        pictureItems.set(1,pictureItem);
        pictureItems.removeLast();
        notifyDataSetChanged();
    }

    public void setStartPage(){
        PictureItem special = new PictureItem();
        special.urlFullImage=PictureFragment.START_PAGE_TAG;
        pictureItems.set(0,special);
        notifyDataSetChanged();
    }

    public void setFinalPage(){
        PictureItem special = new PictureItem();
        special.urlFullImage=PictureFragment.FINAL_PAGE_TAG;
        pictureItems.set(2,special);
        notifyDataSetChanged();
    }

}
