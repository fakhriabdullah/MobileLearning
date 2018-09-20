package com.mobilelearning.student.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.barteksc.pdfviewer.PDFView;
import com.mobilelearning.student.R;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.fragment.FragUtama;
import com.mobilelearning.student.konten.FragPdfViewer;
import com.mobilelearning.student.konten.FragVideoPlayer;
import com.mobilelearning.student.konten.PdfViewer;
import com.mobilelearning.student.util.Website;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class KontenActivity extends AppCompatActivity {
    private int kontenId;
    private String kontenType;
    private String kontenName;
    private String value;
    private int logId=0;
    private SweetAlertDialog loading;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konten);
        activity=KontenActivity.this;

        kontenId=getIntent().getIntExtra("kontenId",0);
        kontenType=getIntent().getStringExtra("kontenType");
        kontenName=getIntent().getStringExtra("kontenName");
        value=getIntent().getStringExtra("value");
        loading=new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);

        setLayout();
        addLog(logId);
    }

    private void setLayout() {
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle(kontenName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLog(logId);
            }
        });
    }

    private void addLog(final int i) {
        loading.setCancelable(false);
        loading.setTitleText(getResources().getString(R.string.loading));
        loading.show();

        Website web=new Website();
        String url=web.getDomain()+"/log/add?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(this));
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                loading.dismiss();
                                if(i==0)
                                {
                                    logId=jsonObject.getInt("data");
                                    Bundle bundle = new Bundle();
                                    bundle.putString("value", value);

                                    FragmentManager manager=getSupportFragmentManager();
                                    FragmentTransaction transaction=manager.beginTransaction();

                                    if(kontenType.equalsIgnoreCase("pdf"))
                                    {
                                        Fragment frag=new FragPdfViewer();
                                        transaction.replace(R.id.content_frame, frag);
                                        frag.setArguments(bundle);
                                        transaction.commit();
                                    }else if(kontenType.equalsIgnoreCase("video"))
                                    {
                                        Fragment frag=new FragVideoPlayer();
                                        transaction.replace(R.id.content_frame, frag);
                                        frag.setArguments(bundle);
                                        transaction.commit();
                                    }else if(kontenType.equalsIgnoreCase("web"))
                                    {
                                        Fragment frag=new FragPdfViewer();
                                        transaction.replace(R.id.content_frame, frag);
                                        frag.setArguments(bundle);
                                        transaction.commit();
                                    }
                                }else{
                                    finish();
                                }
                            }else{
                                loading.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, getResources().getString(R.string.error_json) , Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(activity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                DBUser db = new DBUser(activity);
                params.put("user_id",db.findUser().getUserId()+"");
                params.put("konten_id",kontenId+"");
                params.put("log_id",i+"");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addLog(logId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loading.isShowing())
            loading.dismiss();
    }
}
