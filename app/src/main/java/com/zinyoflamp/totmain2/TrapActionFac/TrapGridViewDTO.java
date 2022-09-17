package com.zinyoflamp.totmain2.TrapActionFac;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017-07-18.
 */

public class TrapGridViewDTO {

    String title;
    Bitmap img;

    public TrapGridViewDTO(Bitmap img, String title){
        this.title=title;
        this.img=img;
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


}
