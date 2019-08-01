package com.joandeson.ufpbalerta.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joandeson.ufpbalerta.R;
import com.joandeson.ufpbalerta.arquivo;
import com.joandeson.ufpbalerta.database.dbbase;
import com.joandeson.ufpbalerta.model.arquivos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JoHN on 12/09/2018.
 */

public class arquivosAdapter extends RecyclerView.Adapter<arquivosAdapter.myHolder>{

    private Context mContext;
    private List<arquivos> lst = new ArrayList<>();
    private dbbase database;
    private int lastPosition;
    //private Drawable img;

    private int favorite = 0;
    private int _favorite = 0;

    public arquivosAdapter(Context mContext, List<arquivos> lst){
        this.mContext = mContext;
        this.lst = lst;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        database = new dbbase(mContext);

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_lista_arquivo,parent,false);
        final arquivosAdapter.myHolder viewholder = new arquivosAdapter.myHolder(view);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(final myHolder holder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.item_animation_fall_down
                        : R.anim.item_animation_fall_down);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        holder.filename.setText(lst.get(position).getTitle().replace(":8er5:","\""));
        holder.nome.setText(lst.get(position).getName().toUpperCase().replace(":8er5:","\""));
        holder._data.setText(lst.get(position).getData());

        if(lst.get(position).getDescription() != null || !lst.get(position).getDescription().equals("")) {
            String conteudo = lst.get(position).getDescription().replace(":8er5:","\"");
            if(conteudo.length() > 150){
                holder.description.setText(lst.get(position).getDescription().replace(":8er5:","\"").substring(0,150)+"...");
            }else {
                if(conteudo.length() > 0) {
                    holder.description.setText(lst.get(position).getDescription().replace(":8er5:","\""));
                }
            }
        }


        if(lst.get(position).getViews() > 0){
            holder.novo.setVisibility(View.INVISIBLE);
        }else{
            holder.novo.setVisibility(View.VISIBLE);
        }

        if(lst.get(position).getFavorite() > 0){
            holder.favorite.setBackgroundResource(R.drawable.ic_favorite_enabled);
        }else{
            holder.favorite.setBackgroundResource(R.drawable.ic_favorite_disabled);
        }

        Log.d("view-arquivo","id:"+lst.get(position).getId()+" position: "+String.valueOf(position)+" views: "+String.valueOf(lst.get(position).getViews()));

        /*if(lst.get(position).getData().equals("") || lst.get(position).getData()==null){
            holder.novo.setVisibility(View.GONE);
        }else{
            holder.novo.setVisibility(View.VISIBLE);
        }*/

        favorite = lst.get(position).getFavorite();
        _favorite = favorite;


        //img = mContext.getResources().getDrawable( R.drawable.ic_favorite_enabled );
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbbase db = new dbbase(mContext);
                if(lst.get(position).getFavorite() > 0){
                    holder.favorite.setBackgroundResource(R.drawable.ic_favorite_disabled);
                    _favorite = 0;
                    db.updateFavorite(lst.get(position).getId(),_favorite);
                }else{
                    holder.favorite.setBackgroundResource(R.drawable.ic_favorite_enabled);
                    _favorite = 1;
                    db.updateFavorite(lst.get(position).getId(),_favorite);
                }
                lst.get(position).setFavorite(_favorite);
            }
        });

        holder.arquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(mContext,arquivo.class);
                i.putExtra("id_arquivo",lst.get(position).getId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                //update views file
                int view = 1;
                if(database.updateViews(lst.get(position).getId(),view)){
                    Log.d("update-views","views atualizados: "+view+"/"+lst.get(position).getViews());
                    holder.novo.setVisibility(View.INVISIBLE);
                }
                //

                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    class myHolder extends RecyclerView.ViewHolder{

        private TextView filename;
        private TextView description;
        private TextView nome;
        private TextView novo;
        private RelativeLayout arquivo;
        private TextView _data;
        private Button favorite;

        public myHolder(View itemView) {
            super(itemView);

            filename        = (TextView)itemView.findViewById(R.id.titulo_arquivo);
            description     = (TextView)itemView.findViewById(R.id.descricao_arquivo);
            nome            = (TextView)itemView.findViewById(R.id.nome_arquivo);
            novo            = (TextView)itemView.findViewById(R.id.novo);
            arquivo         = (RelativeLayout)itemView.findViewById(R.id.arquivo);
            _data           = (TextView)itemView.findViewById(R.id._data);
            favorite        = (Button)itemView.findViewById(R.id.file_favorite);

        }
    }

}
