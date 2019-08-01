package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.model.anuncio;
import com.joandeson.ufpbalerta.utilMe.analytic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ads extends AppCompatActivity {

    private String url;

    private ImageView ads_image;
    private Button ads_fechar;

    private RequestOptions options;

    private boolean isHideAds = false;
    private boolean isAdsClicked = false;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_ads);

        getSupportActionBar().hide();

        ads_image  = (ImageView)findViewById(R.id.ads_image);
        ads_fechar = (Button)findViewById(R.id.ads_fechar);

        util e  = new util();
        url     = e.getUrl()+"/ads/";

        SharedPreferences sp = getSharedPreferences("setting",MODE_PRIVATE);
        String local = sp.getString("localization","");

        if(local != ""){
            url += "?local="+local;
        }

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                fecharAnuncio();
            }
        }, 5000);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                ads_fechar.setText(String.valueOf(millisUntilFinished / 1000));
                ads_fechar.setTextSize(30);
            }

            @Override
            public void onFinish() {
                ads_fechar.setText("FECHAR O ANÚNCIO AGORA");
                ads_fechar.setTextSize(15);
            }
        }.start();

        ads_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isHideAds){
                    finish();
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Ads")
                            .setAction("Clicou em fechar anúncio").build()
                    );
                }else{
                    Toast.makeText(getApplicationContext(),"Aguarde 5 segundos.",Toast.LENGTH_LONG).show();
                }
            }
        });

        initAds(url);

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        analytic ana = new analytic("Ads",this);
        ana.registroTela();
    }

    private void initAds(String url) {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject  response) {

                        try {

                            anuncio info = new anuncio();

                            info.setNome(response.getString("nome"));
                            info.setLink(response.getString("link"));
                            info.setImagem(response.getString("imagem"));
                            info.setId_hash(response.getString("id_hash"));
                            info.setClick(Integer.parseInt(response.getString("click")));
                            info.setViews(Integer.parseInt(response.getString("views")));
                            info.setRegiao(response.getString("regiao"));
                            info.setStep(Integer.parseInt(response.getString("step")));
                            info.setId(Integer.parseInt(response.getString("id")));

                            generateAds(info);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Impossível exibir anúncio.", Toast.LENGTH_LONG).show();
                finish();
                Log.d("ufpb_connection_erro",error.toString());
                //finalizadoSplash();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void fecharAnuncio(){
        isHideAds = true;

    }

    private void generateAds(final anuncio info) {

        options = new RequestOptions()
                .override(1000,800)
                //.fitCenter()
                .placeholder(R.drawable.ads_background)
                .error(R.drawable.ads_background);

        Glide.with(getApplicationContext())
                .load(info.getImagem())
                .apply(options)
                .into(ads_image);


        ads_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAdsClicked) {
                    ClickAds(info);
                    isHideAds = false;
                    ads_fechar.setVisibility(View.GONE);
                    isAdsClicked = true;
                }
            }
        });

    }

    private void ClickAds(final anuncio Clickads){
        util e = new util();
        String url_click_ads = e.getUrl()+"/ads/click/";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_click_ads,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("ufpb_click_ads", response);

                        openWebAds(Clickads.getLink(),Clickads.getNome());

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id", String.valueOf(Clickads.getId()));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);


        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Ads")
                .setAction("Clicou no anúncio").build()
        );

    }

    private void openWebAds(String url,String nome) {
        Intent web_ads = new Intent(this,link.class);
        web_ads.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        web_ads.putExtra("nome",nome);
        web_ads.putExtra("url",url);
        startActivity(web_ads);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(isHideAds) {
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(),"Aguarde 5 segundos.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTracker.setScreenName("Ads");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
