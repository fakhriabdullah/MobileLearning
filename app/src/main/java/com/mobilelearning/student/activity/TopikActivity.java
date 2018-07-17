package com.mobilelearning.student.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilelearning.student.MainActivity;
import com.mobilelearning.student.R;
import com.mobilelearning.student.adapter.KontenAdapter;
import com.mobilelearning.student.adapter.TopikAdapter;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.Konten;
import com.mobilelearning.student.model.Topik;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TopikActivity extends AppCompatActivity {
    private Activity activity;
    private int topikId;
    private String topikName;
    private User user;
    private Toolbar toolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout llNoDataRecommended;
    private RecyclerView rvRecommended;
    private LinearLayout llNoDataNoRecommended;
    private RecyclerView rvNoRecommended;

    private List<Konten> kontenListRecommended=new ArrayList<>();
    private List<Konten> kontenListNoRecommended=new ArrayList<>();
    private KontenAdapter adapterRecommended;
    private KontenAdapter adapterNoRecommended;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topik);

        activity=TopikActivity.this;
        DBUser db = new DBUser(activity);
        user=db.findUser();
        topikId=getIntent().getIntExtra("topikId",0);
        topikName=getIntent().getStringExtra("topikName");

        setLayout();
        getData();
        setClick();
    }

    private void setLayout() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle(topikName);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        rvRecommended=(RecyclerView)findViewById(R.id.rv_konten_recommended);
        rvRecommended.setLayoutManager(new GridLayoutManager(activity,2));
        llNoDataRecommended=(LinearLayout)findViewById(R.id.ll_no_data_recommended);
        rvNoRecommended=(RecyclerView)findViewById(R.id.rv_konten_norecommended);
        rvNoRecommended.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        llNoDataNoRecommended=(LinearLayout)findViewById(R.id.ll_no_data_norecommended);
    }

    private void setClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getData() {
        mSwipeRefreshLayout.setRefreshing(true);
        Website web=new Website();
        String url=web.getDomain()+"/konten/findByUser?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            JSONArray jsonRecommended=jsonObject.getJSONArray("recommended");
                            JSONArray jsonNotRecommended=jsonObject.getJSONArray("norecommended");
                            if(jsonRecommended.length()!=0)
                            {
                                for (int i=0;i<jsonRecommended.length();i++)
                                {
                                    JSONObject c = jsonRecommended.getJSONObject(i);
                                    Konten k = new Konten();
                                    k.setKontenId(c.getInt("konten_belajar_id"));
                                    k.setKontenName(c.getString("nama"));
                                    k.setFile(c.getString("file"));
                                    k.setKontenType(c.getString("jenis"));
                                    k.setColor(c.getString("color"));
                                    k.setColorLight(c.getString("color_light"));
                                    kontenListRecommended.add(k);
                                }
                                adapterRecommended=new KontenAdapter(activity,kontenListRecommended);
                                rvRecommended.setAdapter(adapterRecommended);
                                rvRecommended.setHasFixedSize(true);
                                rvRecommended.setVisibility(View.VISIBLE);
                                llNoDataRecommended.setVisibility(View.GONE);
                            }else {
                                rvRecommended.setVisibility(View.GONE);
                                llNoDataRecommended.setVisibility(View.VISIBLE);
                            }

                            if(jsonNotRecommended.length()!=0)
                            {
                                for (int i=0;i<jsonNotRecommended.length();i++)
                                {
                                    JSONObject c = jsonNotRecommended.getJSONObject(i);
                                    Konten k = new Konten();
                                    k.setKontenId(c.getInt("konten_belajar_id"));
                                    k.setKontenName(c.getString("nama"));
                                    k.setFile(c.getString("file"));
                                    k.setKontenType(c.getString("jenis"));
                                    k.setColor(c.getString("color"));
                                    k.setColorLight(c.getString("color_light"));
                                    kontenListNoRecommended.add(k);
                                }
                                adapterNoRecommended=new KontenAdapter(activity,kontenListNoRecommended);
                                rvNoRecommended.setAdapter(adapterNoRecommended);
                                rvNoRecommended.setHasFixedSize(true);
                                rvNoRecommended.setVisibility(View.VISIBLE);
                                llNoDataNoRecommended.setVisibility(View.GONE);
                            }else {
                                rvNoRecommended.setVisibility(View.GONE);
                                llNoDataNoRecommended.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(getResources().getString(R.string.error_json));
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                error.printStackTrace();
                getResources().getString(R.string.no_internet);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user.getUserId()+"");
                params.put("topik_id",topikId+"");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };

        final int DEFAULT_MAX_RETRIES = 1;
        final float DEFAULT_BACKOFF_MULT = 1f;
        jsonObjReq.setRetryPolicy(
                new DefaultRetryPolicy(
                        (int) TimeUnit.SECONDS.toMillis(20),
                        DEFAULT_MAX_RETRIES,
                        DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

    private void showAlert(String pesan)
    {
        new SweetAlertDialog(activity,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(pesan)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();
    }
}
