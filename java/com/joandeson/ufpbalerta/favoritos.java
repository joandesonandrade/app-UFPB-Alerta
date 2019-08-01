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

import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.model.arquivos;

import java.util.ArrayList;
import java.util.List;

public class favoritos extends AppCompatActivity {

    private ListView arquivos_lista_favoritos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_favoritos);

        arquivos_lista_favoritos = (ListView)findViewById(R.id.arquivos_lista_favoritos);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Favoritos");

        listaArquivos();
    }

    private void listaArquivos() {

        dbbase db = new dbbase(this);
        List<arquivos> arquivos = db.getAllArquivosFavoritos();
        List<String> arq = new ArrayList<>();
        for(int i=0;i<arquivos.size();i++){
            arq.add(arquivos.get(i).getTitle());
        }
        final List<String> arq_id = new ArrayList<>();
        for(int i=0;i<arquivos.size();i++){
            arq_id.add(arquivos.get(i).getId());
        }

        if(arq_id!=null && arq_id.size()>0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, arq.toArray(new String[arq.size()]));
            arquivos_lista_favoritos.setAdapter(adapter);
            arquivos_lista_favoritos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(), arquivo.class);
                    i.putExtra("id_arquivo", arq_id.get(position));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);
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
