package com.arellomobile.picwall.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.events.ServerPicturePageEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.view.grid.PageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GridView {
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PageAdapter pageAdapter;

    private List<PicturePage> pages;


    boolean isLoading;

    @Inject
    Context context;

    public GridView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.picture_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(context, Constants.GRID_ITEMS_IN_ROW);
        mRecyclerView.setLayoutManager(mLayoutManager);

        pages = new ArrayList<>(Constants.GRID_MAX_PAGES_IN_MEMORY+1);
        pageAdapter = new PageAdapter(pages);
        pageAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(pageAdapter);

        mRecyclerView.addOnScrollListener(new OnInfiniteScrollListener(mLayoutManager));


    }


    // ------------------------ Infinite Scroll --------------
    private class OnInfiniteScrollListener extends RecyclerView.OnScrollListener {
        GridLayoutManager mLayoutManager;

        public OnInfiniteScrollListener(GridLayoutManager layoutManager) {
            mLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean ScrollDown = dy > 0 ? true : false;
            boolean ScrollUp = dy < 0 ? true : false;

           if (ScrollUp && isSeenFirstItem(mLayoutManager)) {
                QueryLoadPrev();
            }
            else if (ScrollDown && isSeenLastItem(mLayoutManager)) {
               QueryLoadNext();
            }
        }

        private boolean isSeenFirstItem(GridLayoutManager gridLayoutManager) {
            int item = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
            if (item == 0) return true;
            return false;
        }

        private boolean isSeenLastItem(GridLayoutManager gridLayoutManager) {
            int item = gridLayoutManager.findLastVisibleItemPosition();
            // staggered view может показывать последние номера, меняя их местами в item
            for (int i = 0; i < Constants.GRID_ITEMS_IN_ROW; i++) {
                if (item - i == pageAdapter.getItemCount() - 1) return true;
            }
            return false;
        }


    }

    // ------------------------ queries for paging  --------------
     private void QueryLoadNext() {
        int nextPage = pages.get(pages.size()-1).numberNext;

        if (nextPage == 0 || isLoading) return;
        isLoading = true;
//        Log.d(Constants.LOG_TAG, "--- loading next page! --" + nextPage);
        EventBus.getDefault().post(new ViewRequestPage(nextPage));
        // включаем индикатор загрузки внизу
    }

    private void QueryLoadPrev() {
        int prevPage = pages.get(0).numberPrev;
        if (prevPage == 0 || isLoading) return;
        isLoading = true;
//        Log.d(Constants.LOG_TAG, "--- loading prev page! --" + prevPage); //
        EventBus.getDefault().post(new ViewRequestPage(prevPage));
        // включаем индикатор загрузки внизу
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
    public void onServerPicturePageEvent(ServerPicturePageEvent event) {

        PicturePage newPage = event.picturePage;
        newPage.title = Constants.GRID_PAGE_TITLE_PREFIX + " " + newPage.numberCurrent + " " + Constants.GRID_PAGE_TITLE_MID + " " + newPage.totalPageAmount + " " + Constants.GRID_PAGE_TITLE_POSTFIX;

        addPage(newPage);

        EventBus.getDefault().post(new LogEvent(" =  =  =  =  pagesInSet = " + pages.size()));

    }

    // Вставляет страницу в правильном порядке - сверху или снизу, согласно номерам
    private void addPage(PicturePage newPage) {
        boolean added = false;
        for(int i = 0; i<pages.size(); i++){
            if(pages.get(i).numberCurrent > newPage.numberCurrent){
                addPageInBegin(newPage);
                added = true;
                break;
            }
            else if(pages.get(i).numberCurrent == newPage.numberCurrent){
                pages.set(i,newPage);
                added = true;
                break;
            }
        }
        if(!added) addPageInEnd(newPage);

        Log.d(Constants.LOG_TAG," pages size = " + pages.size());

        pageAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    private void addPageInBegin(PicturePage newPage) {
        pages.add(0,newPage);

        if(pages.size()>Constants.GRID_MAX_PAGES_IN_MEMORY) {
            pages.remove(pages.size()-1);
            mRecyclerView.scrollToPosition(pages.get(0).pictures.size()+Constants.GRID_SCROLL_K);
        }
    }

    private void addPageInEnd(PicturePage newPage) {
        pages.add(newPage);

        // при удалении - адаптер дергается, то есть надо установить его позицию в current-removed
        if(pages.size()+1>Constants.GRID_MAX_PAGES_IN_MEMORY) {
            pages.remove(0);
            // устанавливаем позицию на конец предыдущей страницы
            mRecyclerView.scrollToPosition(pageAdapter.getItemCount()-pages.get(pages.size()-1).pictures.size()-Constants.GRID_SCROLL_K);
        }
        // добавляем после прокрутки, чтобы не дергалось


    }


}
