package com.joandeson.ufpbalerta.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joandeson.ufpbalerta.R;
import com.joandeson.ufpbalerta.database.dbnoticias;
import com.joandeson.ufpbalerta.model.noticia;
import com.joandeson.ufpbalerta.z_noticia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JoHN on 12/09/2018.
 */

public class noticiaAdapter extends RecyclerView.Adapter<noticiaAdapter.myHolder>{

    private Context mContext;
    private List<noticia> lst = new ArrayList<>();
    private int lastPosition;

    public noticiaAdapter(Context mContext, List<noticia> lst){
        this.mContext = mContext;
        this.lst = lst;
    }

    @Override
    public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_noticia,parent,false);
        final noticiaAdapter.myHolder viewholder = new noticiaAdapter.myHolder(view);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(final myHolder holder, final int position) {

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.item_animation_fall_down
                        : R.anim.item_animation_fall_down);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

        holder.titulo_noticia.setText(lst.get(position).getTitulo().replace(":8er5:","\""));
        holder.data_noticia.setText(lst.get(position).getData());

        if(lst.get(position).getConteudo() != null || !lst.get(position).getConteudo().equals("")) {
            String conteudo = lst.get(position).getConteudo().replace(":8er5:","\"");
            if(conteudo.length() > 150){
                holder.descricao_noticia.setText(lst.get(position).getConteudo().replace(":8er5:","\"").substring(0,150)+"...");
            }else {
                if(conteudo.length() > 0) {
                    holder.descricao_noticia.setText(lst.get(position).getConteudo().replace(":8er5:","\""));
                }
            }
        }

        /*if(lst.get(position).getData().equals("") || lst.get(position).getData()==null){
            holder.novo_noticia.setVisibility(View.GONE);
        }else{
            holder.novo_noticia.setVisibility(View.VISIBLE);
        }*/

        holder.i_noticia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, z_noticia.class);
                intent.putExtra("id_noticia",lst.get(position).getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                holder.novo_noticia.setVisibility(View.INVISIBLE);

                /**/
                dbnoticias db = new dbnoticias(mContext);
                db.updateViews(lst.get(position).getId(),1);
                /**/

                mContext.startActivity(intent);
            }
        });

        if(lst.get(position).getViews() > 0){
            holder.novo_noticia.setVisibility(View.INVISIBLE);
        }else{
            holder.novo_noticia.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    class myHolder extends RecyclerView.ViewHolder{

        private TextView titulo_noticia;
        private TextView data_noticia;
        private TextView descricao_noticia;
        private TextView novo_noticia;
        private RelativeLayout i_noticia;

        public myHolder(View itemView) {
            super(itemView);

            titulo_noticia      = (TextView)itemView.findViewById(R.id.titulo_noticia);
            data_noticia        = (TextView)itemView.findViewById(R.id.data_noticia);
            descricao_noticia   = (TextView)itemView.findViewById(R.id.descricao_noticia);
            novo_noticia        = (TextView)itemView.findViewById(R.id.novo_noticia);
            i_noticia           = (RelativeLayout)itemView.findViewById(R.id.i_noticia);

        }
    }

}
