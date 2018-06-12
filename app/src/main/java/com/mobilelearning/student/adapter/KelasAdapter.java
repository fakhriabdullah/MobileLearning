package com.mobilelearning.student.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilelearning.student.R;
import com.mobilelearning.student.activity.KelasActivity;
import com.mobilelearning.student.model.Kelas;

import java.util.List;

/**
 * Created by fakhriabdullah on 16/02/2018.
 */

public class KelasAdapter extends RecyclerView.Adapter<KelasAdapter.MyViewHolder>{
    private Activity activity;
    private List<Kelas> kelasList;

    public KelasAdapter(Activity act, List<Kelas> list) {
        this.activity = act;
        this.kelasList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_kelas, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Kelas k=kelasList.get(position);
        holder.tvKelas.setText(k.getNamaKelas());
        holder.tvGuru.setText(k.getGuru());
        holder.tvDeskripsi.setText(k.getDeskripsi());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, KelasActivity.class);
                intent.putExtra("kelasId",k.getKelasId());
                intent.putExtra("kelasName",k.getNamaKelas());
                activity.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return kelasList.get(position).getKelasId();
    }

    @Override
    public int getItemCount() {
        return kelasList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvKelas, tvGuru, tvDeskripsi;

        public MyViewHolder(View view) {
            super(view);
            tvKelas=(TextView)view.findViewById(R.id.tv_kelas);
            tvGuru=(TextView)view.findViewById(R.id.tv_guru);
            tvDeskripsi=(TextView)view.findViewById(R.id.tv_deskripsi);
        }
    }
}
