package com.joandeson.ufpbalerta;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.utilMe.analytic;

public class sobre extends AppCompatActivity {

    private Button bt_chamar;
    private String url_whatsapp_api;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        getSupportActionBar().setTitle("Sobre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        url_whatsapp_api = "https://www.instagram.com/ufpbalerta/";

        bt_chamar = (Button)findViewById(R.id.chamar);
        bt_chamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url_whatsapp_api));
                startActivity(i);
            }
        });

        analytic ab = new analytic("Sobre", this);
        ab.registroTela();

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();
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

        mTracker.setScreenName("Sobre");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

}
