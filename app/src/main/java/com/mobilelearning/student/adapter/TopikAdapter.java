package com.mobilelearning.student.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilelearning.student.R;
import com.mobilelearning.student.model.Topik;

import java.util.List;

/**
 * Created by fakhriabdullah on 16/02/2018.
 */

public class TopikAdapter extends RecyclerView.Adapter<TopikAdapter.MyViewHolder>{
    private Activity activity;
    private List<Topik> topikList;

    public TopikAdapter(Activity act, List<Topik> list) {
        this.activity = act;
        this.topikList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_topik, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Topik t=topikList.get(position);
        holder.tvTopik.setText(t.getTopikNama());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(activity, KelasActivity.class);
//                intent.putExtra("kelasId",k.getKelasId());
//                intent.putExtra("kelasName",k.getNamaKelas());
//                activity.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return topikList.get(position).getTopikId();
    }

    @Override
    public int getItemCount() {
        return topikList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTopik, tvStatus;

        public MyViewHolder(View view) {
            super(view);
            tvTopik=(TextView)view.findViewById(R.id.tv_topik);
            tvStatus=(TextView)view.findViewById(R.id.tv_status);
        }
    }
}
