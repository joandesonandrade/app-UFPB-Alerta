package com.joandeson.ufpbalerta;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class sobreUser extends AppCompatActivity {

    private String nome;
    private String sobre;
    private String link;
    private String id;

    private TextView u_sobre;
    private Button   u_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre_user);

        nome  = getIntent().getExtras().getString("nome");
        sobre = getIntent().getExtras().getString("sobre");
        link  = getIntent().getExtras().getString("link");
        id    = getIntent().getExtras().getString("id");

        getSupportActionBar().setTitle(nome);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        u_sobre = (TextView)findViewById(R.id.u_sobre);
        u_link  = (Button)findViewById(R.id.u_site);

        u_sobre.setText(sobre);

        if(link == ""){
            u_link.setVisibility(View.GONE);
        }

        u_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenUrl(link);
            }
        });

    }

    private void OpenUrl(String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
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
