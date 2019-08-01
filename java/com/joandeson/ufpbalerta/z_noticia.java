package com.joandeson.ufpbalerta;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.database.dbnoticias;
import com.joandeson.ufpbalerta.model.noticia;
import com.joandeson.ufpbalerta.utilMe.analytic;

public class z_noticia extends AppCompatActivity {

    private String titulo;
    private String conteudo;
    private String id_hash;
    private String data;
    private String url;
    private String id;
    private int views;

    private TextView _data;
    private TextView _conteudo;
    private TextView _novo;
    private TextView _title;
    private Button ver_comentario;

    private noticia _noticia;
    private boolean isNotify = false;

    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_noticia);

        getIntent().getExtras().getString("nome");
        id = getIntent().getExtras().getString("id_noticia");
        isNotify = (getIntent().getExtras().getString("notify")!=null);

        dbnoticias db = new dbnoticias(this);

        if(isNotify){
            db.updateViews(id,1);
        }

        if(id != null){
            _noticia = db.getNoticiaInformations(id);
            if (_noticia != null){
                if(_noticia.getTitulo()==null){
                    Toast.makeText(getApplicationContext(),"Algo de estranho ocorreu! :(",Toast.LENGTH_LONG).show();
                    finish();
                }
                titulo      = _noticia.getTitulo().replace(":8er5:","\"");
                conteudo    = _noticia.getConteudo().replace(":8er5:","\"");
                id_hash     = _noticia.getId_hash();
                data        = _noticia.getData();
                url         = _noticia.getUrl();
                id          = _noticia.getId();
                views       = _noticia.getViews();
            }
        }

        if(isNotify){
            db.updateViews(id,1);
        }

        _data       =   findViewById(R.id._data_noticia);
        _conteudo   =   findViewById(R.id._conteudo_noticia);
        _novo       =   findViewById(R.id._novo_noticia);
        _title      =   (TextView)findViewById(R.id.noticia_titulo);

        ver_comentario  = (Button)findViewById(R.id.ver_comentario_noticia);

        if(views > 0){
            _novo.setVisibility(View.INVISIBLE);
        }

        ver_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    Intent comment = new Intent(getApplicationContext(), comment.class);
                    comment.putExtra("id", id_hash);
                    comment.putExtra("type", "noticia");
                    comment.putExtra("titulo", titulo);
                    startActivity(comment);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("News")
                            .setAction("Clicou em comentários").build()
                    );

                }else{
                    Toast.makeText(getApplicationContext(),"Sem conexão :(",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        getSupportActionBar().setTitle(titulo);

        _conteudo.setText("\n\n"+conteudo.replace(":J2e8:","\n")+"\n\n\nAcessado em: "+url);
        _title.setText(titulo);
        _data.setText(data);

        /*if(_data.equals("") || _data==null){
            _novo.setVisibility(View.GONE);
        }else{
            _novo.setVisibility(View.VISIBLE);
        }*/

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        analytic ana = new analytic("Noticia: "+titulo,this);
        ana.registroTela();

        startAds();

    }

    private void startAds(){
        Intent intent_ads = new Intent(this,ads.class);
        intent_ads.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_ads);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.noticia_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(!isNotify) {
            this.finish();
        }else{
            Intent home = new Intent(this, principal.class);
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
            case R.id.visitar:

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Notícia: "+titulo);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
