package com.joandeson.ufpbalerta.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joandeson.ufpbalerta.R;
import com.joandeson.ufpbalerta.model.residence;
import com.joandeson.ufpbalerta.z_residence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JoHN on 12/02/2019.
 */

public class residenceAdapter extends RecyclerView.Adapter<residenceAdapter.myholderview> {

    private Context mContext;
    private List<residence> lst = new ArrayList<>();

    public residenceAdapter(Context mContext, List<residence> lst) {
        this.mContext = mContext;
        this.lst = lst;
    }

    @Override
    public myholderview onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.row_residence,parent,false);
        residenceAdapter.myholderview myholderview = new myholderview(view);

        return myholderview;
    }

    @Override
    public void onBindViewHolder(myholderview holder, final int position) {
        String preco = lst.get(position).getPreco();
        if(!preco.equals("")){
            preco = "R$"+preco;
        }
        holder.titulo.setText(lst.get(position).getTitle());
        holder.data.setText(lst.get(position).getData());
        holder.campus.setText(lst.get(position).getCampus());
        holder.preco.setText(preco);
        if(lst.get(position).getDescricao() != null || !lst.get(position).getDescricao().equals("")) {
            String conteudo = lst.get(position).getDescricao();
            if(conteudo.length() > 150){
                holder.descricao.setText(lst.get(position).getDescricao().substring(0,150)+"...");
            }else {
                if(conteudo.length() > 0) {
                    holder.descricao.setText(lst.get(position).getDescricao());
                }
            }
        }

        holder.residence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnuncio(lst.get(position));
            }
        });

    }

    void viewAnuncio(residence a){
        Intent residence = new Intent(mContext, z_residence.class);
        residence.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        residence.putExtra("titulo",a.getTitle());
        residence.putExtra("descricao",a.getDescricao());
        residence.putExtra("data",a.getData());
        residence.putExtra("campus",a.getCampus());
        residence.putExtra("preco",a.getPreco());
        residence.putExtra("id",a.getId());
        residence.putExtra("id_hash",a.getId_hash());
        residence.putExtra("whatsapp",a.getWhatsapp());
        residence.putExtra("site",a.getSite());
        residence.putExtra("token",a.getToken());
        mContext.startActivity(residence);
    }

    @Override
    public int getItemCount() {
        return lst.size();
    }

    class myholderview extends RecyclerView.ViewHolder{

        private RelativeLayout residence;
        private TextView titulo;
        private TextView descricao;
        private TextView data;
        private TextView campus;
        private TextView preco;

        public myholderview(View itemView) {
            super(itemView);

            residence = (RelativeLayout)itemView.findViewById(R.id.i_residence);
            titulo = (TextView)itemView.findViewById(R.id.titulo_residence);
            descricao = (TextView)itemView.findViewById(R.id.descricao_residence);
            data = (TextView)itemView.findViewById(R.id.data_residence);
            campus = (TextView)itemView.findViewById(R.id.campus_residence);
            preco  = (TextView)itemView.findViewById(R.id.preco_residence);

        }
    }
}
