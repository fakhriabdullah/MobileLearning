package com.mobilelearning.student.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilelearning.student.R;
import com.mobilelearning.student.util.Website;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_nama_lengkap) EditText etNama;
    @BindView(R.id.et_telepon) EditText etTelepon;
    @BindView(R.id.et_email) EditText etEmail;
    @BindView(R.id.et_username) EditText etUsername;
    @BindView(R.id.et_password) EditText etPassword;
    @BindView(R.id.et_password_ulang) EditText etPasswordUlang;
    @BindView(R.id.btn_daftar) Button btnDaftar;

    //    private EditText etNip, etNama, etJurusan, etDepartemen, etFakultas, etInstansi;
    //    private EditText etTelepon, etEmail, etUsername, etPassword, etPasswordUlang;

    private String nama;
    private String telepon, email, username, password, passwordUlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setLayout();
        setClick();
    }

    private void setLayout() {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
    }

    private void setClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation())
                {
                    register();
                }
            }
        });
    }

    public void register()
    {
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Website web=new Website();

        String url=web.getDomain()+"/auth/register?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("test", "onResponse: "+response);
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {
                                pDialog
                                    .setTitleText("Sukses")
                                    .setContentText("Registrasi Berhasil")
                                    .setConfirmText("Login")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }else{
                                String pesan = jsonObject.getString("data");
                                pDialog
                                    .setTitleText("Error")
                                    .setContentText(pesan)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog
                                .setTitleText("Error")
                                .setContentText(getResources().getString(R.string.error_json))
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog
                    .setContentText(getResources().getString(R.string.no_internet))
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("full_name",nama);
                params.put("phone",telepon);
                params.put("email",email);
                params.put("username",username);
                params.put("password",password);
                params.put("user_type","2");
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

    private boolean validation() {
        boolean sukses = true;
        nama=getTextFromInput(etNama);
        telepon=getTextFromInput(etTelepon);
        email=getTextFromInput(etEmail);
        username=getTextFromInput(etUsername);
        password=getTextFromInput(etPassword);
        passwordUlang=getTextFromInput(etPasswordUlang);

        if(nama.length()==0 || nama.matches("")){
            inputEmpty(etNama);
            sukses=false;
        }
        if(telepon.length()==0 || telepon.matches(""))
        {
            inputEmpty(etTelepon);
            sukses=false;
        }
        if(email.length()==0 || email.matches(""))
        {
            inputEmpty(etEmail);
            sukses=false;
        }
        if(username.length()<5)
        {
            etUsername.setError("Minimal 5 karakter");
            sukses=false;
        }
        if(username.length()>100)
        {
            etUsername.setError("Maksimal 100 karakter");
            sukses=false;
        }
        if(password.length()==0 || password.matches(""))
        {
            inputEmpty(etPassword);
            sukses=false;
        }

        if(!password.equals(passwordUlang))
        {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setContentText(getResources().getString(R.string.password_tidak_sama))
                    .show();
            sukses=false;
        }
        return sukses;
    }

    protected String getTextFromInput(EditText view) {
        return view.getText().toString().trim();
    }

    protected void inputEmpty(EditText view) {
        view.setError(getResources().getString(R.string.harus_diisi));
    }
}

