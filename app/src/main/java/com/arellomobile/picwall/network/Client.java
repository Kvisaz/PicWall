package com.arellomobile.picwall.network;

import android.graphics.Color;
import android.util.Log;

import com.arellomobile.picwall.Constants;
import com.arellomobile.picwall.model.PictureItem;
import com.arellomobile.picwall.model.PicturePage;
import com.arellomobile.picwall.network.model.DesktopprPicture;
import com.arellomobile.picwall.network.model.DesktopprResponse;

import java.text.SimpleDateFormat;
import java.util.Locale;

import retrofit2.Response;

/**
 *   Client functions
 *   get async server
 *
 */
public class Client {
    public static PicturePage getPage(int number){
        PicturePage page = null;

        try{
            Response<DesktopprResponse> response = RetrofitFactory
                    .getApiService()
                    .getPage(number)
                    .execute();

            if(response.isSuccessful()){
                Log.d(Constants.LOG_TAG,"--- CLIENT getPageAsyncCallback response success - ");
                page = getPicturePageFromResponse(response.body());
            }
            else {
                Log.d(Constants.LOG_TAG,"--- CLIENT getPageAsyncCallback response FAULT - ");
            }
        }
        catch (Exception e)
        {
            Log.d(Constants.LOG_TAG,"--- CLIENT getPageAsync Exception - " + e.toString());
        }
        finally {
            return page;
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
