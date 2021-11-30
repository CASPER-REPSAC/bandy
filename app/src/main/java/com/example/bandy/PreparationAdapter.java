package com.example.bandy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.bandy.R;

import java.util.ArrayList;
import java.util.Collections;

public class PreparationAdapter extends BaseAdapter {
    private int checkedCount;
    private ArrayList<PreparationItem> list;
    public PreparationAdapter(ArrayList<PreparationItem> i) {
        list = i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public int getCheckedCount(){
        checkedCount = 0;
        for(int i = 0; i < getCount() ; i ++){
            if(((PreparationItem) getItem(i)).isChecked()){
                checkedCount+=1;
            }
        }
        return checkedCount;
    }

    //체크박스 선택이 끝난 리스트의 값 확인용도
    @Override
    public Object getItem(int p) {
        return list.get(p);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int p, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView= inflater.inflate(R.layout.bus_listitem,parent,false);
        }
        TextView busPreparation = convertView.findViewById(R.id.busitem);
        CheckBox checkBox = convertView.findViewById(R.id.busCheck);
        checkBox.setChecked(list.get(p).checked);
        busPreparation.setText(list.get(p).ItemStringname);

        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //체크박스 클릭
                boolean newState = !list.get(p).isChecked();
                list.get(p).checked = newState;

                Log.d("checked",list.get(p).ItemStringid);
                Log.d("checked",list.get(p).ItemStringname);
            }
        });
        checkBox.setChecked(isChecked(p));
        return convertView;
    }
    public boolean isChecked(int p){
        return list.get(p).checked;
    }
}