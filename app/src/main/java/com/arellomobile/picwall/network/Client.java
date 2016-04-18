package com.arellomobile.picwall.network;
import android.graphics.Color;
import android.util.Log;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.events.LogEvent;
import com.arellomobile.picwall.events.ServerPicturePageEvent;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.network.model.DesktopprPicture;
import com.arellomobile.picwall.network.model.DesktopprResponse;
import com.arellomobile.picwall.model.PicturePage;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

/**
 *   Client functions
 *   get async server
 *
 */
public class Client {

    public static void getPageAsync(int number){
        try{
            RetrofitFactory
                    .getApiService()
                    .getPage(number)
                    .enqueue(new getPageAsyncCallback());
        }
        catch (Exception e)
        {
            EventBus.getDefault().post(new LogEvent("getPageAsync Exception - " + e.toString()));
        }
    }

    private static class getPageAsyncCallback implements retrofit2.Callback<DesktopprResponse> {
        @Override
        public void onResponse(Call<DesktopprResponse> call, Response<DesktopprResponse> response) {
            if(response.isSuccessful()){
                EventBus.getDefault().post(new LogEvent("getPageAsyncCallback response success"));


                PicturePage page = getPicturePageFromResponse(response.body());
                EventBus.getDefault().postSticky(new ServerPicturePageEvent(page));
            }
            else {
                EventBus.getDefault().post(new LogEvent("getPageAsyncCallback response fault"));
            }

        }

        @Override
        public void onFailure(Call<DesktopprResponse> call, Throwable t) {
            EventBus.getDefault().post(new LogEvent("getPageAsync onFailure - "+t.toString()));
        }
    }

    /*
        Конвертор из ответа сервера в общую модель - для удобства адаптации к другим серверам
    */
    private static PicturePage getPicturePageFromResponse(DesktopprResponse serverResponse){
        PicturePage picturePage = new PicturePage();
        for(DesktopprPicture desktopprPicture : serverResponse.response){
            picturePage.pictures.add(getPictureItem(desktopprPicture));
        }
        picturePage.numberCurrent = serverResponse.pagination.current;
        picturePage.numberNext = serverResponse.pagination.next;
        picturePage.numberPrev = serverResponse.pagination.previous;
        picturePage.totalPageAmount = serverResponse.pagination.pages;
        picturePage.totalPictureAmount = serverResponse.pagination.count;

        return picturePage;
    }

    private static PictureItem getPictureItem(DesktopprPicture desktopprPicture) {
        PictureItem pictureItem = new PictureItem();
        pictureItem.urlFullImage = desktopprPicture.image.url;
        pictureItem.urlThumbImage = desktopprPicture.image.thumb.url;
        pictureItem.width = desktopprPicture.width;
        pictureItem.height = desktopprPicture.height;

        pictureItem.title =  desktopprPicture.uploader;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, HH:mm", new Locale("ru","RU"));
        pictureItem.desc =  sdf.format(desktopprPicture.created_at);

        pictureItem.bytes =  desktopprPicture.bytes;

        try{
            pictureItem.colorBackground = Color.parseColor("#"+desktopprPicture.palette[0]);
        }
        catch (Exception ex){
            pictureItem.colorBackground=Color.WHITE;
        }

        try{
            pictureItem.colorBackground2 = Color.parseColor("#"+desktopprPicture.palette[1]);
        }
        catch (Exception ex){
            pictureItem.colorBackground2=Color.LTGRAY;
        }

        return pictureItem;
    }

}
