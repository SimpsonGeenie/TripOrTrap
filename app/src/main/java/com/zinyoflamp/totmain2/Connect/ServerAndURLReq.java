package com.zinyoflamp.totmain2.Connect;

import android.util.Log;

import com.zinyoflamp.totmain2.UTIL.AllDTO;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ServerAndURLReq {

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "^******^";
    String delimiter = lineEnd+twoHyphens+boundary+lineEnd;
    URL url;
    String myResult;
    FileInputStream in;
    StringBuffer postDataBuilder = new StringBuffer();;
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
        String picsurl2="http://triportrap.dothome.co.kr/tot/";

        String urlpath[]={schoolurl, homeurl1, homeurl2, picsurl, homeurl3,picsurl2};
        return urlpath[type];
    }

    public String actiontype(int type2){

        String nullstr="";                                           //14 널
        String totlist="TripOrTrap/triportraplist.jsp";         //1 트랩리스트
        String trapadd="TripOrTrap/trapadd.do";                  //2 트래핑
        //String trapadd="TripOrTrap/TOTServer?command=trapadd";
        String trapunlock="TripOrTrap/trapunlock.do";           //3 언락
        String trapunlockcheck="TripOrTrap/istrapunlock.jsp";   //4 언락 체킹

        String totmembercheck="TripOrTrap/istotmemberok.jsp";   //5 아이디 체킹
        String totmemberjoin="TripOrTrap/totmemberjoin.jsp";    //6 회원가입
        String totmemberlogin="TripOrTrap/totmemberlogin.jsp";  //7 로그인
        String findmytrap="TripOrTrap/findmytrap.jsp";     //8 내트랩 찾기

        String findneartrap="TripOrTrap/findneartrap.jsp";      //9 근처 트랩 찾기
        String myscore="TripOrTrap/myscore.jsp";                 //10 내 점수

        String bbslist="TripOrTrap/bbslist.jsp";                //11 bbslist
        String bbscontents="TripOrTrap/bbscontents.jsp";       //12 bbs내용
        String bbswrite="TripOrTrap/bbswrite.jsp";             //13 bbs 작성
        String bbsdelete="TripOrTrap/bbsdelete.jsp";           //0 bbs 삭제
        String bbsmodify="TripOrTrap/bbsmodify.jsp";           //15 bbs 수정

        String bbsnoticelist="TripOrTrap/bbsnoticelist.jsp";    //16 notice 리스트
        String bbsnotice="TripOrTrap/bbsnotice.jsp";            //17 notice

        String trapranking="TripOrTrap/trapranking.jsp";            //18 ranking
        String myunlock="TripOrTrap/myunlock.jsp";


        String urlpath2[]={bbsdelete, totlist, trapadd, trapunlock, trapunlockcheck, totmembercheck, totmemberjoin, totmemberlogin,
                findmytrap, findneartrap, myscore, bbslist, bbscontents, bbswrite, nullstr, bbsmodify, bbsnoticelist, bbsnotice, trapranking, myunlock};
        return urlpath2[type2];
    }

    public String requestconnection(int type2, AllDTO adto) throws IOException {
        try {
            Log.i("URL Check : ", urllist(type)+actiontype(type2));
            url = new URL(urllist(type)+actiontype(type2));
            buffer=new StringBuffer();
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            if(conn!=null){
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("content-type",
                        "application/x-www-form-urlencoded");


            switch (type2){
                case 0:
                    buffer.append("qnanumber").append("=").append(adto.getQnanum()+"&").append("&");
                    Log.i("요기 맞지? : ",adto.getQnanum()+"");
                    Log.i("버퍼 : ",buffer.append("qnanumber").append("=").append(adto.getQnanum()+"").toString());
                    break;
                case 1: //1 트랩리스트
                    Log.i("lat",adto.getLatitude().toString());
                    Log.i("log",adto.getLongitude().toString());
                    buffer.append("lat").append("=").append(adto.getLatitude()+"").append("&")
                            .append("lon").append("=").append(adto.getLongitude()+"");
                    break;
                case 2: //2 트래핑

                    break;
                case 3: //3 언락

                    break;
                case 4: //4 언락 체킹

                    break;
                case 5: //5 아이디 체킹
                    Log.i("IDCheck","야호~");
                    buffer.append("trapperid").append("=").append(adto.getTrapperid());

                    break;

                case 6: //6 회원가입
                    Log.i("JOIN",adto.getTrapperid());
                    buffer.append("trapperid").append("=").append(adto.getTrapperid()).append("&")
                            .append("trapperpw").append("=").append(adto.getTrapperpw()).append("&")
                            .append("trappername").append("=").append(adto.getTrappername()).append("&")
                            .append("trappernickname").append("=").append(adto.getTrappernickname()).append("&")
                            .append("trapperphone").append("=").append(adto.getTrapperphone()).append("&");
                    break;
                case 7: //7 로그인
                    buffer.append("trapperid").append("=").append(adto.getTrapperid()).append("&")
                            .append("trapperpw").append("=").append(adto.getTrapperpw());
                    break;
                case 8: //8 내트랩 찾기
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount());
                    break;
                case 9: //9 근처 트랩 찾기
                    Log.i("lat",adto.getLatitude().toString());
                    Log.i("log",adto.getLongitude().toString());
                    buffer.append("lat").append("=").append(adto.getLatitude()+"").append("&")
                            .append("lon").append("=").append(adto.getLongitude()+"");
                    break;
                case 10: //10 내 점수
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount()).append("&");
                    break;
                case 11: //11 bbslist
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount());
                    break;
                case 12: //12 bbs내용
                    buffer.append("qnanumber").append("=").append(adto.getQnanum());
                    break;
                case 13: //13 bbs 작성
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount()).append("&")
                            .append("bbstitle").append("=").append(adto.getBbstitle()).append("&")
                            .append("bbscontent").append("=").append(adto.getBbscontent());
                    Log.i("제목 : ",adto.getBbstitle());
                    Log.i("내용 : ",adto.getBbscontent());
                    break;
                case 14: //14 bbs 삭제
                    buffer.append("qnanumber").append("=").append(adto.getQnanum()+"");
                    Log.i("요기 맞지2? : ",adto.getQnanum()+"");
                    Log.i("버퍼2 : ",buffer.append("qnanumber").append("=").append(adto.getQnanum()).toString());
                    break;
                case 15: //15 bbs 수정
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount()).append("&")
                            .append("bbstitle").append("=").append(adto.getBbstitle()).append("&")
                            .append("bbscontent").append("=").append(adto.getBbscontent()).append("&")
                            .append("qnanumber").append("=").append(adto.getQnanum());
                    break;
                case 16: //16 bbs 수정
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount());
                    break;
                case 17: //17 bbs 수정
                    buffer.append("&").append("notinum").append("=").append(adto.getQnanum()+"&").append("&");
                    break;
                case 18: //18 trap ranking
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount());
                    break;
                case 19: //19 my unlock
                    buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount());
                    break;


            }


            OutputStreamWriter ops=new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer=new PrintWriter(ops);

            writer.write(buffer.toString());
            writer.flush();
                output=new StringBuilder();
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
        }catch (Exception ex){
        Log.e("SampleHTTP", "Exception in proccessing response", ex);
        ex.printStackTrace();
    }
        myResult=output.toString();

        return myResult;
    }








}
