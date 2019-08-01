package com.joandeson.ufpbalerta;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.adapter.arquivosAdapter;
import com.joandeson.ufpbalerta.adapter.noticiaAdapter;
import com.joandeson.ufpbalerta.adapter.residenceAdapter;
import com.joandeson.ufpbalerta.database.dbnoticias;
import com.joandeson.ufpbalerta.model.arquivos;
import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.model.noticia;
import com.joandeson.ufpbalerta.model.residence;
import com.joandeson.ufpbalerta.utilMe.analytic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class principal extends AppCompatActivity {

    private List<arquivos> list_arquivos = new ArrayList<>();

    private RecyclerView rv;
    //private RecyclerView rv_noticias;
    private boolean buscador = false;
    private FloatingSearchView searchView;
    //private AppBarLayout base_buscador;
    private ProgressBar bar;

    private List<noticia> noticias = new ArrayList<>();
    private String url_noticias;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String url;
    private String swith = "files";

    private boolean isSearchLobby;

    private Tracker mTracker;

    private BottomNavigationView navigation;

    private FloatingActionButton floating_bt;

    private boolean isSearchHide = true;

    private String json_ip_location = "http://ip-api.com/json/";

    private String url_residence = "";

    private MenuItem Itembuscador;

    private String token;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_files:
                    //rv.setVisibility(View.VISIBLE);
                    //rv_noticias.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    swith = "files";
                    swipeRefreshLayout.setRefreshing(false);
                    getSupportActionBar().setTitle("Arquivos");
                    rv.setVisibility(View.INVISIBLE);
                    initArquivos(list_arquivos,false);
                    floating_bt.setVisibility(View.GONE);
                    if(Itembuscador != null) {
                        Itembuscador.setVisible(true);
                    }
                    return true;
                case R.id.navigation_news:
                    //rv.setVisibility(View.GONE);
                    //rv_noticias.setVisibility(View.VISIBLE);
                    searchView.setVisibility(View.GONE);
                    bar.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.INVISIBLE);
                    Obternoticia(url_noticias);
                    swith = "news";
                    swipeRefreshLayout.setRefreshing(false);
                    getSupportActionBar().setTitle("Notícias");
                    if(searchView.getQuery().length() > 0){
                        searchView.setSearchText("");
                        initArquivos(list_arquivos,false);
                    }
                    floating_bt.setVisibility(View.GONE);
                    if(Itembuscador != null) {
                        Itembuscador.setVisible(false);
                    }
                    return true;
                case R.id.navigation_residencia:
                    //rv.setVisibility(View.GONE);
                    //rv_noticias.setVisibility(View.GONE);
                    searchView.setVisibility(View.GONE);
                    bar.setVisibility(View.GONE);
                    swith = "z_residence";
                    swipeRefreshLayout.setRefreshing(false);
                    getSupportActionBar().setTitle("Anúncios");
                    rv.setVisibility(View.INVISIBLE);
                    GetJsonResidence(url_residence);
                    if(searchView.getQuery().length() > 0){
                        searchView.setSearchText("");
                        initArquivos(list_arquivos,false);
                    }
                    floating_bt.setVisibility(View.VISIBLE);
                    if(Itembuscador != null) {
                        Itembuscador.setVisible(false);
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        getSupportActionBar().setTitle(R.string.app_name);
        final util e = new util();

        showAvaliacao();
        localization();

        SharedPreferences sp = getSharedPreferences("setting",MODE_PRIVATE);
        token = sp.getString("token","666");

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        floating_bt = (FloatingActionButton)findViewById(R.id.floating_add_ap);
        floating_bt.setVisibility(View.GONE);

        bar = (ProgressBar)findViewById(R.id.carregar);
        bar.setVisibility(View.VISIBLE);

        floating_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nova_residencia = new Intent(getApplicationContext(),link.class);
                nova_residencia.putExtra("nome","Registrar Anúncio");
                nova_residencia.putExtra("url",e.getUrl()+"/residence/register/?token="+token);
                nova_residencia.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nova_residencia);
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(swith == "files"){
                    GetJsonArquivos(url);
                }
                if(swith == "news"){
                    Obternoticia(url_noticias);
                }
                if(swith == "z_residence"){
                    GetJsonResidence(url_residence);
                }
            }
        });

        rv              = (RecyclerView)findViewById(R.id.rv);
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        //rv_noticias     = (RecyclerView)findViewById(R.id.rv_noticias);
        //base_buscador   = (AppBarLayout)findViewById(R.id.base_buscador);
        searchView      = (FloatingSearchView)findViewById(R.id.search);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                dbbase db = new dbbase(getApplicationContext());
                List<arquivos> emp = db.search(newQuery);
                if(emp.size() > 0){
                    if (emp.size() >0 && emp!=null){
                        initArquivos(emp,true);
                    }
                }else {
                    rv.setAdapter(null);
                }
            }
        });


        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();

                dbbase db = new dbbase(getApplicationContext());
                if (id == R.id.b_sisu) {

                    List<arquivos> emp = db.search("sisu");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                } else if (id == R.id.b_cem) {

                    List<arquivos> emp = db.search("cem");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }else if (id == R.id.b_reopcao) {

                    List<arquivos> emp = db.search("reopcao");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }else if (id == R.id.b_pstv) {

                    List<arquivos> emp = db.search("pstv");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }else if (id == R.id.b_doc) {

                    List<arquivos> emp = db.search("doc");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }else if (id == R.id.b_ingresso) {

                    List<arquivos> emp = db.search("ingresso");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }else if (id == R.id.b_codesc) {

                    List<arquivos> emp = db.search("codesc-noticias");
                    if(emp.size() > 0){
                        if (emp.size() >0 && emp!=null){
                            initArquivos(emp,true);
                        }
                    }else {
                        Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
                    }

                    return;

                }
            }
        });

        url_noticias = e.getUrl()+"/news/json.php";
        url_residence = e.getUrl()+"/residence/?token="+Uri.parse(token);


        if(isOnline()) {
            url = e.getUrl() + "/json_arquivos/";
            GetJsonArquivos(url);
        }else{
            Toast.makeText(getApplicationContext(),"Você está offline",Toast.LENGTH_LONG).show();
            dbbase db = new dbbase(getApplicationContext());
            List<arquivos> emp = db.getAllArquivos();
            if(emp.size() > 0){
                if (emp.size() >0 && emp!=null){
                    initArquivos(emp,false);
                }
            }else {
                Toast.makeText(getApplicationContext(),"Nenhum resultado",Toast.LENGTH_SHORT).show();
            }
        }

        analytic ana = new analytic("Home",this);
        ana.registroTela();

    }

    void showAvaliacao(){
        SharedPreferences sp = getSharedPreferences("setting",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int access = sp.getInt("access",0) + 1;
        editor.putInt("access",access);
        editor.apply();

        if(access==5){
            LayoutInflater li = getLayoutInflater();
            View view = li.inflate(R.layout.avaliable,null);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("AVALIAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://bit.ly/UFPB-Alerta"));
                    startActivity(i);
                }
            });
            builder.setNegativeButton("depois", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setView(view);
            builder.show();
        }

    }

    private void GetJsonArquivos(String url){

        bar.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
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
                initArquivos(list_arquivos,false);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("dblog_erro",String.valueOf(error.getMessage()));
                Toast.makeText(getApplicationContext(),"Você está offline",Toast.LENGTH_LONG).show();
                dbbase db = new dbbase(getApplicationContext());
                List<arquivos> emp = db.getAllArquivos();
                if(emp.size() > 0){
                    if (emp.size() >0 && emp!=null){
                        initArquivos(emp,false);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Servidor em manutenção. Tente novamente mais tarde!",Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void GetJsonResidence(String url){
        bar.setVisibility(View.VISIBLE);
        final List<residence> jResidence = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject obj = null;

                for (int i=0; i<response.length(); i++){
                    try {
                        obj = response.getJSONObject(i);

                        residence iresidence = new residence();
                        /*iresidence.setTitle(obj.getString("titulo"));
                        iresidence.setDescricao(obj.getString("descicao"));
                        iresidence.setCampus(obj.getString("campus"));
                        iresidence.setData(obj.getString("data"));
                        iresidence.setWhatsapp(obj.getString("whatsapp"));*/

                        iresidence.setTitle(obj.getString("titulo").toString());
                        iresidence.setDescricao(obj.getString("descricao").toString());
                        iresidence.setData(obj.getString("data").toString());
                        iresidence.setCampus(obj.getString("campus").toString());
                        iresidence.setWhatsapp(obj.getString("whatsapp").toString());
                        iresidence.setId(obj.getString("id"));
                        iresidence.setId_hash(obj.getString("id_hash"));
                        iresidence.setPreco(obj.getString("preco"));
                        iresidence.setToken(obj.getString("token"));
                        iresidence.setSite(obj.getString("site"));

                        jResidence.add(iresidence);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //Log.d("hh",jResidence.get(0).getTitle());
                initResidence(jResidence);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"impossível carregar a lista",Toast.LENGTH_LONG).show();
                Log.d("hh",error.getMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void Obternoticia(String url){

        bar.setVisibility(View.VISIBLE);

        if(isOnline() &&  noticias.size() == 0) {
            JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
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
                    initNoticia(noticias);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("dblog_erro", String.valueOf(error.getMessage()));
                    Toast.makeText(getApplicationContext(), "Você está offline", Toast.LENGTH_LONG).show();
                    initDBNoticia();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(request);
        }else{
            if(!isOnline()) {
                Toast.makeText(getApplicationContext(), "Você está offline", Toast.LENGTH_LONG).show();
                initDBNoticia();
            }else{
                initDBNoticia();
            }
        }

    }

    private void initDBNoticia(){
        dbnoticias db = new dbnoticias(this);
        List<noticia> noticias = new ArrayList<>();
        noticias = db.getAllNoticias();
        if(noticias.size() > 0){
            /*rv_noticias.setHasFixedSize(true);
            rv_noticias.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv_noticias.setAdapter(new noticiaAdapter(this,noticias));*/

            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv.setAdapter(new noticiaAdapter(this,noticias));

            bar.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);

            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void initResidence(List<residence> r){
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(new residenceAdapter(this,r));

        //Log.d("jj",String.valueOf(r.get(0).getTitle()));

        bar.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(false);
    }

    private void initNoticia(List<noticia> noticias) {
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
                        Log.d("ufpb_log", "Nova notícia inserida: "+titulo);
                    } else {
                        Log.d("ufpb_log", "Notícia não foi inserida");
                    }
                }
            }

        List<noticia> dbNoticias =  db.getAllNoticias();

        /*rv_noticias.setHasFixedSize(true);
        rv_noticias.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_noticias.setAdapter(new noticiaAdapter(this,dbNoticias));*/

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv.setAdapter(new noticiaAdapter(this,dbNoticias));

        bar.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setRefreshing(false);
    }

    private void initArquivos(List<arquivos> list, boolean isSearch){
        dbbase db = new dbbase(this);
        for(int i =0;i<list.size();i++) {

            String filename = list.get(i).getFilename();
            String name     = list.get(i).getName();
            String title    = list.get(i).getTitle().replace(":8er5:","\"");
            String data     = list.get(i).getData();
            String description = list.get(i).getDescription().replace(":8er5:","\"");
            String filetype = list.get(i).getFiletype();
            String id       = list.get(i).getId();
            String id_hash  = list.get(i).getId_hash();
            String id_conjunto = list.get(i).getId_conjunto();
            String link     = list.get(i).getLink();

            if(db.getData(id.toString()).size() == 0){
                if(db.insertArquivo(filename,name,title,data,description,filetype,id,id_hash,id_conjunto,link)){
                    Log.d("ufpb_log","Novo arquivo inserido");
                }else{
                    Log.d("ufpb_log","Arquivo não foi inserido");
                }
            }
        }



        if(!isSearch) {
            List<arquivos> db_arquivos = db.getAllArquivos();

            for(int i=0;i<db_arquivos.size();i++){
                Log.d("id-files:",db_arquivos.get(i).getId());
            }

            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv.setAdapter(new arquivosAdapter(this, db_arquivos));

            bar.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }else{


            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv.setAdapter(new arquivosAdapter(this, list));

            bar.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);

            isSearchLobby = true;
        }
        /*enviando o token para o servidor*/

        SharedPreferences sp = getSharedPreferences("setting",MODE_PRIVATE);
        String token = sp.getString("token",null);
        String localization = sp.getString("localization",null);

        if(token!=null){
            sendRegistrationToServer(token,localization);
        }

        //Toast.makeText(getApplicationContext(),localization,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {

        /*if(buscador){
            searchView.setVisibility(View.GONE);
            //base_buscador.setVisibility(View.GONE);
            buscador = !buscador;
        }else{
            super.onBackPressed();
        }*/

        if(!searchView.getQuery().equals("") || isSearchLobby){
            searchView.setSearchText("");
            searchView.setVisibility(View.GONE);
            if(swith == "files"){
                dbbase db = new dbbase(getApplicationContext());
                List<arquivos> emp = db.getAllArquivos();
                if(emp.size() > 0){
                    if (emp.size() >0 && emp!=null){
                        initArquivos(emp,false);
                    }
                }
            }
            isSearchLobby=false;
        }else {
            if(swith == "files") {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Vai sair?");
                builder.setMessage("Se realmente desejar sair, clique em SAIR!");
                builder.setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("SAIR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),"Vejo você mais tarde <3",Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.show();
            }else{
                navigation.setSelectedItemId(R.id.navigation_files);
                swith = "files";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        Itembuscador = menu.getItem(1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.act_shared) {
            compartilhar();
            return true;
        }

        if(id == R.id.act_buscar){
            if(swith == "files") {
                if (isSearchHide) {
                    searchView.setVisibility(View.VISIBLE);
                } else {
                    searchView.setVisibility(View.GONE);
                    searchView.setSearchText("");
                }
                isSearchHide = !isSearchHide;
            }
            return true;
        }

        if(id == R.id.act_eventos){
            Toast.makeText(getApplicationContext(),"Função desativada no momento",Toast.LENGTH_LONG).show();
            return true;
        }

        if (id == R.id.act_sobre) {

            Intent sobre = new Intent(this,sobre.class);
            sobre.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(sobre);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Home")
                    .setAction("Clicou em sobre").build()
            );

            return true;
        }

        if(id == R.id.act_favorito){
            Intent favorito = new Intent(this,favoritos.class);
            favorito.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(favorito);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Home")
                    .setAction("Clicou em favoritos").build()
            );

            return true;
        }

        if(id == R.id.colaboradores){

            Intent colaboradores = new Intent(this, com.joandeson.ufpbalerta.colaboradores.class);
            colaboradores.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(colaboradores);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Home")
                    .setAction("Clicou em colaboradores").build()
            );

            return true;
        }


        if(id == R.id.download){
            Intent i = new Intent(this,arquivos_baixado.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);

            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Home")
                    .setAction("Clicou em seus arquivos").build()
            );

            return true;
        }

        if(id == R.id.termos){

            Intent entrarPoliticas = new Intent(this,link.class);
            entrarPoliticas.putExtra("nome","Termos e Privacidade");
            entrarPoliticas.putExtra("url","https://ufpbalerta.br-com.net/termos.html");
            startActivity(entrarPoliticas);

            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void compartilhar() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "Fique atualizado sobre tudo que acontece na UFPB, chamadas do SISU, ofertas de bolsas, eventos, notícias e muito mais.\n" +
                "Aplicativo disponível para Android.\n" +
                "Baixe agora mesmo em \uD83D\uDC47 \n http://bit.ly/UFPB-Alerta";
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "UFPB Alerta");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(intent, "Compartilhe o nosso aplicativo"));

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Home")
                .setAction("Clicou em compartilhar").build()
        );

    }

    private void sendRegistrationToServer(final String token,final String localization) {
        util e = new util();
        String url = e.getUrl()+"/register_token/";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("ufpb_token", response);
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
                params.put("token", token);
                params.put("localization",localization);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(postRequest);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        if(rv!=null) {
            RecyclerView.Adapter adapter = rv.getAdapter();
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {

        //rv.refreshDrawableState();
        mTracker.setScreenName("Home");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        super.onResume();

    }

}
