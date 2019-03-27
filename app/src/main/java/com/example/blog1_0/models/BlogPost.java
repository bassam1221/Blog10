package com.example.blog1_0.models;

public class BlogPost extends BlogPostID{
    public String itemName,imageUrl,desc,user_id,timestamp, dateTo;
    public Double price;
    public BlogPost(){

    }

    public BlogPost(String name, String imageUrl, String desc, String user_id, Double price, String Timestamp,String dateTo) {
        this.user_id=user_id;
        this.itemName = name;
        this.imageUrl = imageUrl;
        this.desc = desc;
        this.price = price;
        this.timestamp = Timestamp;
        this.dateTo=dateTo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser_id(String user_id) { this.user_id = user_id; }

    public void setName(String name) {
        this.itemName = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDesc(String des) {
        this.desc = des;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDateTo(String dateTo) { this.dateTo = dateTo; }



    public String getName() {
        return itemName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public Double getPrice() {
        return price;
    }

    public String getUser_id() { return user_id; }

    public String getDateTo() { return dateTo; }

}
