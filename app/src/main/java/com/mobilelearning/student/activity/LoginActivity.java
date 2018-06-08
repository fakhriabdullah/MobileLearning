package com.mobilelearning.student.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.mobilelearning.student.MainActivity;
import com.mobilelearning.student.R;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {
    private int userType;
    @BindView(R.id.btn_register) Button btnRegister;
    @BindView(R.id.btn_login) Button btnLogin;
    @BindView(R.id.et_username) EditText etUsername;
    @BindView(R.id.et_password) EditText etPassword;

    private String  username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userType = getIntent().getIntExtra("userType",0);
//        setLayout();
        setClick();
    }

//    private void setLayout() {
//        btnRegister=(Button)findViewById(R.id.btn_register);
//    }

    private void setClick() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
//                intent.putExtra("userType",userType);
//                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validation())
                {
                    login();
                }
            }
        });
    }

    private boolean validation() {
        boolean sukses = true;
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        if(username.length()==0 || username.matches(""))
        {
            etUsername.setError(getString(R.string.harus_diisi));
            sukses=false;
        }
        if(password.length()==0 || password.matches(""))
        {
            etPassword.setError(getString(R.string.harus_diisi));
            sukses=false;
        }
        return sukses;
    }
    private void login(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
//        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Website web=new Website();

        String url="";
        if(userType==1)
        {
            url=web.getDomain()+"/auth/loginDosen?hash="+web.getHash();
        }else{
            url=web.getDomain()+"/auth/loginGuru?hash="+web.getHash();
        }
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
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
                                pDialog.dismiss();
                                JSONObject data = jsonObject.getJSONObject("data");
                                Log.d("test", "onResponse: "+data);

                                User user = new User();
                                if(userType==1) {
                                    user.setUserId(data.getInt("id_dosen"));
                                    user.setFullName(data.getString("nama_dosen"));
                                    user.setUserType(1);
                                }else{
                                    user.setUserId(data.getInt("id_guru"));
                                    user.setFullName(data.getString("nama_guru"));
                                    user.setUserType(2);
                                }
                                user.setEmail(data.getString("email"));
                                user.setUsername(data.getString("username"));

                                DBUser db = new DBUser(LoginActivity.this);
                                db.save(user);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
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
                params.put("username",username);
                params.put("password",password);
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
}
