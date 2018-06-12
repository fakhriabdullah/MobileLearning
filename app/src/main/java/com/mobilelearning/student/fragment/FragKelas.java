package com.mobilelearning.student.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilelearning.student.MainActivity;
import com.mobilelearning.student.R;
import com.mobilelearning.student.activity.LoginActivity;
import com.mobilelearning.student.adapter.KelasAdapter;
import com.mobilelearning.student.db.DBUser;
import com.mobilelearning.student.model.Kelas;
import com.mobilelearning.student.model.User;
import com.mobilelearning.student.util.Website;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragKelas extends Fragment {
    private View view;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fabTambahKelas;
    private LinearLayout llNoData;
    private RecyclerView rvKelas;
    private KelasAdapter adapter;
    private List<Kelas> kelasList = new ArrayList<>();

    private User user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.frag_kelas, container, false);

        DBUser db = new DBUser(getActivity());
        user=db.findUser();

        setLayout();
        getKelas();
        setClick();
        return view;
    }

    private void setLayout() {
        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);
        fabTambahKelas=(FloatingActionButton)view.findViewById(R.id.fab_tambah_kelas);
        llNoData=(LinearLayout)view.findViewById(R.id.ll_no_data);
        rvKelas=(RecyclerView)view.findViewById(R.id.rv_kelas);
        rvKelas.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void setClick() {
        fabTambahKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(getActivity());
                editText.setHint("Masukan Kode Kelas");
                new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("Masuk Kelas")
                        .setConfirmText("Masuk")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                String kodeKelas=editText.getText().toString().trim();
                                joinKelas(kodeKelas);
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setCustomView(editText)
                        .show();
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getKelas();
            }
        });
    }

    private void getKelas() {
        if(!isAdded())return;
        kelasList.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        llNoData.setVisibility(View.GONE);
        rvKelas.setVisibility(View.GONE);

        Website web=new Website();
        String url=web.getDomain()+"/kelas/findKelas/user_id/"+user.getUserId();

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getBoolean("status"))
                            {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for(int i=0;i<jsonArray.length();i++)
                                {
                                    JSONObject data=jsonArray.getJSONObject(i);
                                    Kelas kelas = new Kelas();
                                    kelas.setKelasId(data.getInt("kelas_id"));
                                    kelas.setUserId(data.getInt("user_id"));
                                    kelas.setNamaKelas(data.getString("nama_kelas"));
                                    kelas.setGuru(data.getString("nama_guru"));
                                    kelas.setDeskripsi(data.getString("deskripsi"));
                                    kelasList.add(kelas);
                                }

                                adapter=new KelasAdapter(getActivity(),kelasList);
                                rvKelas.setAdapter(adapter);
                                rvKelas.setVisibility(View.VISIBLE);
                            }else{
                                llNoData.setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.error_json) , Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }) {
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
//        JsonArrayRequest jsomObjReq=new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray jarray) {
//                        if(!isAdded())return;
//
//                        mSwipeRefreshLayout.setRefreshing(false);
//                        try {
////                            if(jarray.length()==0){
////                                tv_error_product.setVisibility(View.VISIBLE);
////                            }else{
////                                for(int i=0;i<jarray.length();i++)
////                                {
////                                    JSONObject c=jarray.getJSONObject(i);
////                                    Product pr=new Product();
////                                    pr.setProductId(c.getInt("product_id"));
////                                    pr.setStoreName(c.getString("company_name"));
////                                    pr.setStoreId(c.getInt("mitra_id"));
////                                    pr.setProductName(c.getString("product_name"));
////                                    pr.setProductImage(c.getString("product_image"));
////                                    pr.setDescription(c.getString("product_description"));
////                                    pr.setStoreId(c.getInt("user_id"));
////                                    pr.setPriceDirham(c.getDouble("price_dirham"));
////                                    pr.setPriceDinar(c.getDouble("price_dinar"));
////                                    pr.setPrice(c.getInt("product_price"));
////                                    pr.setProductCondition(c.getString("product_condition"));
////                                    pr.setStock(c.getInt("product_stock"));
////                                    pr.setCategoryName(c.getString("category_name"));
////                                    pr.setViewCounter(c.getInt("view_counter"));
////                                    pr.setDiscount(c.getInt("product_discount"));
////                                    listProduct.add(pr);
////                                }
////
////                                int ukuran_gambar=lebar/2;
////                                productAdapter=new ProductAdapter(ProductByMerchantActivity.this,listProduct,ukuran_gambar,false);
////                                gvProduct.setAdapter(productAdapter);
////                                gvProduct.setVisibility(View.VISIBLE);
////                            }
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                            e.getMessage();
//                            Toast.makeText(getActivity(), getResources().getString(R.string.error_json) , Toast.LENGTH_LONG).show();
//                        }
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                },new Response.ErrorListener()
//        {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // hide the progress dialog
//                Log.d("test", "onErrorResponse: "+error.getMessage());
//                mSwipeRefreshLayout.setRefreshing(false);
//                Toast.makeText(getActivity(), getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        RequestQueue queue= Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
//        queue.add(jsomObjReq);
    }

    private void joinKelas(final String kodeKelas) {
        final SweetAlertDialog pDialog=new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        Website web=new Website();
        String url=web.getDomain()+"/kelas/join?hash="+web.getHash();

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
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
                                        .setTitleText("Berhasil")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                                getKelas();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }else{
                                String pesan = jsonObject.getString("data");
                                pDialog
                                        .setTitleText("Error")
                                        .setContentText(pesan)
                                        .setConfirmText("OK")
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
                                    .setConfirmText("OK")
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
                params.put("kode_kelas",kodeKelas);
                params.put("user_id",user.getUserId()+"");
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
}
