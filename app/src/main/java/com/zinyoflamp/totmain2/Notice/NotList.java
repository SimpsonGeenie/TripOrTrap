package com.zinyoflamp.totmain2.Notice;

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
import com.zinyoflamp.totmain2.QnaBbs.QNADTO;
import com.zinyoflamp.totmain2.QnaBbs.QnaWrite;
import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.AllDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class NotList extends AppCompatActivity{

    ListView notilist;
    NotListAdapter notiadapter;

    String result;
    ServerAndURLReq sau = new ServerAndURLReq();
    AllDTO adto = new AllDTO();
    ArrayList<NotiDTO> list= new ArrayList<>();
    TextView qnanickname, qnatitle;

    Button btnback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbs_notilist);


        btnback=(Button)findViewById(R.id.btnBack);
        qnatitle=(TextView)findViewById(R.id.qnatitle);

        notilist=(ListView)findViewById(R.id.notilist);
        qnanickname=(TextView)findViewById(R.id.qnanickname);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    result=sau.requestconnection(16,adto);

                    JSONObject json = null;
                    Log.i("받아온 json 값 : ",result);

                    json=new JSONObject(result);
                    JSONArray jArr =json.getJSONArray("noti");
                    for(int i=0; i<jArr.length(); i++) {
                        json = jArr.getJSONObject(i);

                        list.add(new NotiDTO(Integer.parseInt(json.getString("notinum")), json.getString("admin"), json.getString("title"), json.getString("notidate")));

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

            notiadapter=new NotListAdapter(getApplicationContext(), R.layout.bbs_notilistrow, list);
            notilist.setAdapter(notiadapter);

        }catch (Exception ex){

        }

        notilist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),NotShow.class);
                intent.putExtra("notinum",list.get(i).getNotinum());
                Log.i("넘기는 값 : ", i+"번");
                startActivity(intent);
            }
        });

    }
}
