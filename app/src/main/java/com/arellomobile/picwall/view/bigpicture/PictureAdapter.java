package com.arellomobile.picwall.view.bigpicture;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.picwall.App;
import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.R;
import com.arellomobile.picwall.imageloader.ImageLoader;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.view.progress.ProgressIndicator;
import com.arellomobile.picwall.view.progress.SignalProgressIndicator;

import java.util.List;

import javax.inject.Inject;

public class PictureAdapter extends RecyclerView.Adapter {
    @Inject
    Context context;
    @Inject
    ImageLoader imageLoader;

    private List<PictureItem> pictures;

    public PictureAdapter(List<PictureItem> pictures){
        this.pictures = pictures;
        App.getComponent().inject(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_viewer_item,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder ivh = (ItemViewHolder) holder;
        PictureItem pictureItem = pictures.get(position);
        int PICTURE_MAX_WIDTH = ivh.imageView.getWidth(); // todo ????? picture width ???

        ivh.imageView.setImageBitmap(null);
        imageLoader.load(pictureItem.urlFullImage, ivh.imageView, PICTURE_MAX_WIDTH, false, ivh.progressIndicator);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener  {
        ImageView imageView;
        SignalProgressIndicator progressIndicator;

        public ItemViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.picture_viewer_imageview);

            ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.picture_viewer_progress_bar);
            TextView textView = (TextView) view.findViewById(R.id.picture_viewer_progress_text);

            Log.d(Constants.LOG_TAG,"--- progressBar is not null? = "+(progressBar!=null));

            progressIndicator = new SignalProgressIndicator(progressBar,textView);
            progressIndicator.setMax(100);
            progressIndicator.setThreshold(75); // when ProgressFullEvent will be sent

            itemView.setOnLongClickListener(this);
        }



        @Override
        public boolean onLongClick(View v) {
            onSelectItem(v, this.getAdapterPosition());
            return true;
        }
    }

    // -------------------------  Select menu --------------------
    private void onSelectItem(View v, int layoutPosition) {
        PictureItem pictureItem = pictures.get(layoutPosition);
        // do something with it
        Log.d(Constants.LOG_TAG,"---- pictureItem select! ---"+pictureItem.urlFullImage);
    }

    // -------------------------  Prefetch next and prev pics --------------------
}
