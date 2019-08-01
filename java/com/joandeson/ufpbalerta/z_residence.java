package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class z_residence extends AppCompatActivity {

    private String title;
    private String data;
    private String descricao;
    private String campus;
    private String whatsapp;
    private String id;
    private String id_hash;
    private String site;
    private String token;
    private String preco;

    private RelativeLayout t_residence;
    private TextView t_titulo;
    private TextView t_descricao;
    private TextView t_data;
    private TextView t_campus;
    private TextView t_preco;
    private Button t_instagram;
    private Button t_whatsapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_residence);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(getIntent().getExtras().getString("titulo") == null){
            Toast.makeText(getApplicationContext(),"Houve um problema interno",Toast.LENGTH_LONG).show();
            this.finish();
        }

        title = getIntent().getExtras().getString("titulo","");
        data  = getIntent().getExtras().getString("data","");
        descricao = getIntent().getExtras().getString("descricao","");
        campus = getIntent().getExtras().getString("campus","");
        whatsapp = getIntent().getExtras().getString("whatsapp","");
        id = getIntent().getExtras().getString("id","");
        id_hash = getIntent().getExtras().getString("id_hash","");
        site = getIntent().getExtras().getString("site","");
        preco = getIntent().getExtras().getString("preco","");
        token  = getIntent().getExtras().getString("token","");

        t_titulo = (TextView)findViewById(R.id.titulo_residence);
        t_data = (TextView)findViewById(R.id.data_residence);
        t_descricao = (TextView)findViewById(R.id.descricao_residence);
        t_campus = (TextView)findViewById(R.id.campus_residence);
        t_preco = (TextView)findViewById(R.id.preco_residence);
        t_instagram = (Button)findViewById(R.id.residence_instagram);
        t_whatsapp = (Button)findViewById(R.id.residence_whatsapp);

        if(!preco.equals("")){
            preco = "R$"+preco;
        }

        t_titulo.setText(title);
        t_data.setText(data);
        t_descricao.setText(descricao);
        t_campus.setText(campus);
        t_preco.setText(preco);


        if(site.equals("") || site == null){
            t_instagram.setVisibility(View.GONE);
        }
        if(whatsapp.equals("") || site==null){
            t_whatsapp.setVisibility(View.GONE);
        }

        if(!whatsapp.startsWith("+55") || !whatsapp.startsWith("55")){
            whatsapp = "55"+whatsapp;
        }

        if(!site.startsWith("https://") || !site.startsWith("http://") || !site.startsWith("www.") || !site.startsWith("instagram.com")){
            site = "https://www.instagram.com/"+site.replace("@","");
        }

        t_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWeb(whatsapp,"whatsapp");
            }
        });

        t_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                site = site.replace("https://www.instagram.com/https://www.instagram.com/","https://www.instagram.com/");
                startWeb(site,"instagram");
                Log.d("gaiola",site);
            }
        });

    }

    private void startWeb(String url, String type) {
        if(type == "whatsapp"){
            url = "https://wa.me/"+url;
        }

        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
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
}
