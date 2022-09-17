package com.zinyoflamp.totmain2.QnaBbs;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zinyoflamp.totmain2.R;
import com.zinyoflamp.totmain2.UTIL.TrapDTO;

import java.util.ArrayList;


public class QnaListAdapter extends BaseAdapter {

    Context ctx;
    int layout;
    ArrayList<QNADTO> list;
    LayoutInflater inf;

    public QnaListAdapter(Context ctx, int layout, ArrayList<QNADTO> list){
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
        TextView qnanum=(TextView)view.findViewById(R.id.qnanum);
        TextView qnatitle=(TextView)view.findViewById(R.id.qnatitle);
        TextView nickname=(TextView)view.findViewById(R.id.nickname);


        QNADTO qto=list.get(i);
        qnanum.setTextColor(Color.BLACK);
        qnanum.setText(qto.getQnanum()+"");
        qnatitle.setTextColor(Color.BLACK);
        qnatitle.setText(qto.getTitle());
        nickname.setTextColor(Color.BLACK);
        nickname.setText(qto.getTrappernickname());


        return view;
    }
}