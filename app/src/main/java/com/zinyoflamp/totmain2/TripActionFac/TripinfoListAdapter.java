package com.zinyoflamp.totmain2.TripActionFac;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zinyoflamp.totmain2.Notice.NotiDTO;
import com.zinyoflamp.totmain2.R;

import java.util.ArrayList;


public class TripinfoListAdapter extends BaseAdapter {

    Context ctx;
    int layout;
    ArrayList<TripInfoDTO> list;
    LayoutInflater inf;

    public TripinfoListAdapter(Context ctx, int layout, ArrayList<TripInfoDTO> list){
        this.ctx=ctx;
        this.layout=layout;
        this.list=list;

        inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=inf.inflate(layout,null);
        }
        ImageView triprowimg=(ImageView)view.findViewById(R.id.triprowimg);
        TextView triptitle=(TextView)view.findViewById(R.id.triptitle);
        TextView tripcontent=(TextView)view.findViewById(R.id.tripcontent);


        TripInfoDTO qto=list.get(i);

        triprowimg.setImageBitmap(qto.getImg());
        triptitle.setTextColor(Color.BLACK);
        triptitle.setText(qto.getTitle());
        tripcontent.setTextColor(Color.BLACK);
        tripcontent.setText(qto.getContent());


        return view;
    }
}