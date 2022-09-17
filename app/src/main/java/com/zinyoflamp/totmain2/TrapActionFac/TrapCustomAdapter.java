package com.zinyoflamp.totmain2.TrapActionFac;

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


public class TrapCustomAdapter extends BaseAdapter {

    Context ctx;
    int layout;
    ArrayList<TrapDTO> list;
    LayoutInflater inf;

    public TrapCustomAdapter(Context ctx, int layout, ArrayList<TrapDTO> list){
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
        TextView txt1=(TextView)view.findViewById(R.id.whattxt);
        ImageView img1=(ImageView)view.findViewById(R.id.myimg);

        TrapDTO dto=list.get(i);
        img1.setImageResource(dto.getTrapimg());
        txt1.setTextColor(Color.BLACK);
        txt1.setText(dto.getTitle());
        return view;
    }
}