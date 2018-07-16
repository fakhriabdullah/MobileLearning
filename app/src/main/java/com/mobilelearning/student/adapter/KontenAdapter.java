package com.mobilelearning.student.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobilelearning.student.R;
import com.mobilelearning.student.activity.TopikActivity;
import com.mobilelearning.student.model.Konten;
import com.mobilelearning.student.model.Topik;

import java.util.List;

/**
 * Created by fakhriabdullah on 16/02/2018.
 */

public class KontenAdapter extends RecyclerView.Adapter<KontenAdapter.MyViewHolder>{
    private Activity activity;
    private List<Konten> kontenList;

    public KontenAdapter(Activity act, List<Konten> list) {
        this.activity = act;
        this.kontenList = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_konten, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Konten k= kontenList.get(position);
        holder.tvKonten.setText(k.getKontenName());
    }

    @Override
    public long getItemId(int position) {
        return kontenList.get(position).getKontenId();
    }

    @Override
    public int getItemCount() {
        return kontenList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvKonten;

        public MyViewHolder(View view) {
            super(view);
            tvKonten=(TextView)view.findViewById(R.id.tv_konten);
        }
    }
}
