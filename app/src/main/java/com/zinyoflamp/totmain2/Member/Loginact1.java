package com.zinyoflamp.totmain2.Member;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;
import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Loginact1 extends AppCompatActivity {
    LoginSessionHandler loginSessionhandler;
    EditText userid, userpw;
    TextView myaccount, myscore, mytraps;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "^******^";
    String delimiter = lineEnd+twoHyphens+boundary+lineEnd;
    String result;
    AllDTO adto=new AllDTO();
    ServerAndURLReq sau=new ServerAndURLReq();
    ArrayList<String> returnaccount=new ArrayList<>();
    Handler handler=new Handler();
    IDChecker idchecker=new IDChecker();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_login);

        userid=(EditText)findViewById(R.id.edituserid);
        userpw=(EditText)findViewById(R.id.edituserpw);



    }

    public void onButtonClick(View view){

        switch (view.getId()){

            case R.id.loginbtn:
                String useridstr=userid.getText().toString(), userpwstr=userpw.getText().toString();
                adto.setTrapperid(useridstr);
                adto.setTrapperpw(userpwstr);
                idchecker.execute(adto.getTrapperid());
                Log.i("trapperid : ", adto.getTrapperid());
                break;
            case R.id.joinbtn:
                Intent joinIntent=new Intent(getApplicationContext(),Contact.class);
                startActivity(joinIntent);
                break;


        }

    }

    public String requestPost(AllDTO adto) {

        StringBuilder output=new StringBuilder();
        String totmemberlogin="http://192.168.10.31:8889/TripOrTrap/totmemberlogin.jsp";
        //String totmemberlogin="http://192.168.219.147:8889/TripOrTrap/totmemberlogin.jsp";

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


                    buffer.append("trapperid").append("=").append(adto.getTrapperid()).append("&")
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

            //result=requestPost(adto);
            try {
                result=sau.requestconnection(7 ,adto);
            } catch (IOException e) {
                e.printStackTrace();
            }

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
                        Intent intentmyaccount=new Intent(getApplicationContext(),MyAccount.class);
                        intentmyaccount.putExtra("trapperid", adto.getTrapperid());
                        startActivity(intentmyaccount);
                        finish();
                    }else if(findreturnaccountid==0){
                        Toast.makeText(getApplicationContext(),"없는 사용자 아이디 입니다. 회원가입을 해주세요.",Toast.LENGTH_LONG).show();
                        Intent intentjoin=new Intent(getApplicationContext(),JoinForm.class);
                        startActivity(intentjoin);
                        finish();
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
