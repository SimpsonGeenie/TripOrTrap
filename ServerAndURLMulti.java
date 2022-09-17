package com.zinyoflamp.totmain2.Connect;

import android.util.Log;

import com.zinyoflamp.totmain2.UTIL.AllDTO;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerAndURLMulti {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "^******^";
    String delimiter = lineEnd+twoHyphens+boundary+lineEnd;
    URL url;
    StringBuilder output=new StringBuilder();

    String what;

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

    public String actiontype(int type2){

        String nullstr="";                                           //0 널
        String totlist="TripOrTrap/triportraplist.jsp";         //1 트랩리스트
        String trapadd="TripOrTrap/trapadd.do";                  //2 트래핑
        //String trapadd="TripOrTrap/TOTServer?command=trapadd";
        String trapunlock="TripOrTrap/trapunlock.do";           //3 언락
        String trapunlockcheck="TripOrTrap/istrapunlock.jsp";   //4 언락 체킹

        String totmembercheck="TripOrTrap/istotmemberok.jsp";   //5 아이디 체킹
        String totmemberjoin="TripOrTrap/totmemberjoin.jsp";    //6 회원가입
        String totmemberlogin="TripOrTrap/totmemberlogin.jsp";  //7 로그인
        String findmytrap="TripOrTrap/trap_findmytrap.jsp";           //8 내트랩 찾기

        String urlpath2[]={nullstr, totlist, trapadd, trapunlock, trapunlockcheck, totmembercheck, totmemberjoin, totmemberlogin, findmytrap};
        return urlpath2[type2];
    }

    public String doFileUpload(int type2, AllDTO adto) {

        try {

            url = new URL(urllist(type)+actiontype(type2));
            Log.i("URL Check : ", ""+url );


            StringBuffer postDataBuilder = new StringBuffer();

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection","Keep-Alive");
            conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            postDataBuilder.append(delimiter);


            switch (type2) {
                    case 2:
                        //trapping(adto);
                        postDataBuilder.append(setValue("addre", adto.getAddre()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("latitude", adto.getLatitude()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("longitude", adto.getLongitude()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("trapperAccount", adto.getTrapperaccount()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("xAxis", adto.getxAxis()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("yAxis", adto.getyAxis()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("zAxis", adto.getzAxis()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("heading", adto.getHeading()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("pitch", adto.getPitch()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("roll", adto.getRoll()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("imgwidth", adto.getImgwidth()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("imgheight", adto.getImgheight()+""));
                        postDataBuilder.append(delimiter);
                        break;
                    case 3:
                        //trapunlock(adto);
                        postDataBuilder.append(setValue("addre", adto.getAddre()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("latitude", adto.getLatitude()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("longitude", adto.getLongitude()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("selectedpictureurl", adto.getSelectedpictureurl()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("trappicAccount", adto.getTrappicaccount()+""));
                        postDataBuilder.append(delimiter);
                        postDataBuilder.append(setValue("unlockerAccount", adto.getTrapperaccount()+""));
                        postDataBuilder.append(delimiter);
                        break;
                }

            postDataBuilder.append(setFile("pictureurl", adto.getTrapperaccount()+"strap"+System.currentTimeMillis()+".jpg"));
            postDataBuilder.append(lineEnd);
            Log.i("Check Point : ", "point 6 : "+adto.getPictureurlspath());
            Log.i("Check Point : ", "point 6 : "+adto.getAddre());
            // write data

            FileInputStream in = new FileInputStream(adto.getPictureurlspath());
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                    conn.getOutputStream()));

            // 위에서 작성한 메타데이터를 먼저 전송한다. (한글이 포함되어 있으므로 UTF-8 메소드 사용)
            out.writeUTF(postDataBuilder.toString());
            Log.i("Check Point : ", "스트링 전송 완료");

            // 파일 복사 작업 시작
            int maxBufferSize = 1024;
            int bufferSize = Math.min(in.available(), maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            // 버퍼 크기만큼 파일로부터 바이트 데이터를 읽는다.
            int byteRead = in.read(buffer, 0, bufferSize);

            // 전송
            while (byteRead > 0) {
                out.write(buffer);
                bufferSize = Math.min(in.available(), maxBufferSize);
                byteRead = in.read(buffer, 0, bufferSize);
            }
            Log.i("Check Point : ", "파일 전송 완료");

            out.writeBytes(delimiter); // 반드시 작성해야 한다.
            out.flush();
            out.close();
            in.close();
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
            switch (type2) {
                case 2:
                    what="true";
                    break;

                case 3:
                what=output.toString();
                    break;
            }

        } catch (Exception e) {
            Log.i("Error : ", "exception " + e.getMessage());
            // TODO: handle exception
            what="fail";
        }
        //Log.i(TAG, data.length+"bytes written successed ... finish!!" );
        Log.i("퍼센트 1 : ", what);
        return what;
    }

    public static String setValue(String key, String value) {
        return "Content-Disposition: form-data; name=\""+ key +"\"r\n\r\n"+value;
    }
    public static String setFile(String key, String fileName) {
        return "Content-Disposition: form-data; name=\""+key+"\";filename=\""+fileName+"\"\r\n";
    }

}
