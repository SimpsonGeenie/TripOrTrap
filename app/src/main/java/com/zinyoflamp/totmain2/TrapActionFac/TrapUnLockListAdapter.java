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

import java.util.ArrayList;

public class TrapUnLockListAdapter extends BaseAdapter{

    Context ctx;
    int layout;
    ArrayList<TrapGridViewDTO> list;
    LayoutInflater inf;

    public TrapUnLockListAdapter(Context ctx, int layout, ArrayList<TrapGridViewDTO> list){
        this.ctx=ctx;
        this.layout=layout;
        this.list=list;

        inf=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TrapUnLockListAdapter(ArrayList<TrapGridViewDTO> list2){
        this.list=list;
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
        TextView txt1=(TextView)view.findViewById(R.id.traptitle);
        ImageView img1=(ImageView)view.findViewById(R.id.traprowimg);

        TrapGridViewDTO dto=list.get(i);
        img1.setImageBitmap(dto.getImg());
        txt1.setTextColor(Color.BLACK);
        txt1.setText(dto.getTitle());
        return view;
    }
}
