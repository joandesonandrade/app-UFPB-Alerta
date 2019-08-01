package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class novo extends AppCompatActivity {

    private String titulo;
    private String texto;
    private TextView tedit;

    private boolean isNotify = false;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo);

        tedit=(TextView)findViewById(R.id._conteudo_texto_novo);

        isNotify = (getIntent().getExtras().getString("notify")!=null);

        getIntent().getExtras().getString("nome");
        titulo = getIntent().getExtras().getString("titulo");
        texto  = getIntent().getExtras().getString("texto");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(titulo);

        tedit.setText(texto);

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Novo: "+titulo);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public void onBackPressed() {
        if(!isNotify) {
            this.finish();
        }else{
            Intent home = new Intent(this, com.joandeson.ufpbalerta.principal.class);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                if(!isNotify) {
                    this.finish();
                }else{
                    Intent home = new Intent(this, com.joandeson.ufpbalerta.principal.class);
                    home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    this.finish();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
