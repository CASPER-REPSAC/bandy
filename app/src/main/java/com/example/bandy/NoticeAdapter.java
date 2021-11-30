package com.example.bandy;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    // 커스텀 리스너 인터페이스
    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }


    ArrayList<Notice> items = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView notiName;
        TextView nodeName;
        TextView route1;
        TextView route2;
        TextView time1;
        TextView time2;
        TextView mon;
        TextView tues;
        TextView wed;
        TextView thur;
        TextView fri;
        TextView sat;
        TextView sun;
        Switch isOn;

        public ViewHolder(View itemView) {
            super(itemView);

            notiName = itemView.findViewById(R.id.tvNotiName);
            nodeName = itemView.findViewById(R.id.tvNodeName);
            route1 = itemView.findViewById(R.id.tvRoute1);
            route2 = itemView.findViewById(R.id.tvRoute2);
            time1 = itemView.findViewById(R.id.tvArrTime1);
            time2 = itemView.findViewById(R.id.tvArrTime2);
            isOn = itemView.findViewById(R.id.isOnSwitch);
            mon = itemView.findViewById(R.id.tvMon);
            tues = itemView.findViewById(R.id.tvTues);
            wed = itemView.findViewById(R.id.tvWed);
            thur = itemView.findViewById(R.id.tvThur);
            fri = itemView.findViewById(R.id.tvFri);
            sat = itemView.findViewById(R.id.tvSat);
            sun = itemView.findViewById(R.id.tvSun);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }

        public void setItem(Notice item) {
            notiName.setText(item.getNotiName());
            nodeName.setText(item.getNodeName());
            route1.setText(item.getRouteName(0));
            route2.setText(item.getRouteName(1));
            time1.setText(item.getArrTimes(0));
            time2.setText(item.getArrTimes(1));
            if (item.isOn()) {
                isOn.setChecked(true);
            } else {
                isOn.setChecked(false);
            }

            isOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        item.setIsOn(true);
                    } else {
                        item.setIsOn(false);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.card_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        if (items.size() == 0) {
            clear();
        }
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addItem(Notice item) {
        items.add(item);
    }

    public void setItems(ArrayList<Notice> items) {
        this.items = items;
    }

    public Notice getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, Notice item) {
        items.set(position, item);
    }
}
