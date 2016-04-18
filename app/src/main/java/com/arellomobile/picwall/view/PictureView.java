package com.arellomobile.picwall.view;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.LoadSelectedPictureEvent;
import com.arellomobile.picwall.events.PictureViewScrollNextPageEvent;
import com.arellomobile.picwall.events.PictureViewScrollPrevPageEvent;
import com.arellomobile.picwall.events.PictureViewSelectEvent;
import com.arellomobile.picwall.events.ProgressFullEvent;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.view.bigpicture.PictureAdapter;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PictureView {
    @Inject
    public ImageLoader imageLoader;
    @Inject
    Context context;

    private List<PictureItem> pictures;
    private PicturePage currentPage;
    private int currentPositionInPage;
    private int currentPositionInList;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private PictureAdapter pictureAdapter;

    public PictureView(View rootView) {
        App.getComponent().inject(this);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.picture_viewer_recycleview);
        mLinearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        pictures = new ArrayList<>(Constants.GRID_MAX_PAGES_IN_MEMORY);
        pictureAdapter = new PictureAdapter(pictures);
        mRecyclerView.setAdapter(pictureAdapter);

        mRecyclerView.addOnScrollListener(new OnInfiniteScrollListener(mLinearLayoutManager));

    }

    // ------------------------ Infinite Scroll --------------
    private class OnInfiniteScrollListener extends RecyclerView.OnScrollListener {
        LinearLayoutManager mLayoutManager;

        public OnInfiniteScrollListener(LinearLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean ScrollRight = dx > 0 ? true : false;
            boolean ScrollLeft = dx < 0 ? true : false;

            if (ScrollLeft && isSeenFirstItem(mLayoutManager)) {
                Log.d(Constants.LOG_TAG,"------- START PICTURE ------");
                currentPositionInPage = currentPage.pictures.indexOf(pictures.get(0));
                EventBus.getDefault().post(new PictureViewSelectEvent(currentPositionInPage, currentPage));
                insertPrevPic(currentPositionInPage);
            }
            else if (ScrollRight && isSeenLastItem(mLayoutManager)) {
                Log.d(Constants.LOG_TAG,"------- END PICTURE ------");
                currentPositionInPage = currentPage.pictures.indexOf(pictures.get(pictures.size()-1));
                EventBus.getDefault().post(new PictureViewSelectEvent(currentPositionInPage, currentPage));
                insertNextPic(currentPositionInPage);
            }
        }

        private boolean isSeenFirstItem(LinearLayoutManager gridLayoutManager) {
            int item = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (item == 0) return true;
            return false;
        }

        private boolean isSeenLastItem(LinearLayoutManager gridLayoutManager) {
            int item = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            if (item == pictureAdapter.getItemCount() - 1) return true;
            return false;
        }

    }
    // ------------------------ load new picture ----------------
    public void loadNewPicture(PicturePage page){
        currentPage = page;
        currentPositionInPage = page.getSelected();

        Log.d(Constants.LOG_TAG,"loadNewPicture currentPositionInPage = " + currentPositionInPage);

        pictures.clear();
        pictures.add(currentPage.getSelectedPicture());
        insertPrevNextPictures();
        pictureAdapter.notifyDataSetChanged();
    }

    // ------------------------ insert prev and next picture ----------------
    private void insertPrevPic(int position) {
        if(position>0) {
            pictures.add(0,currentPage.pictures.get(position-1));
            Log.d(Constants.LOG_TAG,"--------- insertPrevPic = ");
            Log.d(Constants.LOG_TAG,"---------  position of new ==  "+(position-1));
            Log.d(Constants.LOG_TAG,"---------  pictures.size() ==  "+pictures.size());
            if(pictures.size()>Constants.PICTURE_VIEWER_MAX_PAGES) {
                Log.d(Constants.LOG_TAG,"---------  pictures.remove(pictures.size()-1 ------ ");
                pictures.remove(pictures.size()-1);
            }
            mRecyclerView.scrollToPosition(1);
            pictureAdapter.notifyDataSetChanged();
        }
        else{
            EventBus.getDefault().post(new PictureViewScrollPrevPageEvent(currentPage));
        }
    }

    private void insertNextPic(int position) {
        if(position<currentPage.pictures.size()-1){
            pictures.add(currentPage.pictures.get(position+1));
            Log.d(Constants.LOG_TAG,"--------- insertNextPic = ");
            Log.d(Constants.LOG_TAG,"---------  position of new ==  "+(position+1));
            Log.d(Constants.LOG_TAG,"---------  pictures.size() ==  "+pictures.size());

            if(pictures.size()>Constants.PICTURE_VIEWER_MAX_PAGES) {
                Log.d(Constants.LOG_TAG,"---------  pictures.remove(0)------ ");
                pictures.remove(0);
                mRecyclerView.scrollToPosition(pictures.size()-2); // предпоследняя
            }
            pictureAdapter.notifyDataSetChanged();
        }
        else{
            // заказать NEXT страницу и картинку из неё
            EventBus.getDefault().post(new PictureViewScrollNextPageEvent(currentPage));
        }
    }

    // ------------------------ EventBus register --------------
    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    // ------------------------ EventBus handlers --------------
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoadSelectedPicture(LoadSelectedPictureEvent event) {
        loadNewPicture(event.page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressFull (ProgressFullEvent event){
//        insertPrevNextPictures();
    }
    
    private void insertPrevNextPictures() {
        // start
        if(pictures.size()<2){
            insertPrevPic(currentPositionInPage);
            insertNextPic(currentPositionInPage);
        }

    }



}
