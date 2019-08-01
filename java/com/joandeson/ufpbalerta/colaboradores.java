package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.joandeson.ufpbalerta.model.colaborador;

import java.util.ArrayList;
import java.util.List;


public class colaboradores extends AppCompatActivity {

    private List<colaborador> list = new ArrayList<>();
    private List<String> values = new ArrayList<>();
    private ListView viewList;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colaboradores);

        viewList = (ListView)findViewById(R.id.cola_list);
        bar      = (ProgressBar)findViewById(R.id.cola_bar);

        getSupportActionBar().setTitle("Colaboradores");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        util e = new util();
        String url = e.getUrl()+"/json_colaboradores/";
        GetJsonColaboradores(url);

    }

    private void GetJsonColaboradores(String url) {
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject obj = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        obj = response.getJSONObject(i);

                        colaborador element = new colaborador();
                        element.setNome(obj.getString("nome"));
                        element.setSobre(obj.getString("sobre"));
                        element.setLink(obj.getString("link"));
                        element.setId(obj.getString("id"));

                        list.add(element);
                        values.add(obj.getString("nome"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                initColaboradores(list);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("dblog_erro", String.valueOf(error.getMessage()));
                Toast.makeText(getApplicationContext(), "Você está offline", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public void initColaboradores(final List<colaborador> a){

        bar.setVisibility(View.GONE);

        if(a.size() > 0 && a != null){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,values.toArray(new String[values.size()]));
            viewList.setAdapter(adapter);
            viewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent u_sobre = new Intent(getApplicationContext(), sobreUser.class);
                    u_sobre.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    u_sobre.putExtra("nome",a.get(position).getNome());
                    u_sobre.putExtra("sobre",a.get(position).getSobre());
                    u_sobre.putExtra("link",a.get(position).getLink());
                    u_sobre.putExtra("id",a.get(position).getId());
                    startActivity(u_sobre);
                }
            });
        }
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
