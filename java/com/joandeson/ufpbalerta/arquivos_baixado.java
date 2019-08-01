package com.joandeson.ufpbalerta;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.model.arquivos;
import com.joandeson.ufpbalerta.utilMe.analytic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class arquivos_baixado extends AppCompatActivity {

    private ListView lista_arquivos;
    private List<String> values = new ArrayList<>();
    private List<arquivos> informations = new ArrayList<>();
    private String path;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_arquivos_baixado);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Meus arquivos");

        listaArquivos();

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

    }

    private void listaArquivos() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.INTERNET},
                    0);

            return;
        }

        dbbase db = new dbbase(this);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ufpb/doc/";
        File directory = new File(path);
        if(directory.exists()) {
            File[] files = directory.listFiles();
            Arrays.sort(files);

            //buscar arquivos intrusos
            for (int i = 0; i < files.length; i++) {
                if (db.getFileInformations(files[i].getName()).getFilename() == null) {
                    files[i].delete();
                }
            }
            //

            if(files.length > 0 && files!=null) {
                for (int i = 0; i < files.length; i++) {
                    /*if(db.getFileInformations(files[i].getName()).getFilename() == null) {
                        files[i].delete();
                    }*/
                    informations.add(db.getFileInformations(files[i].getName()));
                    if (informations.get(i).getTitle() != null) {
                        values.add(informations.get(i).getTitle().replace(":8er5:","\""));
                    }
                    //}
                }

                if (values.size() > 0 && values != null) {
                    lista_arquivos = (ListView) findViewById(R.id.arquivos_lista);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, values.toArray(new String[values.size()]));
                    lista_arquivos.setAdapter(adapter);
                    lista_arquivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (informations.get(position).getTitle() != null) {
                                if (informations.get(position).getFiletype().equals("pdf")) {
                                    String absolutePath = path + informations.get(position).getFilename();
                                    abrirPDF(absolutePath);
                                }
                                if (informations.get(position).getFiletype().equals("doc") || informations.get(position).getFiletype().equals("docx")) {
                                    String absolutePath = path + informations.get(position).getFilename();
                                    abrirDOC(absolutePath);
                                }
                            }
                        }
                    });
                }
            }
        }
    }


    private void abrirPDF(String absolutePath) {
        File file = null;
        file = new File(absolutePath);
        Uri file_path = FileProvider.getUriForFile(
                this,
                "com.joandeson.ufpbalerta.arquivo",
                file);
        if(file.exists()) {
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(file_path, "application/pdf");
            target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, "Abrir PDF");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Arquivo incorreto", Toast.LENGTH_LONG).show();
        }
    }

    private void abrirDOC(String absolutePath){
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/msword";
        File file = null;
        file = new File(absolutePath);
        Uri file_path = FileProvider.getUriForFile(
                this,
                "com.joandeson.ufpbalerta.arquivo",
                file);
        if(file.exists()) {
            intent.setDataAndType(file_path, type);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Arquivo incorreto", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arquivo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();

                return true;

            case R.id.nfav:

                Intent favorito = new Intent(this,favoritos.class);
                favorito.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(favorito);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Downloads")
                        .setAction("Clicou em favoritos").build()
                );

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
           listaArquivos();
        }
    }
}
