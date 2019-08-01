package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.database.dbnoticias;
import com.joandeson.ufpbalerta.model.arquivos;
import com.joandeson.ufpbalerta.model.noticia;
import com.joandeson.ufpbalerta.utilMe.analytic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class z_default extends AppCompatActivity {

    private boolean isNotify = false;
    private List<arquivos> list_arquivos= new ArrayList<>();
    private List<noticia> noticias = new ArrayList<>();

    private String url_noticias;
    private String url_edital;

    private ImageView alpha;

    private Tracker mTracker;

    private String json_ip_location = "http://ip-api.com/json/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_z_default);

        localization();

        getSupportActionBar().hide();

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();


        util e = new util();
        url_noticias = e.getUrl()+"/news/json.php";
        url_edital   = e.getUrl()+"/json_arquivos/";

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            for (String key : extras.keySet()) {
                if (extras.getString(key) != null) {
                    if (extras.getString("id") != null) {
                        String id = extras.getString("id");
                        edital(id);
                        isNotify = true;
                        break;
                    }
                    if (extras.getString("news") != null) {
                        String id = extras.getString("news");
                        news(id);
                        isNotify = true;
                        break;
                    }
                    if (extras.getString("novo") != null) {
                        String text = extras.getString("novo");
                        String titulo = extras.getString("titulo");
                        novo(text,titulo);
                        isNotify = true;
                        break;
                    }
                }
            }
        }

        Handler handle = new Handler();
        if(!isNotify) {
            handle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finalizadoSplash();
                }
            }, 4500);
        }

        alpha = (ImageView)findViewById(R.id.alpha);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(3000);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(fadeIn);

        alpha.setAnimation(animation);

        analytic ana = new analytic("Default",this);
        ana.registroTela();

    }

    private void novo(String text,String titulo){
        Intent noticia_intent = new Intent(this, novo.class);
        noticia_intent.putExtra("titulo",titulo);
        noticia_intent.putExtra("texto",text);
        noticia_intent.putExtra("notify","verdade");
        noticia_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(noticia_intent);
        finish();
    }

    private void news(final String id) {
        Log.d("ufpb_id",id);
        JsonArrayRequest request = new JsonArrayRequest(url_noticias, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject obj = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        obj = response.getJSONObject(i);

                        noticia list = new noticia();
                        list.setTitulo(obj.getString("titulo").toString());
                        list.setConteudo(obj.getString("conteudo").toString());
                        list.setId_hash(obj.getString("id_hash").toString());
                        list.setData(obj.getString("data").toString());
                        list.setUrl(obj.getString("url").toString());
                        list.setId(obj.getString("id").toString());

                        noticias.add(list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                startNoticia(noticias, id);
                Log.d("ufpb_id_titulo",noticias.get(0).getTitulo());


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Você está sem internet"+error, Toast.LENGTH_LONG).show();
                Log.d("ufpb_connection_erro",error.toString());
                finalizadoSplash();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void edital(final String id) {
        JsonArrayRequest request = new JsonArrayRequest(url_edital, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject obj = null;

                for (int i=0; i<response.length(); i++){
                    try {
                        obj = response.getJSONObject(i);

                        arquivos list = new arquivos();
                        list.setFilename(obj.getString("filename").toString());
                        list.setName(obj.getString("name").toString());
                        list.setTitle(obj.getString("title").toString());
                        list.setDescription(obj.getString("description").toString());
                        list.setFiletype(obj.getString("filetype").toString());
                        list.setId(obj.getString("id").toString());
                        list.setId_hash(obj.getString("id_hash").toString());
                        list.setId_conjunto(obj.getString("id_conjunto").toString());
                        list.setData(obj.getString("data").toString());
                        list.setLink(obj.getString("link").toString());

                        list_arquivos.add(list);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                startEdital(list_arquivos, id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Você está sem internet"+error,Toast.LENGTH_LONG).show();
                Log.d("ufpb_connection_erro",error.toString());
                finalizadoSplash();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void localization() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                json_ip_location,
                null,
                new Response.Listener<JSONObject >() {
            @Override
            public void onResponse(JSONObject  response) {
                String city = "";
                String country = "";
                String full_localization = "";

                try {
                    city    = response.getString("city").toString();
                    country = response.getString("region").toString();
                    full_localization = city+"-"+country;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(full_localization != "") {
                    SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("localization", full_localization);
                    editor.apply();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"Você está sem internet"+error,Toast.LENGTH_LONG).show();
                Log.d("ufpb_connection_erro",error.toString());
                //finalizadoSplash();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void startNoticia(List<noticia> noticias, String id_y) {
        dbnoticias db = new dbnoticias(this);
        //db.deleteAll();
        for(int i =0;i<noticias.size();i++) {

            String titulo = noticias.get(i).getTitulo();
            String conteudo = noticias.get(i).getConteudo();
            String id_hash = noticias.get(i).getId_hash();
            String data = noticias.get(i).getData();
            String url = noticias.get(i).getUrl();
            String id = noticias.get(i).getId();

            if (db.getData(id.toString()).size() == 0) {
                if (db.insertNoticia(titulo, conteudo, id_hash, data, url, id)) {
                    Log.d("ufpb_log", "Nova notícia inserida");
                } else {
                    Log.d("ufpb_log", "Notícia não foi inserida");
                }
            }
        }

        List<noticia> y = db.getData(id_y);
        if(y.get(0).getTitulo()==null){
            Toast.makeText(getApplicationContext(),"Algo de errado ocorreu! :(",Toast.LENGTH_LONG).show();
            finalizadoSplash();
        }
        if(y != null) {
            Intent noticia_intent = new Intent(this, z_noticia.class);
            noticia_intent.putExtra("id_noticia", id_y);
            noticia_intent.putExtra("notify","verdade");
            noticia_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(noticia_intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Erro na notificação",Toast.LENGTH_LONG).show();
            finalizadoSplash();
        }
    }

    private void startEdital(List<arquivos> list, String id_y) {
        dbbase db = new dbbase(this);
        for(int i =0;i<list.size();i++) {

            String filename = list.get(i).getFilename();
            String name     = list.get(i).getName();
            String title    = list.get(i).getTitle();
            String data     = list.get(i).getData();
            String description = list.get(i).getDescription();
            String filetype = list.get(i).getFiletype();
            String id       = list.get(i).getId();
            String id_hash  = list.get(i).getId_hash();
            String id_conjunto = list.get(i).getId_conjunto();
            String link = list.get(i).getLink();

            if(db.getData(id.toString()).size() == 0){
                if(db.insertArquivo(filename,name,title,data,description,filetype,id,id_hash,id_conjunto,link)){
                    Log.d("ufpb_log","Novo arquivo inserido");
                }else{
                    Log.d("ufpb_log","Arquivo não foi inserido");
                }
            }
        }

        List<arquivos> y = db.getData(id_y);
        if(y != null){
            Intent edital_intent = new Intent(this, arquivo.class);
            edital_intent.putExtra("id_arquivo",id_y);
            edital_intent.putExtra("notify","verdade");
            edital_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(edital_intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Erro na notificação",Toast.LENGTH_LONG).show();
            finalizadoSplash();
        }
    }

    private void finalizadoSplash() {
        Intent intent = new Intent(this,principal.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Default");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
