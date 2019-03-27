package com.example.blog1_0.models;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostID {
    @Exclude
    public String BlogPostID;
    public <T extends BlogPostID> T withId(@NonNull final String id) {
        this.BlogPostID = id;
        return (T) this;
    }

}
