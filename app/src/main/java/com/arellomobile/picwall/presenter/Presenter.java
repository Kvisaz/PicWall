package com.arellomobile.picwall.presenter;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.arellomobile.picwall.Constants;

import com.arellomobile.picwall.events.LogEvent;

import com.arellomobile.picwall.events.LoadPageEvent;
import com.arellomobile.picwall.events.PictureScrollNextEvent;
import com.arellomobile.picwall.events.PictureScrollPrevEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.network.Client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Presenter {
    private final Context context;
    ExecutorService executorService;

    private final int PAGE_MIN = 1;
    private int PAGE_MAX = 2;


    ArrayList<PicturePage> pages;
    PictureItem prevPicture;
    PictureItem currentPicture;
    PictureItem nextPicture;

    boolean isWaitingPrevPicture;
    boolean isWaitingNextPicture;

    /*
    *    common page numeration - from 1 to max
    *    picture in page numeration - from 0 to page.size-1
    *
    * */

    public Presenter(Context context) {
        this.context = context;
        pages = new ArrayList<>(3);
        pages.add(null);
        pages.add(null);
        pages.add(null);

        executorService = Executors.newFixedThreadPool(1);

        loadStartPage(); // start page
    }


    // ------------------------ Check Network --------------------

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // ------------------------ Pictures  --------------------
    public void setCurrentPicture(int position) {
        pages.get(Constants.CURRENT).setSelected(position);
        currentPicture = pages.get(Constants.CURRENT).getSelectedPicture();
    }

    public void scrollNextPicture() {
        PicturePage currentPage = pages.get(Constants.CURRENT);
        Log.d(Constants.LOG_TAG, " ------ Presenter.scrollNextPicture --- ");
        int selected = currentPage.getSelected();
        int max = currentPage.getNumberOfPictures() - 1;
        int next = selected + 1;
        if (next < max) {
            prevPicture = currentPage.getSelectedPicture();
            currentPage.setSelected(next);
            currentPicture = currentPage.getSelectedPicture();
            nextPicture = currentPage.pictures.get(next + 1);
            EventBus.getDefault().post(new PictureScrollNextEvent(nextPicture));
        } else {
            isWaitingNextPicture = true;
            scrollPageNext();
        }


    }

    public void scrollPrevPicture() {
        PicturePage currentPage = pages.get(Constants.CURRENT);
        int selected = currentPage.getSelected();
        Log.d(Constants.LOG_TAG, " ------ Presenter.scrollPrevPicture --- ");
        int prev = selected - 1;
        if (prev > 0) {
            nextPicture = currentPage.getSelectedPicture();
            currentPage.setSelected(prev);
            currentPicture = currentPage.getSelectedPicture();
            prevPicture = currentPage.pictures.get(prev - 1);
            EventBus.getDefault().post(new PictureScrollPrevEvent(prevPicture));
        } else {
            isWaitingPrevPicture = true;
            scrollPagePrev();
        }


    }

    // ------------------------ Pages  --------------------
    public void loadStartPage() {
        loadPagePackFor(1);
    }

    public void loadPagePackFor(int currentNum) {
        asyncLoadPage(currentNum, Constants.CURRENT);
        asyncLoadPage(currentNum - 1, Constants.PREV);
        asyncLoadPage(currentNum + 1, Constants.NEXT);
    }

    public void scrollPageNext() {
        if (pages.get(Constants.NEXT) == null) return; // no next page
        // scroll next is shift up
        pages.set(Constants.PREV, pages.get(Constants.CURRENT));
        pages.set(Constants.CURRENT, pages.get(Constants.NEXT));
        int next = pages.get(Constants.CURRENT).numberNext;
        asyncLoadPage(next, Constants.NEXT);
    }

    public void loadNext(PicturePage page) {
        asyncLoadPage(page.numberNext, Constants.NEXT);
    }

    public void scrollPagePrev() {
        if (pages.get(Constants.PREV) == null) return; // no prev page
        // scroll next is shift down
        pages.set(Constants.NEXT, pages.get(Constants.CURRENT));
        pages.set(Constants.CURRENT, pages.get(Constants.PREV));
        int prev = pages.get(Constants.CURRENT).numberPrev;
        asyncLoadPage(prev, Constants.PREV);
    }

    public void loadPrev(PicturePage page) {
        asyncLoadPage(page.numberPrev, Constants.PREV);
    }

    // ------------------------ Async Calls --------------------
    public void asyncLoadPage(int num, int type) {
        executorService.submit(new LoadPageTask(num, type));
    }

    // ------------------------ Async Tasks --------------------
    private class LoadPageTask implements Runnable {
        private final int pageNum;
        private final int pageType;

        LoadPageTask(int pageNum, int type) {
            this.pageNum = pageNum;
            this.pageType = type;

        }

        @Override
        public void run() {
            PicturePage page = null;
            int max = PAGE_MAX;
            if (pageNum >= PAGE_MIN && pageNum <= PAGE_MAX) {
                page = Client.getPage(pageNum);
                if (page != null) max = page.totalPageAmount;
            }
            synchronized (this) {
                pages.set(pageType, page);
                PAGE_MAX = max;
            }

            sendPage(page, pageType);

            if (isWaitingNextPicture) {
                isWaitingNextPicture = false;
                if (page != null) {
                    page.setSelected(0);
                    nextPicture = page.getSelectedPicture();
                    EventBus.getDefault().post(new PictureScrollNextEvent(nextPicture));
                }
            }
            if (isWaitingPrevPicture) {
                isWaitingPrevPicture = false;
                if (page != null) {
                    page.setSelected(page.getNumberOfPictures() - 1);
                    prevPicture = page.getSelectedPicture();
                    EventBus.getDefault().post(new PictureScrollPrevEvent(prevPicture));
                }
            }

            Log.d(Constants.LOG_TAG, "-------- LoadPageTask complete. -- Get page " + pageNum + "--------  pageType =  " + pageType + " not null - " + (pages.get(pageType) != null));
            Log.d(Constants.LOG_TAG, "--------  PAGE_MAX =  " + PAGE_MAX);
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
    @Subscribe
    public void onViewRequestPage(ViewRequestPage event) {
        Client.getPageAsync(event.pagenumber);
    }

    @Subscribe
    public void onLog(LogEvent event) {
        Log.d(Constants.LOG_TAG, event.message);
    }

    // ------------------------ EventBus senders  --------------
    public void sendPage(PicturePage page, int pageType) {
        EventBus.getDefault().post(new LoadPageEvent(page, pageType));
    }


}
