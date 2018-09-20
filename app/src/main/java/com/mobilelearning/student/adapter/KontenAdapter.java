package com.mobilelearning.student.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.mobilelearning.student.R;
import com.mobilelearning.student.activity.KontenActivity;
import com.mobilelearning.student.activity.TopikActivity;
import com.mobilelearning.student.konten.DocumentViewer;
import com.mobilelearning.student.konten.PdfViewer;
import com.mobilelearning.student.konten.VideoPlayer;
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
        holder.llKonten.setBackgroundColor(Color.parseColor(k.getColor()));
        holder.tvBottom.setBackgroundColor(Color.parseColor(k.getColorLight()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity, KontenActivity.class);;
//                if(k.getKontenType().equalsIgnoreCase("pdf"))
//                {
//                    intent= new Intent(activity, PdfViewer.class);
//                }else if(k.getKontenType().equalsIgnoreCase("video"))
//                {
//                    intent= new Intent(activity, VideoPlayer.class);
//                }else if(k.getKontenType().equalsIgnoreCase("web"))
//                {
//                    intent= new Intent(activity, DocumentViewer.class);
//                }
//                if(intent!=null)
//                {
                    intent.putExtra("kontenId",k.getKontenId());
                    intent.putExtra("kontenType",k.getKontenType());
                    intent.putExtra("kontenName",k.getKontenName());
                    intent.putExtra("value",k.getValue());
                    activity.startActivity(intent);
//                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return kontenList.get(position).getKontenId();
    }

    @Override
    public int getItemCount() {
        return kontenList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvKonten, tvBottom;
        LinearLayout llKonten;

        MyViewHolder(View view) {
            super(view);
            tvKonten=(TextView)view.findViewById(R.id.tv_konten);
            tvBottom=(TextView)view.findViewById(R.id.tv_bottom);
            llKonten=(LinearLayout) view.findViewById(R.id.ll_konten);
        }
    }
}
