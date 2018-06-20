package com.kanpurlive.ghufya.kanpur_live;

/**
 * Created by Ghufya on 12/05/2018.
 */


public class Item {

    private String displayName;
    private String email;
    private String uid;
    private String photoUrl1;
    private String photoUrl2;
    private String photoUrl;
    private String photoUrl3;
    private String size;
    private String description;
    private String type;
    private String color;
    private int price;
    private int like;
    private int position;
    private String instanceId;

    public Item() {
    }

    public Item(String displayName, String email, String uid, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
    }

    public Item(String displayName, String photoUrl1, String photoUrl2, String photoUrl3, String size, String description, String type, String color, int price, int like) {
        this.displayName = displayName;
        this.photoUrl1 = photoUrl1;
        this.photoUrl2 = photoUrl2;
        this.photoUrl3 = photoUrl3;
        this.size = size;
        this.description = description;
        this.type = type;
        this.color = color;
        this.price = price;
        this.like = like;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPhotoUrl1() {
        return photoUrl1;
    }

    public void setPhotoUrl1(String photoUrl1) {
        this.photoUrl1 = photoUrl1;
    }

    public String getPhotoUrl2() {
        return photoUrl2;
    }

    public void setPhotoUrl2(String photoUrl2) {
        this.photoUrl2 = photoUrl2;
    }

    public String getPhotoUrl3() {
        return photoUrl3;
    }

    public void setPhotoUrl3(String photoUrl3) {
        this.photoUrl3 = photoUrl3;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
