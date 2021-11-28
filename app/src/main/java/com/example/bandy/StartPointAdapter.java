package com.example.bandy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bandy.R;

import java.util.List;


public class StartPointAdapter extends RecyclerView.Adapter<StartPointAdapter.ViewHolder> {
    List busNumber, stationName;
    Context context;

    public StartPointAdapter(SettingActivity cxt, List busList, List stationList) {
        context = cxt;
        this.busNumber = busList;
        this.stationName = stationList;
        System.out.println(cxt);
        System.out.println(busList);
        System.out.println(stationList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.start_pointview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bn.setText((String)busNumber.get(position));
        holder.sn.setText((String)stationName.get(position));

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SettingActivity.class);
                i.putExtra("bus_num", (int[]) busNumber.get(holder.getAdapterPosition()));
                i.putExtra("station_name",(int[])stationName.get(holder.getAdapterPosition()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return busNumber.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView bn, sn;
        ConstraintLayout mainLayout;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            bn = itemView.findViewById(R.id.busNumber);
            sn = itemView.findViewById(R.id.stationName);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
    /*
    public StartPointAdapter(Context cxt, String bNum[], String sName[]){
        context = cxt;
        busNumber = bNum;
        stationName = sName;
    }*/

}
