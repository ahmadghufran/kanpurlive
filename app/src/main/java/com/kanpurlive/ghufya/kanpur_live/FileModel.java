package com.kanpurlive.ghufya.kanpur_live;

/**
 * Created by Ghufya on 07/04/2018.
 */

public class FileModel {
    private String type;
    private String name_file;
    private String size_file;
    private Item item;

    public FileModel() {
    }

    public FileModel(String type, Item item, String name_file, String size_file) {
        this.type = type;
        this.item = item;
        this.name_file = name_file;
        this.size_file = size_file;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName_file() {
        return name_file;
    }

    public void setName_file(String name_file) {
        this.name_file = name_file;
    }

    public String getSize_file() {
        return size_file;
    }

    public void setSize_file(String size_file) {
        this.size_file = size_file;
    }
}
