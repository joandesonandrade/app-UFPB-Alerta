package com.joandeson.ufpbalerta;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.utilMe.analytic;

public class link extends AppCompatActivity {

    private WebView webView;
    private String titulo;
    private String url;

    private ProgressBar link_ads_progress;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_link);

        titulo  = getIntent().getExtras().getString("nome");
        url     = getIntent().getExtras().getString("url");

        if(url==""){
            Toast.makeText(getApplicationContext(),"Impossível abrir o link.",Toast.LENGTH_LONG).show();
            finish();
        }

        getSupportActionBar().setTitle(titulo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        link_ads_progress = (ProgressBar)findViewById(R.id.link_ads_progress);
        webView = (WebView)findViewById(R.id.ads_web);
        webView.setVisibility(View.INVISIBLE);

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
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                link_ads_progress.setVisibility(View.VISIBLE);
                webView.setVisibility(View.INVISIBLE);
                link_ads_progress.setProgress(newProgress);

                super.onProgressChanged(view, newProgress);
            }


        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

                webView.setVisibility(View.VISIBLE);
                link_ads_progress.setVisibility(View.GONE);
                link_ads_progress.setProgress(0);

                getSupportActionBar().setTitle(view.getTitle());

                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {


                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                Uri myurl = Uri.parse(url);

                Log.d("shouldweb",myurl.toString());
                if(myurl != null){
                    //call whatsapp
                    if(myurl.toString().startsWith("whatsapp://")){
                        /*String msg   = myurl.getQueryParameter("text");
                        String phone = myurl.getQueryParameter("phone");
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                        sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER,phone);
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");*/
                       /* try {
                            startActivity(sendIntent);
                        }catch (ActivityNotFoundException e){*/
                        Intent whatsapp = new Intent(Intent.ACTION_VIEW,myurl);
                        whatsapp.setPackage("com.whatsapp");
                        startActivity(whatsapp);
                        //}
                        return;
                    }
                    //call instagram
                    if(myurl.toString().startsWith("https://instagram.com") || myurl.toString().startsWith("https://www.instagram.com")){
                        Intent IG = new Intent(Intent.ACTION_VIEW,myurl);
                        IG.setPackage("com.instagram.android");
                        try{
                            startActivity(IG);
                        }catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW,myurl));
                        }
                        return;
                    }
                    //call mailto
                    if(myurl.toString().startsWith("mailto://")){
                        startActivity(new Intent(Intent.ACTION_VIEW,myurl));
                        return;
                    }
                    //call market
                    if(myurl.toString().startsWith("market://")){
                        startActivity(new Intent(Intent.ACTION_VIEW,myurl));
                        return;
                    }
                    //call close
                    if(myurl.toString().startsWith("close://")){
                        finish();
                        return;
                    }
                }

                link_ads_progress.setVisibility(View.INVISIBLE);

                super.onPageStarted(view, url, favicon);
            }
        });
        webView.loadUrl(url);

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        analytic ana = new analytic("Link: "+titulo,this);
        ana.registroTela();

    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if(webView.canGoBack()){
                    webView.goBack();
                }else {
                    this.finish();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Link: "+titulo);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
