package com.joandeson.ufpbalerta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.joandeson.ufpbalerta.model.noticia;

/**
 * Created by JoHN on 16/09/2018.
 */

public class dbnoticias extends SQLiteOpenHelper {

    public static final int DATABASE_VERSON = 2;
    public static final String DATABASE_NAME = "dbn.db";
    public static final String TABLE_NAME = "noticias";
    public static final String COLUMN_titulo = "titulo";
    public static final String COLUMN_conteudo = "conteudo";
    public static final String COLUMN_id_hash = "id_hash";
    public static final String COLUMN_data = "data";
    public static final String COLUMN_url = "url";
    public static final String COLUMN_id = "id";
    public static final String COLUMN_favorite = "favorite";
    public static final String COLUMN_views = "views";
    private HashMap hp;
    private Context mContext;

    public dbnoticias(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSON);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(id text,ai integer primary key, titulo text, conteudo text, id_hash text, data text, url text, favorite integer, views integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertNoticia (String titulo, String conteudo, String id_hash, String data,String url,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_titulo, titulo);
        contentValues.put(COLUMN_conteudo, conteudo);
        contentValues.put(COLUMN_id_hash, id_hash);
        contentValues.put(COLUMN_data, data);
        contentValues.put(COLUMN_url, url);
        contentValues.put(COLUMN_id, id);
        contentValues.put(COLUMN_favorite, 0);
        contentValues.put(COLUMN_views, 0);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public List<noticia> search(String query){
        List<noticia> lst = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUMN_titulo+" like '%"+query+"%' order by cast(id as interger) DESC",null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                noticia info = new noticia();
                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));

                lst.add(info);

                cursor.moveToNext();

            }
        }

        return lst;
    }


    public List<noticia> getData(String id) {

        List<noticia> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id='"+id+"' order by cast(id as interger) DESC", null );

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                noticia info = new noticia();
                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }

    public noticia getNoticiaInformations(String id) {

        List<noticia> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id='"+id+"' order by cast(id as interger) DESC", null );

        noticia info = new noticia();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return info;
    }
/*
    public List<noticia> getFilesConjuntos(String id, String id_conjunto) {

        List<noticia> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id!='"+id+"' and id_conjunto='"+id_conjunto+"' and id_conjunto!=''", null );


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                noticia info = new noticia();
                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));

                // Log.d("dblog",info.getNome());

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }
*/
    public List<noticia> getDataCategory(String id) {

        List<noticia> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where name='"+id+"' order by cast(id as interger) DESC", null );


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                noticia info = new noticia();
                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));

                // Log.d("dblog",info.getNome());

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

   /* public boolean updateArquivos (String filename, String name, String title, String data,String description,String filetype,String id,String id_hash, String id_conjunto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_filename, filename);
        contentValues.put(COLUMN_name, name);
        contentValues.put(COLUMN_title, title);
        contentValues.put(COLUMN_data, data);
        contentValues.put(COLUMN_description, description);
        contentValues.put(COLUMN_filetype, filetype);
        contentValues.put(COLUMN_id_hash, id_hash);
        contentValues.put(COLUMN_id_conjunto, id_conjunto);
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        return true;
    }*/

    public Integer deleteArquivo (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[] { id });
    }

    public Boolean deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);

        return true;
    }

    public boolean updateViews(String id, int valor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_views, valor);
        int r = db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        Log.d("update-log","Success: "+String.valueOf(r));
        return true;
    }

    public List<noticia> getAllNoticias() {
        List<noticia> lst = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME+" order by cast(id as interger) DESC limit 50",null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                noticia info = new noticia();
                info.setTitulo(cursor.getString(cursor.getColumnIndex(COLUMN_titulo)));
                info.setConteudo(cursor.getString(cursor.getColumnIndex(COLUMN_conteudo)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_url)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }
}