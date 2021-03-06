package com.mobilelearning.student.konten;

import android.os.AsyncTask;
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
import com.mobilelearning.student.adapter.KelasAdapter;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.Kelas;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PdfViewer extends AppCompatActivity {
    private PDFView pdfView;
    private User user;
    private int kontenId;
    private int logId=0;
    private SweetAlertDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLog(logId);
            }
        });
        pdfView=(PDFView)findViewById(R.id.pdfView);

        kontenId=getIntent().getIntExtra("kontenId",0);
        loading=new SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE);
        addLog(logId);
    }

    private void addLog(final int i) {

        loading.setCancelable(false);
        loading.setTitleText("Mohon Tunggu");
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
                                if(i==0)
                                {
                                    logId=jsonObject.getInt("data");
                                    Website web = new Website();
                                    new RetrievePDFStream().execute(web.getMainDomain()+"/files/Manajemen_Proses.pdf");
                                }else{
                                    finish();
                                }
                            }else{
                                loading.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PdfViewer.this, getResources().getString(R.string.error_json) , Toast.LENGTH_LONG).show();
                            loading.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(PdfViewer.this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                DBUser db = new DBUser(PdfViewer.this);
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

    class RetrievePDFStream extends AsyncTask<String,Void,InputStream>
    {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream=null;
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode()==200)
                {
                    inputStream=new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e)
            {
                e.printStackTrace();
                return  null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream)
                    .enableSwipe(true)
                    .enableDoubletap(true)
                    .load();
            loading.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        addLog(logId);
    }
}
