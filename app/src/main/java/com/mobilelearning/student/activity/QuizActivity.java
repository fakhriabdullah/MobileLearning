package com.mobilelearning.student.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{
    private Activity activity;
    private User user;
    private int kuisId;
    private int totalSoal;
    private int attemptId;
    private int counterSoal=1;

    private HtmlTextView tvSoal;
    private ProgressBar loading;
    private ScrollView svQuiz;
    private HtmlTextView btJawabanA,btJawabanB,btJawabanC,btJawabanD;
    private TextView tvSebelumnya,tvSelanjutnya,tvCounter;
    private ProgressBar pbJawabanA, pbJawabanB, pbJawabanC, pbJawabanD;
    private ImageView ivJawabanA, ivJawabanB, ivJawabanC, ivJawabanD;
    private LinearLayout llJawabanA, llJawabanB, llJawabanC, llJawabanD;

    private String soal;
    private String jawabanA, jawabanB, jawabanC, jawabanD;
    private String jawabanBenar, jawaban="";
    private int soalId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        activity=QuizActivity.this;
        DBUser db = new DBUser(activity);
        user=db.findUser();
        kuisId=getIntent().getIntExtra("kuisId",0);
        totalSoal=getIntent().getIntExtra("totalSoal",0);

        setLayout();
        setClick();
        sendAttempt();
    }

    private void setLayout() {
        loading=(ProgressBar)findViewById(R.id.loading);
        svQuiz=(ScrollView)findViewById(R.id.sv_quiz);
        tvSoal=(HtmlTextView) findViewById(R.id.tv_soal);

        llJawabanA=(LinearLayout)findViewById(R.id.ll_jawabanA);
        llJawabanB=(LinearLayout)findViewById(R.id.ll_jawabanB);
        llJawabanC=(LinearLayout)findViewById(R.id.ll_jawabanC);
        llJawabanD=(LinearLayout)findViewById(R.id.ll_jawabanD);

        btJawabanA=(HtmlTextView)findViewById(R.id.jawabanA);
        btJawabanB=(HtmlTextView)findViewById(R.id.jawabanB);
        btJawabanC=(HtmlTextView)findViewById(R.id.jawabanC);
        btJawabanD=(HtmlTextView)findViewById(R.id.jawabanD);

        pbJawabanA=(ProgressBar)findViewById(R.id.pb_jawabanA);
        pbJawabanB=(ProgressBar)findViewById(R.id.pb_jawabanB);
        pbJawabanC=(ProgressBar)findViewById(R.id.pb_jawabanC);
        pbJawabanD=(ProgressBar)findViewById(R.id.pb_jawabanD);

        ivJawabanA=(ImageView)findViewById(R.id.iv_jawabanA);
        ivJawabanB=(ImageView)findViewById(R.id.iv_jawabanB);
        ivJawabanC=(ImageView)findViewById(R.id.iv_jawabanC);
        ivJawabanD=(ImageView)findViewById(R.id.iv_jawabanD);

        tvSelanjutnya=(TextView)findViewById(R.id.tv_selanjutnya);
        tvSebelumnya=(TextView)findViewById(R.id.tv_sebelumnya);
        tvCounter=(TextView)findViewById(R.id.tv_counter);
    }

    private void setClick()
    {
        llJawabanA.setOnClickListener(this);
        llJawabanB.setOnClickListener(this);
        llJawabanC.setOnClickListener(this);
        llJawabanD.setOnClickListener(this);

        btJawabanA.setOnClickListener(this);
        btJawabanB.setOnClickListener(this);
        btJawabanC.setOnClickListener(this);
        btJawabanD.setOnClickListener(this);

        tvSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterSoal++;
                if(counterSoal>totalSoal){
                    //pergi ke finish kuis
//                    Intent intent = new Intent(activity, QuizFinish.class);
//                    startActivity(intent);
                    finish();
                }else{
                    getSoal();
                }
            }
        });

        tvSebelumnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counterSoal--;
                getSoal();
            }
        });
    }

    private void sendAttempt() {
        Website web=new Website();
        String url=web.getDomain()+"/kuis/attempt?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                attemptId=jsonObject.getInt("data");
                                getSoal();
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
                error.printStackTrace();
                getResources().getString(R.string.no_internet);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",user.getUserId()+"");
                params.put("kuis_id",kuisId+"");
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

    private void sendAnswer(final String answer, final ImageView ivJawaban, final ProgressBar pbJawaban) {
        ivJawabanA.setImageResource(R.drawable.ic_uncheck);
        ivJawabanB.setImageResource(R.drawable.ic_uncheck);
        ivJawabanC.setImageResource(R.drawable.ic_uncheck);
        ivJawabanD.setImageResource(R.drawable.ic_uncheck);

        ivJawaban.setVisibility(View.GONE);
        pbJawaban.setVisibility(View.VISIBLE);
        Website web=new Website();
        String url=web.getDomain()+"/kuis/sendJawaban?hash="+web.getHash();
        Log.d("test", "sendAnswer: "+url);

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                pbJawaban.setVisibility(View.GONE);
                                ivJawaban.setImageResource(R.drawable.ic_checked);
                                ivJawaban.setVisibility(View.VISIBLE);
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
                error.printStackTrace();
                getResources().getString(R.string.no_internet);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                int nilai=0;
                if(answer.equalsIgnoreCase(jawabanBenar)) nilai=1;
                params.put("nilai",nilai+"");
                params.put("jawaban",answer);
                params.put("soal_id",soalId+"");
                params.put("attempt_id",attemptId+"");
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

    private void getSoal() {
        loading.setVisibility(View.VISIBLE);
        svQuiz.setVisibility(View.GONE);

        Website web=new Website();
        String url=web.getDomain()+"/kuis/findSoal?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                JSONObject data=jsonObject.getJSONObject("data");
                                soalId=data.getInt("kuis_soal_id");
                                soal=data.getString("soal");
                                jawabanA=data.getString("jawaban_a");
                                jawabanB=data.getString("jawaban_b");
                                jawabanC=data.getString("jawaban_c");
                                jawabanD=data.getString("jawaban_d");
                                jawabanBenar=data.getString("jawaban_benar");
                                jawaban=data.getString("jawaban");
                                showSoal();
                            }else{
                                showAlert("Soal Tidak Tersedia");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showAlert(getResources().getString(R.string.error_json));
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                getResources().getString(R.string.no_internet);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kuis_id",kuisId+"");
                params.put("urutan",counterSoal+"");
                params.put("attempt_id",attemptId+"");
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

    private void showSoal() {
        if(jawaban.equalsIgnoreCase("a")){
            ivJawabanA.setImageResource(R.drawable.ic_checked);
        }else{
            ivJawabanA.setImageResource(R.drawable.ic_uncheck);
        }
        if(jawaban.equalsIgnoreCase("b")){
            ivJawabanB.setImageResource(R.drawable.ic_checked);
        }else{
            ivJawabanB.setImageResource(R.drawable.ic_uncheck);
        }
        if(jawaban.equalsIgnoreCase("c")){
            ivJawabanC.setImageResource(R.drawable.ic_checked);
        }else{
            ivJawabanC.setImageResource(R.drawable.ic_uncheck);
        }
        if(jawaban.equalsIgnoreCase("d")){
            ivJawabanD.setImageResource(R.drawable.ic_checked);
        }else{
            ivJawabanD.setImageResource(R.drawable.ic_uncheck);
        }
//        ivJawabanB.setImageResource(R.drawable.ic_uncheck);
//        ivJawabanC.setImageResource(R.drawable.ic_uncheck);
//        ivJawabanD.setImageResource(R.drawable.ic_uncheck);
        tvSoal.setHtml(soal, new HtmlHttpImageGetter(tvSoal));
        btJawabanA.setHtml(jawabanA, new HtmlHttpImageGetter(btJawabanA));
        btJawabanB.setHtml(jawabanB, new HtmlHttpImageGetter(btJawabanB));
        btJawabanC.setHtml(jawabanC, new HtmlHttpImageGetter(btJawabanC));
        btJawabanD.setHtml(jawabanD, new HtmlHttpImageGetter(btJawabanD));

        tvCounter.setText(counterSoal+"/"+totalSoal);
        if(counterSoal==1) {
            tvSebelumnya.setText("");
            tvSebelumnya.setVisibility(View.INVISIBLE);
            tvSelanjutnya.setVisibility(View.VISIBLE);
            tvSelanjutnya.setText("Selanjutnya");
        }else if(counterSoal==totalSoal) {
            tvSebelumnya.setVisibility(View.VISIBLE);
            tvSelanjutnya.setVisibility(View.VISIBLE);
            tvSebelumnya.setText("Sebelumnya");
            tvSelanjutnya.setText("Selesai");
        }else{
            tvSebelumnya.setText("Sebelumnya");
            tvSelanjutnya.setText("Selanjutnya");
            tvSebelumnya.setVisibility(View.VISIBLE);
            tvSelanjutnya.setVisibility(View.VISIBLE);

        }

        loading.setVisibility(View.GONE);
        svQuiz.setVisibility(View.VISIBLE);
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
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ll_jawabanA:
            case R.id.jawabanA:
                sendAnswer("a",ivJawabanA,pbJawabanA);
                break;
            case R.id.ll_jawabanB:
            case R.id.jawabanB:
                sendAnswer("b",ivJawabanB,pbJawabanB);
                break;
            case R.id.ll_jawabanC:
            case R.id.jawabanC:
                sendAnswer("c",ivJawabanC,pbJawabanC);
                break;
            case R.id.ll_jawabanD:
            case R.id.jawabanD:
                sendAnswer("d",ivJawabanD,pbJawabanD);
                break;
        }
    }
}
