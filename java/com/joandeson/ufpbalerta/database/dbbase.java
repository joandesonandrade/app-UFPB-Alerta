package com.joandeson.ufpbalerta.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.joandeson.ufpbalerta.model.arquivos;


public class dbbase extends SQLiteOpenHelper {

    public static final int VERSION_DATABASE = 3;
    public static final String DATABASE_NAME = "dbbase.db";
    public static final String TABLE_NAME = "arquivos";
    public static final String COLUMN_filename = "filename";
    public static final String COLUMN_name = "name";
    public static final String COLUMN_title = "title";
    public static final String COLUMN_data = "data";
    public static final String COLUMN_description = "description";
    public static final String COLUMN_filetype = "filetype";
    public static final String COLUMN_id = "id";
    public static final String COLUMN_id_hash = "id_hash";
    public static final String COLUMN_id_conjunto = "id_conjunto";
    public static final String COLUMN_favorite = "favorite";
    public static final String COLUMN_views = "views";
    public static final String COLUMN_link = "link";
    private HashMap hp;
    private Context mContext;

    public dbbase(Context context) {
        super(context, DATABASE_NAME , null, VERSION_DATABASE);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table " + TABLE_NAME +
                        "(id text,ai integer primary key, filename text, name text, title text, data text, description text, filetype text, id_hash text, id_conjunto text, favorite integer, views integer, link text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertArquivo (String filename, String name, String title, String data,String description,String filetype,String id,String id_hash, String id_conjunto, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_filename, filename);
        contentValues.put(COLUMN_name, name);
        contentValues.put(COLUMN_title, title);
        contentValues.put(COLUMN_data, data);
        contentValues.put(COLUMN_description, description);
        contentValues.put(COLUMN_filetype, filetype);
        contentValues.put(COLUMN_id_hash, id_hash);
        contentValues.put(COLUMN_id, id);
        contentValues.put(COLUMN_id_conjunto, id_conjunto);
        contentValues.put(COLUMN_favorite,0);
        contentValues.put(COLUMN_views,0);
        contentValues.put(COLUMN_link,link);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public List<arquivos> search(String query){
        List<arquivos> lst = new ArrayList<>();

        String eq = query;
        eq =eq.replace(" ","-");
        eq =eq.replace("/","AA");
        eq =eq.replace("\\","Aa");
        eq =eq.replace(":","Ab");
        eq =eq.replace("*","AB");
        eq =eq.replace("?","Ba");
        eq =eq.replace(">","BA");
        eq =eq.replace("<","BB");
        eq =eq.replace("|","Ca");

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUMN_title+" like '%"+query+"%' or "+COLUMN_description+" like '%"+query+"%' or "+COLUMN_filename+" like '%"+eq+"%' or "+COLUMN_name+" like '%"+eq+"%' order by cast(id as interger) DESC limit 100",null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);

                cursor.moveToNext();

            }
        }

        return lst;
    }

    public arquivos getFile(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id='"+id+"' order by cast(id as interger) DESC", null );

        List<arquivos> lst = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);
            }
        }
        return lst.get(0);
    }


    public List<arquivos> getData(String id) {

        List<arquivos> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id='"+id+"' order by cast(id as interger) DESC", null );

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }

    public arquivos getFileInformations(String filename) {

        List<arquivos> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where filename='"+filename+"' order by cast(id as interger) DESC", null );

        arquivos info = new arquivos();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {


                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return info;
    }

    public List<arquivos> getFilesConjuntos(String id, String id_conjunto) {

        List<arquivos> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where id!='"+id+"' and id_conjunto='"+id_conjunto+"' and id_conjunto!='' order by cast(id as interger) DESC", null );


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                // Log.d("dblog",info.getNome());

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }

    public List<arquivos> getDataCategory(String id) {

        List<arquivos> lst = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+TABLE_NAME+" where name='"+id+"' order by id DESC", null );


        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

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

    public boolean updateArquivos (String filename, String name, String title, String data,String description,String filetype,String id,String id_hash, String id_conjunto) {
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
    }

    public boolean updateViews(String id, int valor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_views, valor);
        int r = db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        Log.d("update-log","Success: "+String.valueOf(r));
        return true;
    }

    public boolean updateFavorite(String id, int valor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_favorite, valor);
        int r = db.update(TABLE_NAME, contentValues, "id = ? ", new String[] { id } );
        Log.d("update-log","Success: "+String.valueOf(r));
        return true;
    }

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

    public List<arquivos> getAllArquivos() {
        List<arquivos> lst = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME+" order by cast(id as interger) DESC limit 100",null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }

    public List<arquivos> getAllArquivosFavoritos() {
        List<arquivos> lst = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor  cursor = db.rawQuery("select * from "+TABLE_NAME+" where "+COLUMN_favorite+"=1 order by cast(id as interger) DESC",null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {

                arquivos info = new arquivos();
                info.setFilename(cursor.getString(cursor.getColumnIndex(COLUMN_filename)));
                info.setName(cursor.getString(cursor.getColumnIndex(COLUMN_name)));
                info.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_title)));
                info.setData(cursor.getString(cursor.getColumnIndex(COLUMN_data)));
                info.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_description)));
                info.setFiletype(cursor.getString(cursor.getColumnIndex(COLUMN_filetype)));
                info.setId(cursor.getString(cursor.getColumnIndex(COLUMN_id)));
                info.setId_hash(cursor.getString(cursor.getColumnIndex(COLUMN_id_hash)));
                info.setId_conjunto(cursor.getString(cursor.getColumnIndex(COLUMN_id_conjunto)));
                info.setFavorite(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_favorite))));
                info.setViews(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_views))));
                info.setLink(cursor.getString(cursor.getColumnIndex(COLUMN_link)));

                lst.add(info);

                cursor.moveToNext();

            }
        }
        return lst;
    }
}