package com.mobilelearning.student.konten;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.mobilelearning.student.R;
import com.mobilelearning.student.util.Website;

public class DocumentViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Website web = new Website();
        String doc=web.getMainDomain()+"/documentviewer";
        WebView wv = (WebView) findViewById(R.id.web_view);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.loadUrl(doc);
    }
}
