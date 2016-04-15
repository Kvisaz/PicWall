package com.arellomobile.picwall.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.App;
import com.arellomobile.picwall.events.LoadBigPictureUrlEvent;
import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.events.ServerPicturePageEvent;
import com.arellomobile.picwall.events.ViewRequestPage;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.utilits.ColorUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PictureGridView {
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private PictureGridAdapter mAdapter;


    private List<PictureItem> pictures;

    private int prevPage;
    private int currentPage;
    private int nextPage;

    private boolean isLoading;

    // 3-page set for infinite scroll
    private List<PicturePage> pages;
    private int currentPageInSet;

    private final int MAX_PAGES_IN_SET = 3;

    @Inject
    public ImageLoader imageLoader;  // @Inject на конструкторе => без прямого вызова инжекции

    private final int ITEMS_IN_ROW = 2;
    private final int ITEM_LAYOUT_XML_ID = R.layout.picture_grid_item;
    private final int THUMB_STUB_R_ID = R.drawable.thumb_placeholder;
//    private final int THUMB_STUB_R_ID = R.drawable.progress_preview_animation;

    private final int PICTURE_MAX_WIDTH;

    public PictureGridView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.picture_recycler_view);
        mRecyclerView.setHasFixedSize(true);


        mLayoutManager = new StaggeredGridLayoutManager(ITEMS_IN_ROW, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        // мотивация отключения - не вижу смысла в мельтешении
        mRecyclerView.setItemAnimator(null);

        mRecyclerView.addOnScrollListener(new OnInfiniteScrollListener((StaggeredGridLayoutManager) mLayoutManager));

        mAdapter = new PictureGridAdapter();
        mRecyclerView.setAdapter(mAdapter);

        pages = new ArrayList<>();
        pictures = new ArrayList<>();

        // imageLoader = new ImageLoader(rootView.getContext());
        App.getComponent().injectImageLoader(this); // Dagger

        PICTURE_MAX_WIDTH = 240; // actual size needed
    }


    public class PictureGridAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(ITEM_LAYOUT_XML_ID, parent, false);
            ItemViewHolder ivh = new ItemViewHolder(view);
            return ivh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            PictureItem pic = pictures.get(position);
            ItemViewHolder ivh = (ItemViewHolder) holder;

            ivh.cardView.setCardBackgroundColor(pic.colorBackground);
            int colorText = ColorUtil.getContrastColor(pic.colorBackground);
            ivh.titleView.setTextColor(colorText);
            ivh.titleView.setText(pic.title);
            ivh.descView.setTextColor(colorText);
            ivh.descView.setText(pic.desc);
            ivh.imageView.setImageResource(THUMB_STUB_R_ID);

            // ImageLoader Magic - load from web or cache
            imageLoader.load(pic.urlThumbImage, ivh.imageView, PICTURE_MAX_WIDTH, true, null);
        }

        @Override
        public int getItemCount() {
            if (pictures == null) return 0;
            else return pictures.size();
        }

        private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView imageView;
            TextView titleView;
            TextView descView;
            CardView cardView;

            public ItemViewHolder(View itemView) {
                super(itemView);
                cardView = (CardView) itemView.findViewById(R.id.picItemCardView);
                imageView = (ImageView) itemView.findViewById(R.id.picItemImageView);
                titleView = (TextView) itemView.findViewById(R.id.picItemTitle);
                descView = (TextView) itemView.findViewById(R.id.picItemDescription);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                onSelectItem(v, this.getLayoutPosition());
            }
        }
    }

    // ------------------------ OnSelectItem Listener --------------
    private void onSelectItem(View v, int position) {
        String bigPicUrl = pictures.get(position).urlFullImage;

        EventBus.getDefault().post(new LogEvent("--- SELECT bigPicUrl----" + bigPicUrl));

        LoadBigPictureUrlEvent event = new LoadBigPictureUrlEvent(bigPicUrl);
        EventBus.getDefault().postSticky(event);
    }

    // ------------------------ Infinite Scroll --------------
    private class OnInfiniteScrollListener extends RecyclerView.OnScrollListener {
        StaggeredGridLayoutManager mStaggeredGridLayoutManager;

        public OnInfiniteScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mStaggeredGridLayoutManager = layoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean ScrollDown = dy > 0 ? true : false;
            boolean ScrollUp = dy < 0 ? true : false;

            if (ScrollUp && isSeenFirstItem(mStaggeredGridLayoutManager)) {
                Log.d(Constants.LOG_TAG, "--- begin of page! --"); //
                QueryLoadPrev();
            }

            if (ScrollDown && isSeenLastItem(mStaggeredGridLayoutManager)) {
                Log.d(Constants.LOG_TAG, "--- end of page! --"); //
                QueryLoadNext();
            }
        }

        private boolean isSeenFirstItem(StaggeredGridLayoutManager mStaggeredGridLayoutManager) {
            int[] items = mStaggeredGridLayoutManager.findFirstVisibleItemPositions(null);
//            if(items[0]==0) return true;
            for (int i = 0; i < ITEMS_IN_ROW; i++) {
                if (items[i] == 0) return true;
            }
            return false;
        }

        private boolean isSeenLastItem(StaggeredGridLayoutManager mStaggeredGridLayoutManager) {
            int[] items = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);
            // staggered view может показывать последние номера, меняя их местами в item
            for (int i = 0; i < ITEMS_IN_ROW; i++) {
                if (items[items.length - 1 - i] == mAdapter.getItemCount() - 1) return true;
            }
            return false;
        }


    }

    // ------------------------ queries for paging  --------------
    private void QueryLoadNext() {
        if (nextPage == 0 || isLoading) return;
        isLoading = true;
        Log.d(Constants.LOG_TAG, "--- loading next page! --" + nextPage); //
        EventBus.getDefault().post(new ViewRequestPage(nextPage));
        // включаем индикатор загрузки внизу

    }

    private void QueryLoadPrev() {
        if (prevPage == 0 || isLoading) return;
        isLoading = true;
        Log.d(Constants.LOG_TAG, "--- loading prev page! --" + prevPage); //
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



        prevPage = event.picturePage.numberPrev;
        currentPage = event.picturePage.numberCurrent;
        nextPage = event.picturePage.numberNext;

        EventBus.getDefault().post(new LogEvent(" prevPage = " + prevPage + " current = " + currentPage + " nextPage = " + nextPage));

        int oldCurrentPageInSet = currentPageInSet;
        EventBus.getDefault().post(new LogEvent(" =  =  =  =  oldCurrentPageInSet = " + oldCurrentPageInSet));
        if (pages.size() == 0) { // инициализация
            pages.add(event.picturePage);
            currentPageInSet = 0;
        } else if (currentPage < pages.get(oldCurrentPageInSet).numberCurrent) { // добавляем сверху
            pages.add(0, event.picturePage);
            removeLastPageFromSet();
            currentPageInSet = 0;
        } else if (currentPage > pages.get(oldCurrentPageInSet).numberCurrent) { // добавляем cнизу
            pages.add(event.picturePage);
            removeFirstPageFromSet();
            currentPageInSet = pages.size()-1;
        }

        EventBus.getDefault().post(new LogEvent(" =  =  =  =  pagesInSet = " + pages.size()));
        EventBus.getDefault().post(new LogEvent(" firstPageInSet = " + pages.get(0).numberCurrent + " lastPageInSet = " + pages.get(pages.size() - 1).numberCurrent + " maxPage = " + pages.get(0).totalPageAmount));

        EventBus.getDefault().post(new LogEvent(" =  =  =  =  picturesPerPage = " + pages.get(currentPageInSet).getNumberOfPages()));


        pictures = getListOfPictures(pages);
        EventBus.getDefault().post(new LogEvent(" =  =  =  =  pictures.size = " + pictures.size()));

        mAdapter.notifyDataSetChanged();

        isLoading = false; // конец загрузки, и тут ставим отключение индикатора
    }

    private void removeFirstPageFromSet() {
        if(pages.size()>MAX_PAGES_IN_SET){
            pages.remove(0);
        }
    }

    private void removeLastPageFromSet() {
        if(pages.size()>MAX_PAGES_IN_SET){
            pages.remove(pages.size()-1);
        }
    }

    private List<PictureItem> getListOfPictures(List<PicturePage> pages) {
        List<PictureItem> pictures = new ArrayList<>();
        for (PicturePage page : pages) {
            pictures.addAll(page.pictures);
        }
        return pictures;
    }


}
