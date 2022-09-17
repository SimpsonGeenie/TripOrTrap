package com.zinyoflamp.totmain2.TripActionFac;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zinyoflamp.totmain2.MainView.MainActivity;
import com.zinyoflamp.totmain2.R;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TripRecommend extends AppCompatActivity {

    GridView gview;
    MyTripViewDialog mld=null;
    DownloadManager dm;
    SharedPreferences pref;
    Button btnback;
    Bitmap mBitmap;
    List<String> wheresname=new ArrayList<>();
    ArrayList<TripInfoDTO> ttolist = new ArrayList<TripInfoDTO>();
    List<Bitmap> wheresbmap=new ArrayList<>();
    List<String> wherescon=new ArrayList<>();
    TextView textid;

    TripinfoListAdapter tadapter;
    String result;
    int num=1;
    String url;

    String mytrap="http://localhost:8889/TripOrTrap/traplist.do";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_recommendlist);
        btnback=(Button)findViewById(R.id.btnBack);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        url="http://korean.visitkorea.or.kr/kor/bz15/tp/content/list.jsp?func_name=list&orderType=R&pageNum="+num+"&local1=&local2=&localGroup=&category=T";
        textid=(TextView)findViewById(R.id.textid);

        textid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                num++;
                if(num>168){
                    num=1;
                }
                url="http://korean.visitkorea.or.kr/kor/bz15/tp/content/list.jsp?func_name=list&orderType=R&pageNum="+num+"&local1=&local2=&localGroup=&category=T";
                Log.i("url : ", url);
                makeviews(url);
            }
        });


        gview=(GridView)findViewById(R.id.tripinfolist);
        makeviews(url);

        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mld=new MyTripViewDialog(TripRecommend.this, ttolist.get(position).getImg(),ttolist.get(position).getTitle());
                mld.show();
            }
        });



    }

    public void makeviews(final String urlis){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                gethtml(urlis);
            }
        });
        t.start();
        try {
            t.join();
            tadapter=new TripinfoListAdapter(getApplicationContext(), R.layout.trip_triprecommendlistrow, ttolist);
            gview.setAdapter(tadapter);
            t.interrupt();
        }catch (Exception ex){

        }

    }


    public void gethtml(String url){
        String imgaddr = null;
        String wherecon=null;
        String wherename=null;
        try {
            Document document = Jsoup.connect(url).get();
            Document doc = Jsoup.parse(url);
            //Elements titles = doc.select("ul.local_list li span.type01");

            Elements imgs = document.select("ul[class=local_list]");

            Elements img2=imgs.select("li");
            Elements img3=img2.select("a");
            Elements img4=img3.select("figure");
            Elements img5=img4.select("img");

            Elements div=img3.select("div");
            Elements where=div.select("strong");


            for(Element src1 : where) {
                wherename=removeTag(src1.toString());
                wheresname.add(wherename);
            }

            for(Element src : img5) {
                imgaddr=src.attr("abs:src");
                wherecon=trim(src.attr("alt"), 100);
                dm=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                pref= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                mBitmap = BitmapFactory.decodeStream((InputStream) new URL(imgaddr).getContent());
                wheresbmap.add(mBitmap);
                wherescon.add(wherecon);

            }
            for(int i=0;i<16;i++){
                ttolist.add(new TripInfoDTO(wheresbmap.get(i), wheresname.get(i), wherescon.get(i)));
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }

    public String removeTag(String tag) throws Exception {
        return tag.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
    }

    class MyTripViewDialog extends Dialog {

        String mTitle;
        TextView addresstext;
        ImageView mytrapimg;
        Bitmap img;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.trap_mytrapshow);
            addresstext = (TextView) findViewById(R.id.address);
            mytrapimg = (ImageView) findViewById(R.id.mytrapimg);

            setTitle(mTitle);
            setContent(img);


            mytrapimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });

            mytrapimg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent intent=new Intent(getApplicationContext(), TripRecommendMapView.class);
                    intent.putExtra("mTitle", mTitle);
                    startActivity(intent);
                    mld.dismiss();
                    finish();
                    return false;
                }
            });

        }

        private void setTitle(String Address){
            addresstext.setText(Address);
        }

        private void setContent(Bitmap img){
            mytrapimg.setImageBitmap(img);
        }



        public MyTripViewDialog(Context context, Bitmap img, String mTitle) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.mTitle=mTitle;
            this.img=img;


        }

    }

}
