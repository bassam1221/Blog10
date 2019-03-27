package com.example.blog1_0.models;

public class FollowMarket {
    String image ,name ,privelege,user_id;

    public  FollowMarket(){

    }

    public FollowMarket(String image, String name, String privelege ,String user_id) {
        this.image = image;
        this.name = name;
        this.privelege = privelege;
        this.user_id=user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrivelege() {
        return privelege;
    }

    public void setPrivelege(String privelege) {
        this.privelege = privelege;
    }
}
