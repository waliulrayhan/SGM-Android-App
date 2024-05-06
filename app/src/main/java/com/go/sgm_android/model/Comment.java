package com.go.sgm_android.model;

public class Comment {
    private String comment;

    public Comment() {
        // Default constructor required for Firebase
    }

    public Comment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
