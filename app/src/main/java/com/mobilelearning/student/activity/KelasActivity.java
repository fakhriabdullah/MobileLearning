package com.mobilelearning.student.activity;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobilelearning.student.R;
import com.mobilelearning.student.adapter.KelasAdapter;
import com.mobilelearning.student.adapter.TopikAdapter;
import com.mobilelearning.student.model.Kelas;
import com.mobilelearning.student.model.Topik;
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

public class KelasActivity extends AppCompatActivity {
    private Activity activity;
    private int kelasId;
    private String kelasName;
    private Toolbar toolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout llNoData;
    private RecyclerView rvTopik;

    private List<Topik> topikList=new ArrayList<>();
    private TopikAdapter adapter;

//    private ViewPagerAdapter adapter;
//    private TabLayout tabLayout;
//    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        kelasId=getIntent().getIntExtra("kelasId",0);
        kelasName=getIntent().getStringExtra("kelasName");
        activity=KelasActivity.this;

        setLayout();
        getTopik();
        setClick();
    }

    private void setLayout() {
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitle(kelasName);
        mSwipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        llNoData=(LinearLayout)findViewById(R.id.ll_no_data);
        rvTopik=(RecyclerView)findViewById(R.id.rv_topik);
        rvTopik.setLayoutManager(new LinearLayoutManager(activity));

//        viewPager = (ViewPager) findViewById(R.id.viewpager);
//        tabLayout = (TabLayout) findViewById(R.id.tabs);
//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
    }

    private void setClick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getTopik() {
        topikList.clear();
        mSwipeRefreshLayout.setRefreshing(true);
        llNoData.setVisibility(View.GONE);
        rvTopik.setVisibility(View.GONE);

        Website web=new Website();
        String url=web.getDomain()+"/kelas/findTopik/kelas_id/"+kelasId;

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(activity));
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
                                    Topik topik = new Topik();
                                    topik.setTopikId(data.getInt("topik_belajar_id"));
                                    topik.setTopikNama(data.getString("nama"));
                                    topikList.add(topik);
                                }

                                adapter=new TopikAdapter(activity,topikList);
                                rvTopik.setAdapter(adapter);
                                rvTopik.setVisibility(View.VISIBLE);
                            }else{
                                llNoData.setVisibility(View.VISIBLE);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, getResources().getString(R.string.error_json) , Toast.LENGTH_LONG).show();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(activity, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
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
    }
//
//    private void setupViewPager(ViewPager viewPager) {
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new FragTopik(), "Topik");
//        adapter.addFragment(new FragAktivitas(), "Anggota");
//        viewPager.setAdapter(adapter);
//    }
//
//    class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
}
