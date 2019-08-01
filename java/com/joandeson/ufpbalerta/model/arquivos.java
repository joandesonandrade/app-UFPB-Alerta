package com.joandeson.ufpbalerta.model;

/**
 * Created by JoHN on 12/09/2018.
 */

public class arquivos {

    public String filename;
    public String name;
    public String title;
    public String description;
    public String filetype;
    public String id;
    public String id_hash;
    public String id_conjunto;
    public String data;
    public int favorite;
    public int views;
    public String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getFilename() {
        return filename;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_hash() {
        return id_hash;
    }

    public void setId_hash(String id_hash) {
        this.id_hash = id_hash;
    }

    public String getId_conjunto() {
        return id_conjunto;
    }

    public void setId_conjunto(String id_conjunto) {
        this.id_conjunto = id_conjunto;
    }
}
