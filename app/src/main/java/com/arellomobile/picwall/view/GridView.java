package com.arellomobile.picwall.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.Presenter;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.PageLoadEvent;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.view.grid.PageAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;

import javax.inject.Inject;

public class GridView {
    boolean noScrolling;
    @Inject
    Context context;
    @Inject
    Presenter presenter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PageAdapter pageAdapter;
    private LinkedList<PicturePage> pages;
    private int correctScroll;

    public GridView(View rootView) {
        App.getComponent().inject(this);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.picture_grid_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(context, Constants.GRID_ITEMS_IN_ROW);
        mRecyclerView.setLayoutManager(mLayoutManager);

        pages = presenter.pages;

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
            ScrollDown = true;
            ScrollUp = true;
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
            int item = gridLayoutManager.findLastCompletelyVisibleItemPosition();
            // staggered view может показывать последние номера, меняя их местами в item
            for (int i = 0; i < Constants.GRID_ITEMS_IN_ROW; i++) {
                if (item - i == pageAdapter.getItemCount() - 1) return true;
            }
            return false;
        }
    }

    // ------------------------ queries for paging  --------------
    private void QueryLoadNext() {
        if (noScrolling) return;
        noScrolling = true;
        Log.d(Constants.LOG_TAG," ------- QueryLoadNext ------- ");
        int num = pages.getLast().numberNext;
        if(num==0){
            Log.d(Constants.LOG_TAG," ------- no next page! ------- ");
            noScrolling = false;
            return;
        }
        Log.d(Constants.LOG_TAG," ------- asyncLoadPage ------- "+num);
        presenter.asyncLoadPage(num);
    }

    private void QueryLoadPrev() {
        if (noScrolling) return;
        noScrolling = true;
        Log.d(Constants.LOG_TAG," ------- QueryLoadPrev ------- ");
        int num = pages.getFirst().numberPrev;
        if(num==0){
            Log.d(Constants.LOG_TAG," ------- no prev page! ------- ");
            noScrolling = false;
            return;
        }
        presenter.asyncLoadPage(num);
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
    public void onLoadPage(PageLoadEvent event) {
        Log.d(Constants.LOG_TAG," ------- PageLoadEvent event ------- ");
        Log.d(Constants.LOG_TAG," ------- event.page.number ------- "+event.page.numberCurrent);

        correctScroll = event.correctScroll;
        if(correctScroll==Presenter.CORRECT_SCROLL_PREV){
            mRecyclerView.scrollToPosition(pages.getFirst().pictures.size() + 1 + Constants.GRID_SCROLL_K);
        }
        else if(correctScroll==Presenter.CORRECT_SCROLL_NEXT){
            int position = pageAdapter.getItemCount() - event.page.pictures.size() - pages.size()-Constants.GRID_SCROLL_K;
            mRecyclerView.scrollToPosition(position);
        }

        pageAdapter.notifyDataSetChanged();
        noScrolling = false;

    }
}