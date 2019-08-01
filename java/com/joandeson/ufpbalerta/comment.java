package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.utilMe.analytic;

public class comment extends AppCompatActivity {

    private WebView webView;
    String tipo = "";
    String id = "";
    String titulo = "";

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_comment);

        tipo = getIntent().getExtras().getString("type");
        id = getIntent().getExtras().getString("id");
        titulo = getIntent().getExtras().getString("titulo");


        webView = (WebView) findViewById(R.id.web_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle("Comentários: " + titulo);

        if (tipo == "" || id == "") {
            Toast.makeText(getApplicationContext(), "Ocorreu um erro na aplicação", Toast.LENGTH_LONG).show();
            finish();
        }

        util u = new util();
        String url = u.getUrl() + "/comment/?type=" + tipo + "&id=" + id+":"+tipo;

        WebSettings webSettings = webView.getSettings();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }else{
            CookieManager.getInstance().setAcceptCookie(true);
        }
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webView.setSelected(false);
        webView.requestFocusFromTouch();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        analytic ana = new analytic("Comentario: "+titulo,this);
        ana.registroTela();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Comentario: "+titulo);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
