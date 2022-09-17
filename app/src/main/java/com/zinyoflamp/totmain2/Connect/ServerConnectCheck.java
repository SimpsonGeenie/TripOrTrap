package com.zinyoflamp.totmain2.Connect;

import android.util.*;
import java.io.*;
import java.net.*;

import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.UTIL.TrapDTO2;

public class ServerConnectCheck {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "^******^";
    URL url;
    String myResult;
    StringBuilder output=new StringBuilder();
    StringBuffer buffer=new StringBuffer();
    DataOutputStream out;
    int type=4;

    public String urllist(int type){

        String schoolurl="http://192.168.10.31:8889/";
        String homeurl1="http://totserver.mooo.com:8889/";
        String homeurl2="http://192.168.219.147:8889/";
        String picsurl="http://mustory.ivyro.net/tot/";
        String homeurl3="http://totserver.mooo.com:8088/";

        String urlpath[]={schoolurl, homeurl1, homeurl2, picsurl, homeurl3};
        return urlpath[type];
    }

    public String actiontype(){

        String nullstr="TripOrTrap/pageok.jsp";                                           //0 널

        return nullstr;
    }

    public String requestconnection() throws IOException {
        String okornot=null;
        try {
            Log.i("URL Check : ", urllist(type)+actiontype());
            url = new URL(urllist(type)+actiontype());

            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            if(conn!=null){
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("content-type",
                        "application/x-www-form-urlencoded");
                buffer.append("isok").append("=").append("isok");
                OutputStreamWriter ops=new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                PrintWriter writer=new PrintWriter(ops);
                writer.write(buffer.toString());
                writer.flush();

                int resCode=conn.getResponseCode();
                Log.e("request ","rescode"+resCode);
                if(resCode==HttpURLConnection.HTTP_OK){
                    BufferedReader reader=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line=null;
                    while (true){
                        line=reader.readLine();
                        if(line==null){
                            break;
                        }
                        output.append(line+"\n");
                    }
                    reader.close();
                    Log.e("@@@","HttpURLConn OK");
                    Log.i("리턴 값 : ", output.toString());
                }

            }
            conn.disconnect();
            okornot="ok";
        }catch (Exception ex){
            Log.e("SampleHTTP", "Exception in proccessing response", ex);
            ex.printStackTrace();
            okornot="fail";
        }
        return okornot;
    }




}

