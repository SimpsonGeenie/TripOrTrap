package com.zinyoflamp.totmain2.QnaBbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zinyoflamp.totmain2.Connect.ServerAndURLReq;
import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class QnaList extends AppCompatActivity{

    ListView qnabbslist;
    QnaListAdapter qnaadapter;

    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    ArrayList<QNADTO> list= new ArrayList<>();
    TextView qnanickname, qnatitle;

    Button btnwrite, btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_qnalist);

        btnwrite = (Button)findViewById(R.id.btnbbswrite);
        btnback=(Button)findViewById(R.id.btnBack);
        qnatitle=(TextView)findViewById(R.id.qnatitle);

        qnabbslist=(ListView)findViewById(R.id.qnabbslist);
        qnanickname=(TextView)findViewById(R.id.qnanickname);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent writebbs = new Intent(getApplicationContext(),QnaWrite.class);
                startActivity(writebbs);
                finish();
            }
        });

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result=sau.requestconnection(11,adto);

                    JSONObject json = null;
                    Log.i("받아온 값 : ",result);

                    json=new JSONObject(result);
                    JSONArray jArr =json.getJSONArray("qna");
                    for(int i=0; i<jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        list.add(new QNADTO(Integer.parseInt(json.getString("qnanum")), json.getString("title"), json.getString("trappernickname")));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();
        try {
            t.join();
            qnaadapter=new QnaListAdapter(getApplicationContext(), R.layout.bbs_qnalistrow, list);
            qnabbslist.setAdapter(qnaadapter);
        }catch (Exception ex){

        }

        qnabbslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),QnaShow.class);
                intent.putExtra("qnanum",list.get(i).getQnanum());
                Log.i("넘기는 값 : ", i+"번");
                startActivity(intent);
            }
        });

    }
}
