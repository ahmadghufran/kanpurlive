package com.kanpurlive.ghufya.kanpur_live;

/**
 * Created by Ghufya on 12/05/2018.
 */


public class Shop {

    private String displayName;
    private String email;
    private String uid;
    private String photoUrl;

    private String instanceId;

    public Shop() {
    }

    public Shop(String displayName, String email, String uid, String photoUrl) {
        this.displayName = displayName;
        this.email = email;
        this.uid = uid;
        this.photoUrl = photoUrl;
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
