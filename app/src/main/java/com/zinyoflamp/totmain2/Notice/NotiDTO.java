package com.zinyoflamp.totmain2.Notice;

import java.security.Timestamp;


public class NotiDTO {


    int notinum;
    String admin;
    String title;
    String content;
    String notidate;


    public NotiDTO(int notinum, String admin, String title, String notidate) {
        this.notinum = notinum;
        this.admin = admin;
        this.title = title;
        this.notidate=notidate;
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

    public int getNotinum() {
        return notinum;
    }

    public void setNotinum(int notinum) {
        this.notinum = notinum;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getNotidate() {
        return notidate;
    }

    public void setNotidate(String notidate) {
        this.notidate = notidate;
    }
}
