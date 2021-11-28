package com.example.bandy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    ArrayList<Notice> items = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView notiName;
        TextView nodeName;
        TextView route1;
        TextView route2;
        Switch isOn;

        public ViewHolder(View itemView) {
            super(itemView);

            notiName = itemView.findViewById(R.id.tvNotiName);
            nodeName = itemView.findViewById(R.id.tvNodeName);
            route1 = itemView.findViewById(R.id.tvRoute1);
            route2 = itemView.findViewById(R.id.tvRoute2);
            isOn = itemView.findViewById(R.id.isOnSwitch);
        }

        public void setItem(Notice item) {
            notiName.setText(item.getNotiName());
            nodeName.setText(item.getNodeName());
            if (item.isOn()) {
                isOn.setChecked(true);
            } else {
                isOn.setChecked(false);
            }
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
        return items.size();
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
