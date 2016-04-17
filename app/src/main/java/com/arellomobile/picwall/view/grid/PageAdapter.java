package com.arellomobile.picwall.view.grid;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.events.LoadSelectedPictureEvent;
import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.utilits.ColorUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

public class PageAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    @Inject
    ImageLoader imageLoader;
    private List<PicturePage> pages;

    public PageAdapter(List<PicturePage> pages){
        this.pages = pages;
        App.getComponent().injectImageLoader(this);
    }

    @Override
    public int getSectionCount() {
        return pages.size();
    }

    @Override
    public int getItemCount(int page) {
        return pages.get(page).pictures.size();
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        String title = pages.get(section).title;
        SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.pageTitleTextView.setText(title);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int page, int relativePosition, int absolutePosition) {
        List<PictureItem> pictures = pages.get(page).pictures;

        PictureItem pic = pictures.get(relativePosition);
        ItemViewHolder ivh = (ItemViewHolder) holder;

        ivh.cardView.setCardBackgroundColor(pic.colorBackground);
        int colorText = ColorUtil.getContrastColor(pic.colorBackground);
        ivh.titleView.setTextColor(colorText);
        ivh.titleView.setText(pic.title);
        ivh.descView.setTextColor(colorText);
        ivh.descView.setText(pic.desc);
        ivh.imageView.setImageResource(R.drawable.thumb_placeholder);

        // ImageLoader Magic - load from web or cache
        imageLoader.load(pic.urlThumbImage, ivh.imageView, Constants.GRID_PICTURE_MAX_WIDTH, true, null);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_grid_page_title,parent,false);
            return new SectionViewHolder(v);
       }
        else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_grid_item,parent,false);
            return new ItemViewHolder(v);
        }
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
            onSelectItem(v, this.getAdapterPosition());
        }
    }

    private void onSelectItem(View v, int layoutPosition) {
        float total = (float)getItemCount();

        int page = (int)Math.floor(total / layoutPosition);
        int picture_in_page = getItemCount() % layoutPosition;

        int listedPictures = 0;
        int previousPagesSize = 0;
        for(int i = 0;i<pages.size();i++){
            int pageSize = pages.get(i).pictures.size() + 1; // +1 - это секции
            previousPagesSize = listedPictures;
            listedPictures += pageSize;
            if(layoutPosition<listedPictures){
                page = i;
                picture_in_page = layoutPosition - previousPagesSize - 1; // -1 потому что текущая секция
                break;
            }
        }

        Log.d(Constants.LOG_TAG,"layoutPosition = " + layoutPosition + " total=" + total + " page =  "+page + " picture_in_page="+picture_in_page);

        PicturePage pageCurrent = pages.get(page);
        pageCurrent.setSelectedPicture(picture_in_page);
        LoadSelectedPictureEvent event = new LoadSelectedPictureEvent(pageCurrent);
        EventBus.getDefault().postSticky(event);
    }

    // SectionViewHolder Class for Sections
    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        final TextView pageTitleTextView;
        public SectionViewHolder(View itemView) {
            super(itemView);
            pageTitleTextView = (TextView) itemView.findViewById(R.id.pageTitle);
        }
    }

}
