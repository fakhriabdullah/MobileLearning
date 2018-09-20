package com.mobilelearning.student.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilelearning.student.R;
import com.mobilelearning.student.adapter.KontenAdapter;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.Konten;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizOverviewActivity extends AppCompatActivity {
    private Activity activity;
    private int topikId;
    private String topikName;
    private User user;
    private Toolbar toolbar;

    private TextView tvNilai,tvTopik;
    private Button btMulai;

    private int kuisId;
    private int totalSoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_overview);

        activity=QuizOverviewActivity.this;
        DBUser db = new DBUser(activity);
        user=db.findUser();
        topikId=getIntent().getIntExtra("topikId",0);
        topikName=getIntent().getStringExtra("topikName");

        setLayout();
        setClick();
    }

    private void setLayout() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle("Kuis");

        tvTopik=(TextView)findViewById(R.id.tv_topik);
        tvTopik.setText(topikName);
        tvNilai=(TextView)findViewById(R.id.tv_nilai);
        btMulai=(Button)findViewById(R.id.btn_mulai);
    }

    private void setClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity,QuizActivity.class);
                i.putExtra("kuisId",kuisId);
                i.putExtra("totalSoal",totalSoal);
                startActivity(i);
            }
        });
    }

    private void getData() {
        final SweetAlertDialog loading = new SweetAlertDialog(activity,SweetAlertDialog.PROGRESS_TYPE);
        loading.setTitleText("Loading");
        loading.setCancelable(false);
        loading.show();

        Website web=new Website();
        String url=web.getDomain()+"/kuis/findKuis?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                JSONObject data = jsonObject.getJSONObject("data");
                                kuisId=data.getInt("kuis_id");
                                totalSoal=data.getInt("total_soal");
                                if(!data.getString("nilai").equalsIgnoreCase("null"))
                                {
                                    tvNilai.setText(data.getString("nilai"));
                                }else{
                                    tvNilai.setText("0");
                                }
                            }else{
                                showAlert("Kuis Tidak Tersedia");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(getResources().getString(R.string.error_json));
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
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

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
