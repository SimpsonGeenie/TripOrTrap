package com.zinyoflamp.totmain2.Member;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.*;

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


public class JoinForm extends Activity{
    Context ctx;
    ProgressDialog Pdialog;
    InputMethodManager imm;
    EditText edittrapperid, edittrapperpw, edittrapperpwre, edittrappername, edittrappernickname, edittrapperphone;
    TextView resultview;
    String result;
    AllDTO adto=new AllDTO();
    ServerAndURLReq sau=new ServerAndURLReq();
    LinearLayout linearlayout;

    Cursor cursor;
    StringBuffer sb;
    String isidtrue;
    IDChecker idchecker=new IDChecker();
    MemberDTO mdto=new MemberDTO();
    LoginSessionHandler loginSessionhandler;
    ArrayList<String> trapperaccount = new ArrayList<>();
    ArrayList<String> trapperreturnid = new ArrayList<>();
    ArrayList<String> trapperpw = new ArrayList<>();
    String datacheck=null;


    int idchckbtnpushed=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_joinform);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        edittrapperid=(EditText)findViewById(R.id.edittrapperid);
        edittrapperpw=(EditText)findViewById(R.id.edittrapperpw);
        edittrapperpwre=(EditText)findViewById(R.id.edittrapperpwre);
        edittrappername=(EditText)findViewById(R.id.edittrappername);
        edittrappernickname=(EditText)findViewById(R.id.edittrappernickname);
        edittrapperphone=(EditText)findViewById(R.id.edittrapperphone);
        linearlayout=(LinearLayout)findViewById(R.id.linearlayout);
        resultview=(TextView)findViewById(R.id.resulttext);




    }

    public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.linearlayout :
                hideKeyboard();
                break;
            case R.id.idcheck :
                hideKeyboard();
                String idchecktitle[]={"아이디 입력 오류", "아이디 사용확인"};
                String idchecklist[]={"아이디를 입력해 주세요.", "아이디는 이메일 형식입니다.", "아이디 사용 여부를 확인합니다. 잠시 기다려주세요."};
                isidtrue=edittrapperid.getText().toString();
                adto.setTrapperid(isidtrue);
                if(isidtrue.length()<1){
                    Toast.makeText(getApplicationContext(),idchecktitle[0]+" / "+idchecklist[0],Toast.LENGTH_SHORT).show();
                }else if((!isidtrue.contains("@"))||(!isidtrue.contains("."))){
                    Toast.makeText(getApplicationContext(),idchecktitle[0]+" / "+idchecklist[1],Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),idchecktitle[1]+" / "+idchecklist[2],Toast.LENGTH_SHORT).show();
                    Log.i("to server id : ", isidtrue);
                    //

                    datacheck="idcheck";
                    idchecker.execute("idcheck");

                    idchckbtnpushed++;
                }
                break;

            case R.id.joinstart :
                idchecker.cancel(true);
                if(idchckbtnpushed==0){
                    Toast.makeText(getApplicationContext(),"ID체크를 하지 않았습니다.",Toast.LENGTH_SHORT).show();
                }
                hideKeyboard();
                String Checktitle[]={"비밀번호 입력 오류", "비밀번호 재입력 오류", "이름 입력 오류", "닉네임 입력 오류", "휴대전화 입력 오류"};
                String checklist[]={"비밀번호는 8자 이상입니다.", "비밀번호가 같지 않습니다.", "이름을 정확히 입력해 주세요.", "닉네임을 입력해 주세요.", "휴대전화 번호를 입력해 주세요."};

                if(edittrapperpw.getText().length()<8){
                    Toast.makeText(getApplicationContext(),Checktitle[0]+" / "+checklist[0],Toast.LENGTH_SHORT).show();
                }else if (edittrapperpw.getText()!=edittrapperpw.getText()){
                    Toast.makeText(getApplicationContext(),Checktitle[1]+" / "+checklist[1],Toast.LENGTH_SHORT).show();
                }else if (edittrappername.getText().length()<1){
                    Toast.makeText(getApplicationContext(),Checktitle[2]+" / "+checklist[2],Toast.LENGTH_SHORT).show();
                }else if (edittrappernickname.getText().length()<2){
                    Toast.makeText(getApplicationContext(),Checktitle[3]+" / "+checklist[3],Toast.LENGTH_SHORT).show();
                }else if (edittrapperphone.getText().length()<1) {
                    Toast.makeText(getApplicationContext(),Checktitle[4]+" / "+checklist[4],Toast.LENGTH_SHORT).show();
                } else{
                    idchecker=new IDChecker();
                    adto=new AllDTO();
                    adto.setTrapperid(edittrapperid.getText().toString());
                    adto.setTrapperpw(edittrapperpw.getText().toString());
                    adto.setTrappername(edittrappername.getText().toString());
                    adto.setTrappernickname(edittrappernickname.getText().toString());
                    adto.setTrapperphone(edittrapperphone.getText().toString());
                    Log.i("값 확인", adto.getTrapperid());
                    datacheck="join";
                    idchecker.execute("join");
                }
                break;
        }
    }

    private class IDChecker extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //Pdialog=progressDialog("ID 확인", "네트워크 요청중");
        }


        protected String doInBackground(String... data) {
            String returnstrid=null;
            String returnaccount=null;
            Log.i("datacheck? ",datacheck);
            JSONObject json = new JSONObject();
            JSONArray jArr =new JSONArray();

            try{
                if(datacheck.equals("idcheck")){
                    //result=requestPost("idcheck");
                    result=sau.requestconnection(5 ,adto);
                    Log.i("리턴 값", result);
                }else if(datacheck.equals("join")){
                    result=requestPost("join");
                    //result=sau.requestconnection(6 ,adto);
                    Log.i("리턴 값", result);
                }

                json=null;
                jArr=null;
                json=new JSONObject(result);
                jArr =json.getJSONArray("trap");

                for(int i=0; i<jArr.length(); i++) {
                    json = jArr.getJSONObject(i);
                    if(datacheck.equals("idcheck")){
                        trapperreturnid.add(json.getString("trapperidreturn"));
                        returnstrid=trapperreturnid.get(i);
                    }else if(datacheck.equals("join")){
                        trapperaccount.add(json.getString("trapperaccount"));
                        returnstrid=trapperaccount.get(i);
                        Log.i("제발", trapperaccount.get(i));
                    }

                }

                mdto.setIstrapperconnok(returnstrid);
                if(returnstrid=="0"){
                    returnstrid="fail";
                }


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("Check! : ",returnstrid);
            return returnstrid;
        }

        protected void onPostExecute(String result)
        {
            int failnum=0;

            if(result!=null) {
                if (result.equals("used")) {
                    resultview.setText("이미 사용중인 아이디 입니다. 다른 이메일을 입력해 주세요.");
                } else if (result.equals("unused")) {
                    resultview.setText("ID로 사용 가능합니다. 나머지 정보를 입력해 주세요.");
                } else if(result.equals("fail")){
                    resultview.setText("가입에 문제가 발생하였습니다. 잠시 후 다시 시도해 보세요.");
                    failnum++;
                }else if(result.equals("fail")&&(failnum>5)){
                    resultview.setText("가입에 지속적인 문제가 발생하였습니다. 정확한 내용은 문의를 부탁드립니다.");
                }else {
                    int showresult=Integer.parseInt(result);
                    Log.i("account",showresult+"");
                    resultview.setText("Trip Or Trap 가입을 축하합니다.");
                    loginSessionhandler=LoginSessionHandler.open(getApplicationContext());
                    loginSessionhandler.insert(showresult+"", adto.getTrapperid(), adto.getTrapperpw());
                    sb = new StringBuffer();
                    cursor = loginSessionhandler.select();
                    String loginaccountis=null;
                    if (cursor!=null) {
                        while (cursor.moveToNext()) {
                            loginaccountis = cursor.getString(0);
                        }
                        Intent tomyaccountintent = new Intent(getApplicationContext(), MyAccount.class);
                        tomyaccountintent.putExtra("loginaccountis", loginaccountis);
                        startActivity(tomyaccountintent);
                        finish();
                    }
                }


            }else if(mdto.getIstrapperconnok()==null){
                Toast.makeText(getApplicationContext(),"서버 점검중입니다.",Toast.LENGTH_SHORT).show();
            }


        }
    }

    public String requestPost(String type) {
        Log.i("to server id : ", type);
        StringBuilder output=new StringBuilder();
        String urlis="http://totserver.mooo.com:8088/TripOrTrap/totmemberjoin.jsp";
        //String urlis="http://192.168.10.31:8889/TripOrTrap/totmemberjoin.jsp";
        try{
            Log.i("URL" , urlis);
            URL url=new URL(urlis);

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
                            .append("trapperpw").append("=").append(adto.getTrapperpw()).append("&")
                            .append("trappername").append("=").append(adto.getTrappername()).append("&")
                            .append("trappernickname").append("=").append(adto.getTrappernickname()).append("&")
                            .append("trapperphone").append("=").append(adto.getTrapperphone()).append("&");

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
        Log.i("Check",output.toString());
        return output.toString();
    }


    private void hideKeyboard()
    {
        imm.hideSoftInputFromWindow(edittrapperid.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edittrapperpw.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edittrappername.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edittrappernickname.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edittrapperpwre.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(edittrapperphone.getWindowToken(), 0);

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
