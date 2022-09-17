package com.zinyoflamp.totmain2.Member;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017-09-21.
 */

public class FixMyForm extends AppCompatActivity {

    EditText userpw, userpwnew, userpwre;

    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "^******^";
    String delimiter = lineEnd+twoHyphens+boundary+lineEnd;
    String result;
    AllDTO adto=new AllDTO();
    ServerAndURLReq sau=new ServerAndURLReq();
    ArrayList<String> returnaccount=new ArrayList<>();
    Handler handler=new Handler();
    LoginSessionHandler loginSessionhandler;
    Cursor cursor;
    StringBuffer sb;
    LoginDTO myinfo=new LoginDTO();
    TextView myaccounttext, myaccountview, myscoreview, mytrapsview;
    IDChecker idchecker=new IDChecker();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_fixmyform);

        userpw=(EditText)findViewById(R.id.edittrapperpwafter);
        userpwnew=(EditText)findViewById(R.id.edittrapperpwnew);
        userpwre=(EditText)findViewById(R.id.edittrapperpwnewre);
        loginSessionhandler=LoginSessionHandler.open(getApplicationContext());
        sb = new StringBuffer();
        cursor = loginSessionhandler.select();

        if (cursor!=null){
            while (cursor.moveToNext()){
                myinfo.setTrapperaccount(cursor.getString(0));
                myinfo.setTrapperid(cursor.getString(1));
                myinfo.setTrapperpw(cursor.getString(2));
            }
            if(myinfo.getTrapperid()!=null){

            }else{
                Toast.makeText(getApplicationContext(),"잘못된 접근입니다.",Toast.LENGTH_LONG).show();
                Intent myaccountintent=new Intent(getApplicationContext(),Loginact1.class);
                startActivity(myaccountintent);
            }
            adto.setTrapperaccount(myinfo.getTrapperaccount());
            adto.setTrapperid(myinfo.getTrapperid());
            adto.setTrapperpw(myinfo.getTrapperpw());
        }


    }
    public void onButtonClick(View view){

        switch (view.getId()){

            case R.id.fixmypw:
                String userpassnew=userpwnew.getText().toString(), userpwstr=userpw.getText().toString(), userpassnewre=userpwre.getText().toString();

                if(!userpassnewre.equals(userpassnew)){
                    Toast.makeText(getApplicationContext(),"변경하실 비밀번호가 서로 다릅니다.",Toast.LENGTH_LONG).show();
                }else if(!adto.getTrapperpw().equals(userpwstr)) {
                    Toast.makeText(getApplicationContext(),"기존 사용중인 비밀번호를 잘못 입력하셨습니다.",Toast.LENGTH_LONG).show();
                }else if((adto.getTrapperpw().equals(userpassnew)&&(adto.getTrapperpw().equals(userpassnewre)))){
                    Toast.makeText(getApplicationContext(),"기존 사용중인 비밀번호와 다르게 입력하셔야 합니다.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"비밀번호를 변경 합니다.",Toast.LENGTH_LONG).show();
                    String tempaccount=adto.getTrapperaccount();
                    adto=new AllDTO();
                    adto.setTrapperaccount(tempaccount);
                    adto.setTrapperpw(userpassnew);

                    idchecker.execute(adto.getTrapperpw());
                }


                break;
        }

    }

    public String requestPost(AllDTO adto) {

        StringBuilder output=new StringBuilder();
        String totmemberlogin="http://192.168.10.31:8889/TripOrTrap/member_fixmyform.jsp";
        //String totmemberlogin="http://192.168.219.147:8889/TripOrTrap/fixmyform.jsp";

        try{
            URL url=new URL(totmemberlogin);

            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            if(conn!=null){
                conn.setConnectTimeout(30000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                //메타정보 세팅함
                conn.setRequestProperty("content-type",
                        "application/x-www-form-urlencoded");

                StringBuffer buffer=new StringBuffer();


                buffer.append("trapperaccount").append("=").append(adto.getTrapperaccount()).append("&")
                        .append("trapperpw").append("=").append(adto.getTrapperpw()).append("&");

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
                    conn.disconnect();
                    Log.e("@@@","HttpURLConn OK");

                }

            }

        }catch (Exception ex){
            Log.e("SampleHTTP", "Exception in proccessing response", ex);
            ex.printStackTrace();
        }
        return output.toString();
    }

    private class IDChecker extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            //Pdialog=progressDialog("ID 확인", "네트워크 요청중");
        }


        protected String doInBackground(String... data) {

            result=requestPost(adto);

//            try {
//                result=sau.requestconnection(0,5,adto);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return result;
        }

        protected void onPostExecute(String result)
        {

            try{

                JSONObject json = new JSONObject();
                Log.i("받아온 값 : ",result);

                json=new JSONObject(result);
                JSONArray jArr =json.getJSONArray("trap");

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    returnaccount.add(json.getString("trapperaccount"));
                }
                String returnaccountid=returnaccount.get(0).toString();

                Log.i("받아온 값 확인 : ",returnaccountid);
                adto.setTrapperaccount(returnaccountid);
                Log.i("Check acc, id, pw", adto.getTrapperaccount()+","+adto.getTrapperid()+","+adto.getTrapperpw());
                try{
                    loginSessionhandler=LoginSessionHandler.open(getApplicationContext());
                    loginSessionhandler.insert(adto.getTrapperaccount()+"", adto.getTrapperid()+"", adto.getTrapperpw()+"");
                }catch (Exception ex){

                }
                if(returnaccountid!=null){
                    int findreturnaccountid=Integer.parseInt(returnaccountid);
                    Log.i("account",findreturnaccountid+"");
                    if(findreturnaccountid>0){
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        intent.putExtra("trapperid", adto.getTrapperid());
                        startActivity(intent);
                    }else if(findreturnaccountid==0){
                        Toast.makeText(getApplicationContext(),"비밀번호 변경에 실패했습니다. 문의 부탁드립니다.",Toast.LENGTH_LONG).show();
//                        Intent intentqna=new Intent(getApplicationContext(),QNA.class);
//                        startActivity(intentqna);
                    }
                }else if (returnaccountid==null){
                    Toast.makeText(getApplicationContext(),"로그인에 실패했습니다. 잠시 후 다시 시도해 주세요.",Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            idchecker.cancel(true);

        }
    }

    @Override
    protected void onDestroy() {

        try
        {
            if (idchecker.getStatus() == AsyncTask.Status.RUNNING)
            {
                idchecker.cancel(true);
            }
            else
            {
            }
        }
        catch (Exception e)
        {
        }

        super.onDestroy();
    }
}
