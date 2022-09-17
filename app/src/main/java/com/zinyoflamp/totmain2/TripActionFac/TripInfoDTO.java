package com.zinyoflamp.totmain2.TripActionFac;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017-09-27.
 */

public class TripInfoDTO {

    Bitmap img;
    String title;
    String content;

    public TripInfoDTO(Bitmap img, String title, String content) {
        this.img = img;
        this.title=title;
        this.content = content;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
