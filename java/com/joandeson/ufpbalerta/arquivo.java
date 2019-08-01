package com.joandeson.ufpbalerta;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.model.arquivos;
import com.joandeson.ufpbalerta.utilMe.analytic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class arquivo extends AppCompatActivity {

    private String filename         = "";
    private String name             = "";
    private String title            = "";
    private String data             = "";
    private String description      = "";
    private String filetype         = "";
    private String id_hash          = "";
    private String id_conjunto      = "";
    private String id               = "";
    private String link             = "";

    private TextView tdescription;
    private TextView tdata;
    private TextView tnome;
    private TextView tfilename;
    private TextView tnovo;
    private Button ver_comentario;
    private TextView ttitulo;
    private Button favorito;

    private String pathServer = "";
    private long downloadID;

    private int j = 9999;
    private int pp = 0;
    private List<TextView> listTextview = new ArrayList<>();
    private Drawable img;

    private int BitClick = 15000;
    private int views = 0;
    private int favorite = 0;
    private int _favorite = 0;

    private Tracker mTracker;

    String apath = "";

    private boolean isNotify = false;

    private boolean isInterneServer = false;

    private List<String> links_conjuntos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_arquivo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dbbase db = new dbbase(this);

        apath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/ufpb/doc/";

        getIntent().getExtras().getString("nome");
        id = getIntent().getExtras().getString("id_arquivo");
        List<arquivos> lst = db.getData(id);

        isNotify = (getIntent().getExtras().getString("notify")!=null);

        if(lst.size() > 0 && lst!=null){
            if(lst.get(0).getFilename()==null){
                Toast.makeText(getApplicationContext(),"Algo de estranho ocorreu! :(",Toast.LENGTH_LONG).show();
                finish();
            }
            filename    = lst.get(0).getFilename();
            name        = lst.get(0).getName();
            title       = lst.get(0).getTitle();
            data        = lst.get(0).getData();
            description = lst.get(0).getDescription();
            filetype    = lst.get(0).getFiletype();
            id_hash     = lst.get(0).getId_hash();
            id_conjunto = lst.get(0).getId_conjunto();
            views       = lst.get(0).getViews();
            favorite    = lst.get(0).getFavorite();
            id          = lst.get(0).getId();
            link        = lst.get(0).getLink();
        }

        img = getApplicationContext().getResources().getDrawable( R.drawable.ic_abrir_arquivo );
        title = title.replace(":8er5:","\"");
        description = description.replace(":8er5:","\"");

        if(isNotify){
            db.updateViews(id,1);
        }

        getSupportActionBar().setTitle(title);

        tdescription    = (TextView)findViewById(R.id.arquivo_description);
        tdata           = (TextView)findViewById(R.id.arquivo_data);
        tfilename       = (TextView)findViewById(R.id.arquivo_filename);
        tnome           = (TextView)findViewById(R.id.arquivo_nome);
        tnovo           = (TextView)findViewById(R.id.arquivo_novo);
        ver_comentario  = (Button)findViewById(R.id.ver_comentario_arquivo);
        ttitulo         = (TextView)findViewById(R.id.arquivo_titulo);
        favorito        = (Button)findViewById(R.id._favorite);

        if(views > 0){
            tnovo.setVisibility(View.INVISIBLE);
        }

        if(favorite>0){
            favorito.setBackgroundResource(R.drawable.ic_favorite_enabled);
        }else{
            favorito.setBackgroundResource(R.drawable.ic_favorite_disabled);
        }

        _favorite = favorite;

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbbase db = new dbbase(getApplicationContext());
                if(_favorite > 0){
                    favorito.setBackgroundResource(R.drawable.ic_favorite_disabled);
                    _favorite = 0;
                    db.updateFavorite(id,_favorite);
                }else{
                    favorito.setBackgroundResource(R.drawable.ic_favorite_enabled);
                    _favorite = 1;
                    db.updateFavorite(id,_favorite);
                }
            }
        });

        ver_comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()) {
                    Intent comment = new Intent(getApplicationContext(), comment.class);
                    comment.putExtra("id", id_hash);
                    comment.putExtra("type", "arquivo");
                    comment.putExtra("titulo", title);
                    startActivity(comment);

                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("File")
                            .setAction("Clicou em comentários").build()
                    );

                }else{
                    Toast.makeText(getApplicationContext(),"Sem conexão :(",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        ttitulo.setText(title);
        tdescription.setText("\n\n"+description+"\n\n\nFonte: https://www.ufpb.br");
        tdata.setText(data);

        tfilename.setText(filename);

        if(!fileExists(filename)) {
            tfilename.setText(filename);
        }else{
            tfilename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }


        tnome.setText(name.toUpperCase());

        /*if(data.equals("") || data==null){
            tnovo.setVisibility(View.GONE);
        }else{
            tnovo.setVisibility(View.VISIBLE);
        }*/

        /*download manager*/
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long broadcastedDownloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if(broadcastedDownloadID == downloadID){
                    if(getDownloadStatus() == DownloadManager.STATUS_SUCCESSFUL){
                        Toast.makeText(getApplicationContext(),"Arquivo baixado",Toast.LENGTH_LONG).show();
                        Log.d("bitclick",String.valueOf(BitClick));
                        if(BitClick==15000) {
                            tfilename.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                            if (filetype.equals("pdf")) {
                                String absolutePath = apath + filename;
                                abrirPDF(absolutePath);
                            }
                            if (filetype.equals("doc") || filetype.equals("docx")) {
                                String absolutePath = apath + filename;
                                abrirDOC(absolutePath);
                            }
                        }else{
                            listTextview.get(BitClick).setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                            if (filetype.equals("pdf")) {
                                String absolutePath = apath + listTextview.get(BitClick).getText();
                                abrirPDF(absolutePath);
                            }
                            if (filetype.equals("doc") || filetype.equals("docx")) {
                                String absolutePath = apath + listTextview.get(BitClick).getText();
                                abrirDOC(absolutePath);
                            }
                            BitClick=15000;
                        }
                    }else{
                        //Toast.makeText(getApplicationContext(),"Erro no Download",Toast.LENGTH_LONG).show();
                        if(isInterneServer){
                            pathServer = link;
                        }else{
                            util e = new util();
                            pathServer = e.getUrl() + "documentos/" + name + "/" + filename;
                        }
                        Log.d("ppq",pathServer);
                        DownloadFile(pathServer, "Baixando " + title, description, filename);
                    }
                }
            }
        },filter);
        /**/

        util e = new util();
        if(link == "") {
            pathServer = e.getUrl() + "documentos/" + name + "/" + filename;
            isInterneServer = true;
        }else{
            pathServer = link;
            isInterneServer = false;
        }
        tfilename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fileExists(filename)) {
                    //tfilename.setText("Baixando, aguarde...");
                    //Toast.makeText(getApplicationContext(),"Baixando, aguarde...",Toast.LENGTH_LONG).show();
                    DownloadFile(pathServer, "Baixando " + title, description, filename);
                }else{

                    if(filetype.equals("pdf")){
                        String absolutePath = apath+filename;
                        abrirPDF(absolutePath);
                    }
                    if(filetype.equals("doc") || filetype.equals("docx")){
                        String absolutePath = apath+filename;
                        abrirDOC(absolutePath);
                    }

                    //Log.d("ufpb_dd","filename 1º");

                }
            }
        });

        final List<arquivos> arquivos_baixar = db.getFilesConjuntos(id,id_conjunto);
        for(int i=0;i<arquivos_baixar.size();i++){
            Log.d("ufpb_n",arquivos_baixar.get(i).getFilename());
            links_conjuntos.add(arquivos_baixar.get(i).getLink());
        }
        if(arquivos_baixar.size()>0){
            if(arquivos_baixar.get(0).getFilename() != null){
                for(int i=0;i<arquivos_baixar.size();i++){
                    LinearLayout liw = (LinearLayout)findViewById(R.id.baixar_arquivos);
                    LayoutInflater linflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View customView = linflater.inflate(R.layout.row_arquivo, null);
                    final TextView namej = customView.findViewById(R.id.arquivo_filename);
                    namej.setText(arquivos_baixar.get(i).getFilename());
                    j = i;
                    pp = i;
                    listTextview.add(namej);
                    if(!fileExists(arquivos_baixar.get(j).getFilename())) {
                        namej.setText(arquivos_baixar.get(j).getFilename());
                    }else{
                        namej.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    }
                    Log.d("BitClick",String.valueOf(BitClick));
                    namej.setOnClickListener(DF(i,namej));

                    liw.addView(customView);
                }
            }
        }

        AnalyticsGoogle application = (AnalyticsGoogle) getApplication();
        mTracker = application.getDefaultTracker();

        analytic ana = new analytic("Arquivo: "+title,this);
        ana.registroTela();

        startAds();
    }


    private void startAds(){
        Intent intent_ads = new Intent(this,ads.class);
        intent_ads.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent_ads);
    }

    private View.OnClickListener DF(final int i, final TextView namej) {
        View.OnClickListener o = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util p =new util();
                BitClick = i;

                            String apathServer = "";
                            if(link == ""){
                                apathServer = p.getUrl()+"documentos/"+name+"/"+namej.getText().toString();
                                isInterneServer = true;
                            }else{
                                apathServer = links_conjuntos.get(i);
                                isInterneServer = false;
                            }
                            if(!fileExists(namej.getText().toString())) {
                               // namej.setText("Baixando, aguarde...");
                                DownloadFile(apathServer, "Baixando " + namej.getText().toString(), "", namej.getText().toString());
                            }else{

                                if(filetype.equals("pdf")){
                                    String absolutePath = apath+namej.getText().toString();
                                    abrirPDF(absolutePath);
                                }
                                if(filetype.equals("doc") || filetype.equals("docx")){
                                    String absolutePath = apath+namej.getText().toString();
                                    abrirDOC(absolutePath);
                                }

                               // Log.d("ufpb_dd","filename 2º - "+namej.getText().toString());
                            }
            }
        };
        return o;
    }

    private void abrirPDF(String absolutePath) {
        File file = null;
        file = new File(absolutePath);
        if(file.exists()) {
            Uri file_path = FileProvider.getUriForFile(
                    this,
                    "com.joandeson.ufpbalerta.arquivo",
                    file);
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

    private void DownloadFile(String url, String title, String description,String filename){

        Log.d("ppq",url);

        if(!isOnline()){
            Toast.makeText(getApplicationContext(),"Sem conexão :(",Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getApplicationContext(),"Iniciando o download...",Toast.LENGTH_SHORT).show();

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.INTERNET},
                    0);

            return;
        }

        //Log.d("ufpb_download",url);

        String path = Environment.DIRECTORY_DOWNLOADS+"/ufpb/doc";
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }

        Uri uri = Uri.parse(url);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(title);
        request.setDescription(description);
        request.setDestinationInExternalPublicDir(path,filename);

        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("File")
                .setAction("Clicou em baixar arquivo").build()
        );

    }

    private int getDownloadStatus(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadID);
        DownloadManager downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        if(cursor.moveToFirst()){
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);

            return status;
        }

        return DownloadManager.ERROR_UNKNOWN;
    }

    private boolean fileExists(String filename){

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/ufpb/doc/"+filename;
        File file = new File(path);

        return file.exists();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            util e = new util();
            pathServer = e.getUrl()+"documentos/"+name+"/"+filename;

           DownloadFile(pathServer,title,description,filename);
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

        mTracker.setScreenName("Arquivo: "+title);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }
}
