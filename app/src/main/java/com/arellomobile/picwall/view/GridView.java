package com.arellomobile.picwall.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.PictureViewSelectEvent;
import com.arellomobile.picwall.events.LoadPageEvent;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.presenter.Presenter;
import com.arellomobile.picwall.view.grid.PageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GridView {
    boolean isLoading;
    @Inject
    Context context;
    @Inject
    Presenter presenter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PageAdapter pageAdapter;
    private List<PicturePage> pages;

    public GridView(View rootView) {
        App.getComponent().inject(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.picture_grid_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(context, Constants.GRID_ITEMS_IN_ROW);
        mRecyclerView.setLayoutManager(mLayoutManager);

        pages = new ArrayList<>(Constants.GRID_MAX_PAGES_IN_MEMORY);

        pageAdapter = new PageAdapter(pages);
        pageAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(pageAdapter);

        mRecyclerView.addOnScrollListener(new OnInfiniteScrollListener(mLayoutManager));
    }

    // ------------------------ queries for paging  --------------
    private void QueryLoadNext() {
        if (isLoading) return;
        isLoading = true;
        presenter.loadNext(pages.get(pages.size()-1));

    }

    private void QueryLoadPrev() {
        if (isLoading) return;
        isLoading = true;
        presenter.loadPrev(pages.get(0));
    }

    // ------------------------ Вставляем и убираем страницы для Infinite Scroll  --------------
    // Вставляет страницу в правильном порядке - сверху или снизу, согласно номерам
    private void addPage(PicturePage newPage) {
        if(newPage==null){
            Log.d(Constants.LOG_TAG, " ------  null page event = " + pages.size());
            isLoading = false;
            return;
        }

        newPage.title = Constants.GRID_PAGE_TITLE_PREFIX + " " + newPage.numberCurrent + " " + Constants.GRID_PAGE_TITLE_MID + " " + newPage.totalPageAmount + " " + Constants.GRID_PAGE_TITLE_POSTFIX;

        boolean added = false;
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).numberCurrent > newPage.numberCurrent) {
                addPageInBegin(newPage);
                added = true;
                break;
            } else if (pages.get(i).numberCurrent == newPage.numberCurrent) {
                pages.set(i, newPage);
                added = true;
                break;
            }
        }
        if (!added) addPageInEnd(newPage);

        Log.d(Constants.LOG_TAG, " pages size = " + pages.size());

        pageAdapter.notifyDataSetChanged();
        isLoading = false;
    }

    private void addPageInBegin(PicturePage newPage) {
        pages.add(0, newPage);
        Log.d(Constants.LOG_TAG, " ------  addPageInBegin = ");

        if (pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY) {
            pages.remove(pages.size() - 1);
            Log.d(Constants.LOG_TAG, " ------  pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY = ");
            mRecyclerView.scrollToPosition(pages.get(0).pictures.size() + Constants.GRID_SCROLL_K);
        }
    }

    private void addPageInEnd(PicturePage newPage) {
        pages.add(newPage);
        Log.d(Constants.LOG_TAG, " ------  addPageInEnd = ");
        PicturePage loadpage = newPage;

        // при удалении - адаптер дергается, то есть надо установить его позицию в currentPosition-removed
        if (pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY) {
            pages.remove(0);
            Log.d(Constants.LOG_TAG, " ------  pages.size() > Constants.GRID_MAX_PAGES_IN_MEMORY = ");
            // устанавливаем позицию на конец предыдущей страницы
            mRecyclerView.scrollToPosition(pageAdapter.getItemCount() - pages.get(pages.size() - 1).pictures.size() - Constants.GRID_SCROLL_K);
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
    public void onLoadPage(LoadPageEvent event) {
         PicturePage newPage = event.page;
         addPage(newPage);
          Log.d(Constants.LOG_TAG, " ------ LoadPageEvent event,  pagesInSet = " + pages.size());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelect(PictureViewSelectEvent event) {
        // find page current number in page collection
        int pagePosition = pages.indexOf(event.page);

        int pageSumSize = 0;
        for (PicturePage page : pages) {
            if (page == event.page) break;
            pageSumSize += page.getNumberOfPictures() + 1; // заголовок секции
        }

        int realPosition = pageSumSize + event.position + 1;
        mRecyclerView.smoothScrollToPosition(realPosition);
        View view = mLayoutManager.findViewByPosition(realPosition); // заголовок секции добавляет +1
        pageAdapter.highlight(view);
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
            } else if (ScrollDown && isSeenLastItem(mLayoutManager)) {
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

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollNextPage(PictureViewScrollNextPageEvent event) {
        QueryLoadNext();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScrollPrevPage(PictureViewScrollPrevPageEvent event) {
        QueryLoadPrev();
    }*/


}
