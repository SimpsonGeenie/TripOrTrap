package com.zinyoflamp.totmain2.QnaBbs;

import java.security.Timestamp;


public class QNADTO {

    int trapperaccount;
    int qnanum;
    String trapperid;
    String trappernickname;
    String title;
    String content;
    String reply;
    Timestamp indate;

    public QNADTO(int qnanum, String title, String trappernickname) {
        this.qnanum = qnanum;
        this.trappernickname = trappernickname;
        this.title = title;
    }

    public int getTrapperaccount() {
        return trapperaccount;
    }

    public void setTrapperaccount(int trapperaccount) {
        this.trapperaccount = trapperaccount;
    }

    public int getQnanum() {
        return qnanum;
    }

    public void setQnanum(int qnanum) {
        this.qnanum = qnanum;
    }

    public String getTrapperid() {
        return trapperid;
    }

    public void setTrapperid(String trapperid) {
        this.trapperid = trapperid;
    }

    public String getTrappernickname() {
        return trappernickname;
    }

    public void setTrappernickname(String trappernickname) {
        this.trappernickname = trappernickname;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Timestamp getIndate() {
        return indate;
    }

    public void setIndate(Timestamp indate) {
        this.indate = indate;
    }
}
